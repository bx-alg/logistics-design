package com.logistics.service;

import com.logistics.dto.ProductionTrackingDTO;
import com.logistics.dto.ProductionSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;

public interface ProductionTrackingService {
    Page<ProductionTrackingDTO> findByConditions(String orderNo, String customerName,
                                               Integer status, LocalDateTime startDate,
                                               LocalDateTime endDate, Pageable pageable);
    
    ProductionSummaryDTO getProductionSummary();
    
    ProductionTrackingDTO updateProgress(Long id, Integer progress, String description);
} 