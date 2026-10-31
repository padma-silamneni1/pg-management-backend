package com.pgmanagement.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class PgRegistrationDto {
    
    @NotBlank(message = "PG name is required")
    private String name;
    
    @NotBlank(message = "Address is required")
    private String address;
    
    @NotBlank(message = "City is required")
    private String city;
    
    @NotBlank(message = "State is required")
    private String state;
    
    @NotBlank(message = "Pincode is required")
    @Pattern(regexp = "^\\d{6}$", message = "Pincode must be 6 digits")
    private String pincode;
    
    @NotNull(message = "Number of floors is required")
    @Min(value = 1, message = "Minimum 1 floor required")
    private Integer numberOfFloors;
    
    @NotNull(message = "Total rooms is required")
    @Min(value = 1, message = "Minimum 1 room required")
    private Integer totalRooms;
    
    @NotNull(message = "Total beds is required")
    @Min(value = 1, message = "Minimum 1 bed required")
    private Integer totalBeds;
    
    @NotNull(message = "Rent per bed is required")
    @DecimalMin(value = "0.01", message = "Rent must be greater than 0")
    private Double rentPerBed;
    
    @NotBlank(message = "Owner name is required")
    private String ownerName;
    
    @NotBlank(message = "Owner email is required")
    @Email(message = "Invalid email format")
    private String ownerEmail;
    
    @NotBlank(message = "Owner phone is required")
    @Pattern(regexp = "^\\d{10}$", message = "Phone must be 10 digits")
    private String ownerPhone;
    
    private String amenities;
    
    private String description;
}
