package com.pgmanagement.repository;

import com.pgmanagement.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    List<Payment> findByBookingId(Long bookingId);
    
    List<Payment> findByTenantId(Long tenantId);
    
    List<Payment> findByPgId(Long pgId);
    
    List<Payment> findByPgIdAndStatus(Long pgId, Payment.PaymentStatus status);
    
    @Query("SELECT p FROM Payment p WHERE p.pg.id = :pgId AND p.paymentType = 'MONTHLY_RENT' AND p.dueDate < :currentDate AND p.status = 'PENDING'")
    List<Payment> findOverduePaymentsByPgId(@Param("pgId") Long pgId, @Param("currentDate") LocalDate currentDate);
    
    @Query("SELECT p FROM Payment p WHERE p.pg.id = :pgId AND p.paymentDate >= :startDate AND p.paymentDate <= :endDate")
    List<Payment> findPaymentsByPgIdAndDateRange(@Param("pgId") Long pgId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT p FROM Payment p WHERE p.tenant.id = :tenantId ORDER BY p.paymentDate DESC")
    List<Payment> findPaymentHistoryByTenantId(@Param("tenantId") Long tenantId);
}
