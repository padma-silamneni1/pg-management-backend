package com.pgmanagement.service;

import com.pgmanagement.dto.BookingRequestDto;
import com.pgmanagement.dto.ResponseDto;
import com.pgmanagement.entity.*;
import com.pgmanagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingService {
    
    private final BookingRepository bookingRepository;
    private final PgRepository pgRepository;
    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;
    private final UserRepository userRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    public ResponseDto requestBooking(Long userId, BookingRequestDto dto) {
        var pgOpt = pgRepository.findById(dto.getPgId());
        if (pgOpt.isEmpty()) {
            return ResponseDto.error("PG not found");
        }
        
        Pg pg = pgOpt.get();
        
        if (pg.getAvailableBeds() <= 0) {
            return ResponseDto.error("No beds available in this PG");
        }
        
        var userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseDto.error("User not found");
        }
        
        User user = userOpt.get();
        
        // Calculate total amount
        double monthlyRent = pg.getRentPerBed();
        double securityDeposit = monthlyRent; // Security deposit equals one month rent
        double totalAmount;
        
        if (dto.getIsMonthlyPayment()) {
            totalAmount = securityDeposit + monthlyRent; // Deposit + first month
        } else {
            totalAmount = securityDeposit + (monthlyRent * dto.getDurationMonths());
        }
        
        Booking booking = Booking.builder()
                .user(user)
                .pg(pg)
                .requestedDate(LocalDate.now())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .durationMonths(dto.getDurationMonths())
                .isMonthlyPayment(dto.getIsMonthlyPayment())
                .totalAmount(totalAmount)
                .securityDeposit(securityDeposit)
                .status(Booking.BookingStatus.REQUESTED)
                .build();
        
        if (dto.getRoomId() != null) {
            roomRepository.findById(dto.getRoomId()).ifPresent(booking::setRoom);
        }
        
        if (dto.getBedId() != null) {
            bedRepository.findById(dto.getBedId()).ifPresent(booking::setBed);
        }
        
        booking = bookingRepository.save(booking);
        
        // Send Kafka event
        kafkaTemplate.send("booking-events", booking);
        
        return ResponseDto.success("Booking request submitted successfully", booking);
    }
    
    public ResponseDto approveBooking(Long bookingId, String ownerEmail) {
        var bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            return ResponseDto.error("Booking not found");
        }
        
        Booking booking = bookingOpt.get();
        
        if (!booking.getPg().getOwnerEmail().equals(ownerEmail)) {
            return ResponseDto.error("You are not authorized to approve this booking");
        }
        
        if (booking.getStatus() != Booking.BookingStatus.REQUESTED) {
            return ResponseDto.error("Booking cannot be approved in current status");
        }
        
        booking.setStatus(Booking.BookingStatus.APPROVED);
        booking = bookingRepository.save(booking);
        
        // Send Kafka event
        kafkaTemplate.send("booking-events", booking);
        
        return ResponseDto.success("Booking approved successfully", booking);
    }
    
    public ResponseDto rejectBooking(Long bookingId, String ownerEmail, String reason) {
        var bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            return ResponseDto.error("Booking not found");
        }
        
        Booking booking = bookingOpt.get();
        
        if (!booking.getPg().getOwnerEmail().equals(ownerEmail)) {
            return ResponseDto.error("You are not authorized to reject this booking");
        }
        
        if (booking.getStatus() != Booking.BookingStatus.REQUESTED) {
            return ResponseDto.error("Booking cannot be rejected in current status");
        }
        
        booking.setStatus(Booking.BookingStatus.REJECTED);
        booking.setRejectionReason(reason);
        booking = bookingRepository.save(booking);
        
        // Send Kafka event
        kafkaTemplate.send("booking-events", booking);
        
        return ResponseDto.success("Booking rejected successfully", booking);
    }
    
    public ResponseDto initiatePayment(Long bookingId, Long userId) {
        var bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            return ResponseDto.error("Booking not found");
        }
        
        Booking booking = bookingOpt.get();
        
        if (!booking.getUser().getId().equals(userId)) {
            return ResponseDto.error("You are not authorized to initiate payment for this booking");
        }
        
        if (booking.getStatus() != Booking.BookingStatus.APPROVED) {
            return ResponseDto.error("Booking is not approved yet");
        }
        
        booking.setStatus(Booking.BookingStatus.PAYMENT_PENDING);
        booking = bookingRepository.save(booking);
        
        return ResponseDto.success("Payment initiated. Please complete the payment.", booking);
    }
    
    public ResponseDto completePayment(Long bookingId, String transactionId) {
        var bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            return ResponseDto.error("Booking not found");
        }
        
        Booking booking = bookingOpt.get();
        
        if (booking.getStatus() != Booking.BookingStatus.PAYMENT_PENDING) {
            return ResponseDto.error("Payment is not pending for this booking");
        }
        
        // Create tenant record
        Tenant tenant = createTenantFromBooking(booking);
        
        // Update booking status
        booking.setStatus(Booking.BookingStatus.PAYMENT_COMPLETED);
        bookingRepository.save(booking);
        
        // Send Kafka event
        kafkaTemplate.send("booking-events", booking);
        
        return ResponseDto.success("Payment completed successfully", tenant);
    }
    
    private Tenant createTenantFromBooking(Booking booking) {
        // Assign bed if not already assigned
        Bed bed = booking.getBed();
        if (bed == null) {
            List<Bed> availableBeds = bedRepository.findAvailableBedsByPgId(booking.getPg().getId());
            if (availableBeds.isEmpty()) {
                throw new RuntimeException("No beds available");
            }
            bed = availableBeds.get(0);
        }
        
        // Mark bed as occupied
        bed.setIsAvailable(false);
        bedRepository.save(bed);
        
        // Update room
        Room room = bed.getRoom();
        room.setOccupiedBeds(room.getOccupiedBeds() + 1);
        room.setAvailableBeds(room.getAvailableBeds() - 1);
        roomRepository.save(room);
        
        // Update PG
        Pg pg = booking.getPg();
        pg.setAvailableBeds(pg.getAvailableBeds() - 1);
        pgRepository.save(pg);
        
        // Create tenant
        Tenant tenant = Tenant.builder()
                .user(booking.getUser())
                .pg(pg)
                .room(room)
                .bed(bed)
                .name(booking.getUser().getName())
                .phone(booking.getUser().getPhone())
                .email(booking.getUser().getEmail())
                .checkInDate(booking.getStartDate())
                .monthlyRent(pg.getRentPerBed())
                .securityDeposit(booking.getSecurityDeposit())
                .status(Tenant.TenantStatus.ACTIVE)
                .build();
        
        return tenant;
    }
    
    public ResponseDto getBookingsByUserId(Long userId) {
        List<Booking> bookings = bookingRepository.findByUserId(userId);
        return ResponseDto.success("Bookings retrieved successfully", bookings);
    }
    
    public ResponseDto getPendingBookingsByPgId(Long pgId) {
        List<Booking> bookings = bookingRepository.findPendingBookingsByPgId(pgId);
        return ResponseDto.success("Pending bookings retrieved successfully", bookings);
    }
    
    public ResponseDto getPaymentPendingBookingsByUserId(Long userId) {
        List<Booking> bookings = bookingRepository.findPaymentPendingBookingsByUserId(userId);
        return ResponseDto.success("Payment pending bookings retrieved successfully", bookings);
    }
}
