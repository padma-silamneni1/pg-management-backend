package com.pgmanagement.dto;

import lombok.Data;
import java.util.List;

@Data
public class StatisticsDto {
    
    private Long totalRooms;
    
    private Long occupiedRooms;
    
    private Long availableRooms;
    
    private Long totalBeds;
    
    private Long occupiedBeds;
    
    private Long availableBeds;
    
    private Long totalTenants;
    
    private Double totalMonthlyRevenue;
    
    private Double pendingPayments;
    
    private Double overduePayments;
    
    private List<TenantPaymentInfo> overdueTenants;
    
    @Data
    public static class TenantPaymentInfo {
        private String tenantName;
        private String roomNumber;
        private String bedNumber;
        private Double pendingAmount;
        private Integer overdueDays;
        private String phone;
    }
}
