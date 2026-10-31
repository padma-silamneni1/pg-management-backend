package com.pgmanagement.repository;

import com.pgmanagement.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    
    List<Room> findByPgId(Long pgId);
    
    List<Room> findByPgIdAndFloorNumber(Long pgId, Integer floorNumber);
    
    @Query("SELECT r FROM Room r WHERE r.pg.id = :pgId AND r.availableBeds > 0")
    List<Room> findAvailableRoomsByPgId(@Param("pgId") Long pgId);
    
    @Query("SELECT r FROM Room r WHERE r.pg.id = :pgId AND r.isActive = true")
    List<Room> findActiveRoomsByPgId(@Param("pgId") Long pgId);
}
