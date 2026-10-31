package com.pgmanagement.controller;

import com.pgmanagement.dto.BookingRequestDto;
import com.pgmanagement.dto.ResponseDto;
import com.pgmanagement.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/booking")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BookingController {
    
    private final BookingService bookingService;
    
    @PostMapping("/request")
    public ResponseEntity<ResponseDto> requestBooking(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody BookingRequestDto dto) {
        return ResponseEntity.ok(bookingService.requestBooking(userId, dto));
    }
    
    @PostMapping("/{bookingId}/approve")
    public ResponseEntity<ResponseDto> approveBooking(
            @PathVariable Long bookingId,
            @RequestHeader("X-Owner-Email") String ownerEmail) {
        return ResponseEntity.ok(bookingService.approveBooking(bookingId, ownerEmail));
    }
    
    @PostMapping("/{bookingId}/reject")
    public ResponseEntity<ResponseDto> rejectBooking(
            @PathVariable Long bookingId,
            @RequestHeader("X-Owner-Email") String ownerEmail,
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(bookingService.rejectBooking(bookingId, ownerEmail, reason));
    }
    
    @PostMapping("/{bookingId}/payment/initiate")
    public ResponseEntity<ResponseDto> initiatePayment(
            @PathVariable Long bookingId,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(bookingService.initiatePayment(bookingId, userId));
    }
    
    @PostMapping("/{bookingId}/payment/complete")
    public ResponseEntity<ResponseDto> completePayment(
            @PathVariable Long bookingId,
            @RequestParam String transactionId) {
        return ResponseEntity.ok(bookingService.completePayment(bookingId, transactionId));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseDto> getBookingsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getBookingsByUserId(userId));
    }
    
    @GetMapping("/pg/{pgId}/pending")
    public ResponseEntity<ResponseDto> getPendingBookingsByPgId(@PathVariable Long pgId) {
        return ResponseEntity.ok(bookingService.getPendingBookingsByPgId(pgId));
    }
    
    @GetMapping("/user/{userId}/payment-pending")
    public ResponseEntity<ResponseDto> getPaymentPendingBookingsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getPaymentPendingBookingsByUserId(userId));
    }
}
