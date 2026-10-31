package com.pgmanagement.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Data
public class BookingRequestDto {
    
    @NotNull(message = "PG ID is required")
    private Long pgId;
    
    private Long roomId;
    
    private Long bedId;
    
    @NotNull(message = "Start date is required")
    @Future(message = "Start date must be in the future")
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Minimum 1 month required")
    private Integer durationMonths;
    
    @NotNull(message = "Monthly payment preference is required")
    private Boolean isMonthlyPayment;
}
