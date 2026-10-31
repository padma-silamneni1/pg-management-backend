package com.pgmanagement.repository;

import com.pgmanagement.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    List<Booking> findByUserId(Long userId);
    
    List<Booking> findByPgId(Long pgId);
    
    List<Booking> findByPgIdAndStatus(Long pgId, Booking.BookingStatus status);
    
    Optional<Booking> findByIdAndUserId(Long id, Long userId);
    
    @Query("SELECT b FROM Booking b WHERE b.pg.id = :pgId AND b.status IN ('REQUESTED', 'APPROVED', 'PAYMENT_PENDING')")
    List<Booking> findPendingBookingsByPgId(@Param("pgId") Long pgId);
    
    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.status = 'PAYMENT_PENDING'")
    List<Booking> findPaymentPendingBookingsByUserId(@Param("userId") Long userId);
}
