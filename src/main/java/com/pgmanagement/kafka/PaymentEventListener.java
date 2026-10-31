package com.pgmanagement.kafka;

import com.pgmanagement.entity.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaymentEventListener {
    
    @KafkaListener(topics = "payment-events", groupId = "pg-management-group")
    public void handlePaymentEvent(Payment payment) {
        log.info("Received payment event: {}", payment.getId());
        
        switch (payment.getStatus()) {
            case PENDING:
                log.info("Payment pending: ₹{} for PG: {}", 
                    payment.getAmount(), payment.getPg().getName());
                break;
            case COMPLETED:
                log.info("Payment completed: ₹{} by tenant: {}", 
                    payment.getAmount(), payment.getTenant().getName());
                // Send receipt to tenant
                break;
            case FAILED:
                log.error("Payment failed: ₹{} for tenant: {}", 
                    payment.getAmount(), payment.getTenant().getName());
                // Send notification to tenant and owner
                break;
            case OVERDUE:
                log.warn("Payment overdue: ₹{} for tenant: {}", 
                    payment.getAmount(), payment.getTenant().getName());
                // Send overdue alert
                break;
            default:
                log.warn("Unknown payment status: {}", payment.getStatus());
        }
    }
    
    @KafkaListener(topics = "payment-alerts", groupId = "pg-management-group")
    public void handlePaymentAlert(Payment payment) {
        log.warn("Payment alert: Overdue payment ₹{} for tenant {} in PG {}", 
            payment.getAmount(), 
            payment.getTenant().getName(), 
            payment.getPg().getName());
        
        // Send email/SMS notification to tenant
        // Send notification to PG owner
    }
}
