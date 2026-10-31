package com.pgmanagement.controller;

import com.pgmanagement.dto.PgRegistrationDto;
import com.pgmanagement.dto.ResponseDto;
import com.pgmanagement.dto.RoomDto;
import com.pgmanagement.service.PgService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pg")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PgController {
    
    private final PgService pgService;
    
    @PostMapping("/register")
    public ResponseEntity<ResponseDto> registerPg(@Valid @RequestBody PgRegistrationDto dto) {
        return ResponseEntity.ok(pgService.registerPg(dto));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto> getPgById(@PathVariable Long id) {
        return ResponseEntity.ok(pgService.getPgById(id));
    }
    
    @GetMapping("/owner/{email}")
    public ResponseEntity<ResponseDto> getPgByOwnerEmail(@PathVariable String email) {
        return ResponseEntity.ok(pgService.getPgByOwnerEmail(email));
    }
    
    @GetMapping("/city/{city}")
    public ResponseEntity<ResponseDto> getPgsByCity(@PathVariable String city) {
        return ResponseEntity.ok(pgService.getPgsByCity(city));
    }
    
    @GetMapping("/available/city/{city}")
    public ResponseEntity<ResponseDto> getAvailablePgsByCity(@PathVariable String city) {
        return ResponseEntity.ok(pgService.getAvailablePgsByCity(city));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ResponseDto> searchPgsByLocation(@RequestParam String location) {
        return ResponseEntity.ok(pgService.searchPgsByLocation(location));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto> updatePg(@PathVariable Long id, @Valid @RequestBody PgRegistrationDto dto) {
        return ResponseEntity.ok(pgService.updatePg(id, dto));
    }
    
    @GetMapping("/{pgId}/rooms")
    public ResponseEntity<ResponseDto> getRoomsByPgId(@PathVariable Long pgId) {
        return ResponseEntity.ok(pgService.getRoomsByPgId(pgId));
    }
    
    @GetMapping("/{pgId}/rooms/available")
    public ResponseEntity<ResponseDto> getAvailableRoomsByPgId(@PathVariable Long pgId) {
        return ResponseEntity.ok(pgService.getAvailableRoomsByPgId(pgId));
    }
    
    @GetMapping("/rooms/{roomId}/beds/available")
    public ResponseEntity<ResponseDto> getAvailableBedsByRoomId(@PathVariable Long roomId) {
        return ResponseEntity.ok(pgService.getAvailableBedsByRoomId(roomId));
    }
    
    @GetMapping("/{pgId}/beds/available")
    public ResponseEntity<ResponseDto> getAvailableBedsByPgId(@PathVariable Long pgId) {
        return ResponseEntity.ok(pgService.getAvailableBedsByPgId(pgId));
    }
    
    @PostMapping("/{pgId}/rooms")
    public ResponseEntity<ResponseDto> addRoom(@PathVariable Long pgId, @Valid @RequestBody RoomDto dto) {
        return ResponseEntity.ok(pgService.addRoom(pgId, dto));
    }
}
