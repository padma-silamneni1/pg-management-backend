package com.pgmanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pgs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pg {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String address;
    
    @Column(nullable = false)
    private String city;
    
    @Column(nullable = false)
    private String state;
    
    @Column(nullable = false)
    private String pincode;
    
    @Column(nullable = false)
    private Integer numberOfFloors;
    
    @Column(nullable = false)
    private Integer totalRooms;
    
    @Column(nullable = false)
    private Integer totalBeds;
    
    @Column(nullable = false)
    private Integer availableBeds;
    
    @Column(nullable = false)
    private Double rentPerBed;
    
    @Column(nullable = false)
    private String ownerName;
    
    @Column(nullable = false, unique = true)
    private String ownerEmail;
    
    @Column(nullable = false)
    private String ownerPhone;
    
    @Column(length = 1000)
    private String amenities;
    
    @Column(length = 1000)
    private String description;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @OneToMany(mappedBy = "pg", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Room> rooms = new ArrayList<>();
    
    @OneToMany(mappedBy = "pg", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Booking> bookings = new ArrayList<>();
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
