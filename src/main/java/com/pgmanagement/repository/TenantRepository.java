package com.pgmanagement.repository;

import com.pgmanagement.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    
    List<Tenant> findByPgId(Long pgId);
    
    List<Tenant> findByPgIdAndStatus(Long pgId, Tenant.TenantStatus status);
    
    List<Tenant> findByUserId(Long userId);
    
    @Query("SELECT t FROM Tenant t WHERE t.pg.id = :pgId AND t.status = 'ACTIVE'")
    List<Tenant> findActiveTenantsByPgId(@Param("pgId") Long pgId);
    
    @Query("SELECT t FROM Tenant t WHERE t.room.id = :roomId AND t.status = 'ACTIVE'")
    List<Tenant> findActiveTenantsByRoomId(@Param("roomId") Long roomId);
}
