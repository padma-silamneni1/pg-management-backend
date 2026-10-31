package com.pgmanagement.repository;

import com.pgmanagement.entity.Bed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BedRepository extends JpaRepository<Bed, Long> {
    
    List<Bed> findByRoomId(Long roomId);
    
    @Query("SELECT b FROM Bed b WHERE b.room.id = :roomId AND b.isAvailable = true AND b.isActive = true")
    List<Bed> findAvailableBedsByRoomId(@Param("roomId") Long roomId);
    
    @Query("SELECT b FROM Bed b WHERE b.room.pg.id = :pgId AND b.isAvailable = true AND b.isActive = true")
    List<Bed> findAvailableBedsByPgId(@Param("pgId") Long pgId);
}
