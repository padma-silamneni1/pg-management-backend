package com.pgmanagement.kafka;

import com.pgmanagement.entity.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentEventProducer {
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    public void sendPaymentEvent(Payment payment) {
        log.info("Sending payment event: {}", payment.getId());
        kafkaTemplate.send("payment-events", payment);
    }
    
    public void sendOverduePaymentAlert(Payment payment) {
        log.info("Sending overdue payment alert for tenant: {}", payment.getTenant().getName());
        kafkaTemplate.send("payment-alerts", payment);
    }
}
