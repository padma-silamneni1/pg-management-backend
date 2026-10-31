package com.pgmanagement.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class RoomDto {
    
    private Long id;
    
    @NotNull(message = "Floor number is required")
    @Min(value = 1, message = "Floor must be at least 1")
    private Integer floorNumber;
    
    @NotBlank(message = "Room number is required")
    private String roomNumber;
    
    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;
    
    @NotNull(message = "Available beds is required")
    @Min(value = 0, message = "Available beds cannot be negative")
    private Integer availableBeds;
    
    @NotBlank(message = "Room type is required")
    private String roomType;
    
    private String amenities;
    
    private Boolean isActive;
}
