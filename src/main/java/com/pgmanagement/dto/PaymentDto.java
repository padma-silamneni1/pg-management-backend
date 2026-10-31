package com.pgmanagement.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PaymentDto {
    
    private Long id;
    
    private Double amount;
    
    private LocalDate paymentDate;
    
    private LocalDate dueDate;
    
    private String paymentType;
    
    private String status;
    
    private String transactionId;
    
    private String notes;
    
    private Long bookingId;
    
    private Long tenantId;
    
    private Long pgId;
}
