package com.pgmanagement.kafka;

import com.pgmanagement.entity.Booking;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BookingEventListener {
    
    @KafkaListener(topics = "booking-events", groupId = "pg-management-group")
    public void handleBookingEvent(Booking booking) {
        log.info("Received booking event: {}", booking.getId());
        
        switch (booking.getStatus()) {
            case REQUESTED:
                log.info("New booking request received for PG: {}", booking.getPg().getName());
                // Send notification to PG owner
                break;
            case APPROVED:
                log.info("Booking approved for user: {}", booking.getUser().getEmail());
                // Send notification to customer
                break;
            case REJECTED:
                log.info("Booking rejected for user: {}. Reason: {}", 
                    booking.getUser().getEmail(), booking.getRejectionReason());
                // Send notification to customer
                break;
            case PAYMENT_PENDING:
                log.info("Payment pending for booking: {}", booking.getId());
                // Send payment reminder
                break;
            case PAYMENT_COMPLETED:
                log.info("Payment completed for booking: {}", booking.getId());
                // Send confirmation and create tenant record
                break;
            case COMPLETED:
                log.info("Booking completed: {}", booking.getId());
                break;
            case CANCELLED:
                log.info("Booking cancelled: {}", booking.getId());
                break;
            default:
                log.warn("Unknown booking status: {}", booking.getStatus());
        }
    }
}
