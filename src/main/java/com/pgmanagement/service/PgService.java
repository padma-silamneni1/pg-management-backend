package com.pgmanagement.service;

import com.pgmanagement.dto.PgRegistrationDto;
import com.pgmanagement.dto.ResponseDto;
import com.pgmanagement.dto.RoomDto;
import com.pgmanagement.entity.Pg;
import com.pgmanagement.entity.Room;
import com.pgmanagement.entity.Bed;
import com.pgmanagement.repository.PgRepository;
import com.pgmanagement.repository.RoomRepository;
import com.pgmanagement.repository.BedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PgService {
    
    private final PgRepository pgRepository;
    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;
    
    public ResponseDto registerPg(PgRegistrationDto dto) {
        if (pgRepository.findByOwnerEmail(dto.getOwnerEmail()).isPresent()) {
            return ResponseDto.error("PG with this owner email already exists");
        }
        
        Pg pg = Pg.builder()
                .name(dto.getName())
                .address(dto.getAddress())
                .city(dto.getCity())
                .state(dto.getState())
                .pincode(dto.getPincode())
                .numberOfFloors(dto.getNumberOfFloors())
                .totalRooms(dto.getTotalRooms())
                .totalBeds(dto.getTotalBeds())
                .availableBeds(dto.getTotalBeds())
                .rentPerBed(dto.getRentPerBed())
                .ownerName(dto.getOwnerName())
                .ownerEmail(dto.getOwnerEmail())
                .ownerPhone(dto.getOwnerPhone())
                .amenities(dto.getAmenities())
                .description(dto.getDescription())
                .isActive(true)
                .build();
        
        pg = pgRepository.save(pg);
        
        // Create default rooms and beds
        createDefaultRoomsAndBeds(pg, dto);
        
        return ResponseDto.success("PG registered successfully", pg);
    }
    
    @CacheEvict(value = "pgs", allEntries = true)
    private void createDefaultRoomsAndBeds(Pg pg, PgRegistrationDto dto) {
        int bedsPerRoom = dto.getTotalBeds() / dto.getTotalRooms();
        int remainingBeds = dto.getTotalBeds() % dto.getTotalRooms();
        
        int roomsPerFloor = dto.getTotalRooms() / dto.getNumberOfFloors();
        int remainingRooms = dto.getTotalRooms() % dto.getNumberOfFloors();
        
        int roomCounter = 1;
        for (int floor = 1; floor <= dto.getNumberOfFloors(); floor++) {
            int roomsOnThisFloor = roomsPerFloor + (floor <= remainingRooms ? 1 : 0);
            
            for (int r = 0; r < roomsOnThisFloor && roomCounter <= dto.getTotalRooms(); r++) {
                int bedsInThisRoom = bedsPerRoom + (roomCounter <= remainingBeds ? 1 : 0);
                
                Room room = Room.builder()
                        .pg(pg)
                        .floorNumber(floor)
                        .roomNumber("R" + floor + "-" + roomCounter)
                        .capacity(bedsInThisRoom)
                        .occupiedBeds(0)
                        .availableBeds(bedsInThisRoom)
                        .roomType("SHARING")
                        .isActive(true)
                        .build();
                
                room = roomRepository.save(room);
                
                // Create beds for this room
                for (int b = 1; b <= bedsInThisRoom; b++) {
                    Bed bed = Bed.builder()
                            .room(room)
                            .bedNumber(room.getRoomNumber() + "-B" + b)
                            .isAvailable(true)
                            .isActive(true)
                            .build();
                    bedRepository.save(bed);
                }
                
                roomCounter++;
            }
        }
    }
    
    @Cacheable(value = "pgs", key = "#id")
    public ResponseDto getPgById(Long id) {
        return pgRepository.findById(id)
                .map(pg -> ResponseDto.success("PG retrieved successfully", pg))
                .orElse(ResponseDto.error("PG not found"));
    }
    
    @Cacheable(value = "pgs", key = "'owner:' + #email")
    public ResponseDto getPgByOwnerEmail(String email) {
        return pgRepository.findByOwnerEmail(email)
                .map(pg -> ResponseDto.success("PG retrieved successfully", pg))
                .orElse(ResponseDto.error("PG not found for this owner"));
    }
    
    @Cacheable(value = "pgs", key = "'city:' + #city")
    public ResponseDto getPgsByCity(String city) {
        List<Pg> pgs = pgRepository.findByCity(city);
        return ResponseDto.success("PGs retrieved successfully", pgs);
    }
    
    @Cacheable(value = "pgs", key = "'available:' + #city")
    public ResponseDto getAvailablePgsByCity(String city) {
        List<Pg> pgs = pgRepository.findAvailablePgsByCity(city);
        return ResponseDto.success("Available PGs retrieved successfully", pgs);
    }
    
    public ResponseDto searchPgsByLocation(String location) {
        List<Pg> pgs = pgRepository.searchAvailablePgsByLocation(location);
        return ResponseDto.success("PGs retrieved successfully", pgs);
    }
    
    @CacheEvict(value = "pgs", allEntries = true)
    public ResponseDto updatePg(Long id, PgRegistrationDto dto) {
        return pgRepository.findById(id)
                .map(pg -> {
                    pg.setName(dto.getName());
                    pg.setAddress(dto.getAddress());
                    pg.setCity(dto.getCity());
                    pg.setState(dto.getState());
                    pg.setPincode(dto.getPincode());
                    pg.setRentPerBed(dto.getRentPerBed());
                    pg.setOwnerName(dto.getOwnerName());
                    pg.setOwnerPhone(dto.getOwnerPhone());
                    pg.setAmenities(dto.getAmenities());
                    pg.setDescription(dto.getDescription());
                    pgRepository.save(pg);
                    return ResponseDto.success("PG updated successfully", pg);
                })
                .orElse(ResponseDto.error("PG not found"));
    }
    
    public ResponseDto getRoomsByPgId(Long pgId) {
        List<Room> rooms = roomRepository.findByPgId(pgId);
        return ResponseDto.success("Rooms retrieved successfully", rooms);
    }
    
    public ResponseDto getAvailableRoomsByPgId(Long pgId) {
        List<Room> rooms = roomRepository.findAvailableRoomsByPgId(pgId);
        return ResponseDto.success("Available rooms retrieved successfully", rooms);
    }
    
    public ResponseDto getAvailableBedsByRoomId(Long roomId) {
        List<Bed> beds = bedRepository.findAvailableBedsByRoomId(roomId);
        return ResponseDto.success("Available beds retrieved successfully", beds);
    }
    
    public ResponseDto getAvailableBedsByPgId(Long pgId) {
        List<Bed> beds = bedRepository.findAvailableBedsByPgId(pgId);
        return ResponseDto.success("Available beds retrieved successfully", beds);
    }
    
    @CacheEvict(value = "pgs", allEntries = true)
    public ResponseDto addRoom(Long pgId, RoomDto dto) {
        return pgRepository.findById(pgId)
                .map(pg -> {
                    Room room = Room.builder()
                            .pg(pg)
                            .floorNumber(dto.getFloorNumber())
                            .roomNumber(dto.getRoomNumber())
                            .capacity(dto.getCapacity())
                            .occupiedBeds(0)
                            .availableBeds(dto.getCapacity())
                            .roomType(dto.getRoomType())
                            .amenities(dto.getAmenities())
                            .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                            .build();
                    
                    room = roomRepository.save(room);
                    
                    // Create beds for this room
                    for (int b = 1; b <= dto.getCapacity(); b++) {
                        Bed bed = Bed.builder()
                                .room(room)
                                .bedNumber(room.getRoomNumber() + "-B" + b)
                                .isAvailable(true)
                                .isActive(true)
                                .build();
                        bedRepository.save(bed);
                    }
                    
                    // Update PG total beds
                    pg.setTotalBeds(pg.getTotalBeds() + dto.getCapacity());
                    pg.setAvailableBeds(pg.getAvailableBeds() + dto.getCapacity());
                    pg.setTotalRooms(pg.getTotalRooms() + 1);
                    pgRepository.save(pg);
                    
                    return ResponseDto.success("Room added successfully", room);
                })
                .orElse(ResponseDto.error("PG not found"));
    }
}
