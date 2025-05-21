package com.logistics.controller;

import com.logistics.common.Result;
import com.logistics.dto.ProductionTrackingDTO;
import com.logistics.dto.ProductionSummaryDTO;
import com.logistics.service.ProductionTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/production-tracking")
public class ProductionTrackingController {

    @Autowired
    private ProductionTrackingService productionTrackingService;

    @GetMapping("/list")
    public Result<Page<ProductionTrackingDTO>> list(
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable) {
        return Result.success(productionTrackingService.findByConditions(
                orderNo, customerName, status, startDate, endDate, pageable));
    }

    @GetMapping("/summary")
    public Result<ProductionSummaryDTO> getSummary() {
        return Result.success(productionTrackingService.getProductionSummary());
    }

    @PutMapping("/{id}/progress")
    public Result<ProductionTrackingDTO> updateProgress(
            @PathVariable Long id,
            @RequestParam Integer progress,
            @RequestParam(required = false) String description) {
        return Result.success(productionTrackingService.updateProgress(id, progress, description));
    }
} 