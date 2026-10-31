package com.pgmanagement.controller;

import com.pgmanagement.dto.ResponseDto;
import com.pgmanagement.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StatisticsController {
    
    private final StatisticsService statisticsService;
    
    @GetMapping("/pg/{pgId}")
    public ResponseEntity<ResponseDto> getStatistics(@PathVariable Long pgId) {
        return ResponseEntity.ok(statisticsService.getStatistics(pgId));
    }
    
    @GetMapping("/pg/{pgId}/payment-history/{tenantId}")
    public ResponseEntity<ResponseDto> getPaymentHistory(
            @PathVariable Long pgId,
            @PathVariable Long tenantId) {
        return ResponseEntity.ok(statisticsService.getPaymentHistory(pgId, tenantId));
    }
    
    @GetMapping("/pg/{pgId}/payments")
    public ResponseEntity<ResponseDto> getPaymentsByDateRange(
            @PathVariable Long pgId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(statisticsService.getPaymentsByDateRange(pgId, startDate, endDate));
    }
}
