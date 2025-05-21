package com.logistics.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProductionTrackingDTO {
    private Long id;
    private String orderNo;
    private String customerName;
    private String productName;
    private Integer status;
    private Integer progress;
    private LocalDateTime startTime;
    private LocalDateTime estimatedEndTime;
    private String description;
} 