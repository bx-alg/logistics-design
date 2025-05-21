package com.logistics.dto;

import lombok.Data;

@Data
public class ProductionSummaryDTO {
    private Long waiting;      // 待生产
    private Long processing;   // 生产中
    private Long shipped;      // 已发货
    private Long completed;    // 已完成
} 