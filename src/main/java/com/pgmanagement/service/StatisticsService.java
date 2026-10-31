package com.pgmanagement.service;

import com.pgmanagement.dto.ResponseDto;
import com.pgmanagement.dto.StatisticsDto;
import com.pgmanagement.entity.Payment;
import com.pgmanagement.entity.Tenant;
import com.pgmanagement.repository.PaymentRepository;
import com.pgmanagement.repository.RoomRepository;
import com.pgmanagement.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    
    private final RoomRepository roomRepository;
    private final TenantRepository tenantRepository;
    private final PaymentRepository paymentRepository;
    
    public ResponseDto getStatistics(Long pgId) {
        List<com.pgmanagement.entity.Room> rooms = roomRepository.findByPgId(pgId);
        List<Tenant> activeTenants = tenantRepository.findActiveTenantsByPgId(pgId);
        List<Payment> overduePayments = paymentRepository.findOverduePaymentsByPgId(pgId, LocalDate.now());
        
        StatisticsDto stats = new StatisticsDto();
        
        // Room statistics
        stats.setTotalRooms((long) rooms.size());
        stats.setOccupiedRooms(rooms.stream().filter(r -> r.getOccupiedBeds() > 0).count());
        stats.setAvailableRooms(rooms.stream().filter(r -> r.getAvailableBeds() > 0).count());
        
        // Bed statistics
        stats.setTotalBeds(rooms.stream().mapToInt(com.pgmanagement.entity.Room::getCapacity).sum());
        stats.setOccupiedBeds(rooms.stream().mapToInt(com.pgmanagement.entity.Room::getOccupiedBeds).sum());
        stats.setAvailableBeds(rooms.stream().mapToInt(com.pgmanagement.entity.Room::getAvailableBeds).sum());
        
        // Tenant statistics
        stats.setTotalTenants((long) activeTenants.size());
        
        // Revenue calculations
        double monthlyRevenue = activeTenants.stream()
                .mapToDouble(Tenant::getMonthlyRent)
                .sum();
        stats.setTotalMonthlyRevenue(monthlyRevenue);
        
        // Payment statistics
        double pendingPayments = overduePayments.stream()
                .filter(p -> p.getStatus() == Payment.PaymentStatus.PENDING)
                .mapToDouble(Payment::getAmount)
                .sum();
        stats.setPendingPayments(pendingPayments);
        
        double overdueAmount = overduePayments.stream()
                .mapToDouble(Payment::getAmount)
                .sum();
        stats.setOverduePayments(overdueAmount);
        
        // Overdue tenants details
        List<StatisticsDto.TenantPaymentInfo> overdueTenants = overduePayments.stream()
                .map(payment -> {
                    Tenant tenant = payment.getTenant();
                    if (tenant == null) return null;
                    
                    StatisticsDto.TenantPaymentInfo info = new StatisticsDto.TenantPaymentInfo();
                    info.setTenantName(tenant.getName());
                    info.setRoomNumber(tenant.getRoom().getRoomNumber());
                    info.setBedNumber(tenant.getBed().getBedNumber());
                    info.setPendingAmount(payment.getAmount());
                    info.setOverdueDays((int) ChronoUnit.DAYS.between(payment.getDueDate(), LocalDate.now()));
                    info.setPhone(tenant.getPhone());
                    return info;
                })
                .filter(info -> info != null)
                .collect(Collectors.toList());
        
        stats.setOverdueTenants(overdueTenants);
        
        return ResponseDto.success("Statistics retrieved successfully", stats);
    }
    
    public ResponseDto getPaymentHistory(Long pgId, Long tenantId) {
        List<Payment> payments = paymentRepository.findPaymentHistoryByTenantId(tenantId);
        return ResponseDto.success("Payment history retrieved successfully", payments);
    }
    
    public ResponseDto getPaymentsByDateRange(Long pgId, LocalDate startDate, LocalDate endDate) {
        List<Payment> payments = paymentRepository.findPaymentsByPgIdAndDateRange(pgId, startDate, endDate);
        return ResponseDto.success("Payments retrieved successfully", payments);
    }
}
