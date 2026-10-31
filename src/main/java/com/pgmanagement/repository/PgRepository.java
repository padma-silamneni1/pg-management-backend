package com.pgmanagement.repository;

import com.pgmanagement.entity.Pg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PgRepository extends JpaRepository<Pg, Long> {
    
    Optional<Pg> findByOwnerEmail(String ownerEmail);
    
    List<Pg> findByCity(String city);
    
    List<Pg> findByCityAndState(String city, String state);
    
    @Query("SELECT p FROM Pg p WHERE p.city = :city AND p.availableBeds > 0")
    List<Pg> findAvailablePgsByCity(@Param("city") String city);
    
    @Query("SELECT p FROM Pg p WHERE p.state = :state AND p.availableBeds > 0")
    List<Pg> findAvailablePgsByState(@Param("state") String state);
    
    @Query("SELECT p FROM Pg p WHERE (p.city LIKE %:location% OR p.state LIKE %:location% OR p.pincode LIKE %:location%) AND p.availableBeds > 0")
    List<Pg> searchAvailablePgsByLocation(@Param("location") String location);
    
    List<Pg> findByIsActiveTrue();
}
