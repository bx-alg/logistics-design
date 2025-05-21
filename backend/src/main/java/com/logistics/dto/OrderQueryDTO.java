package com.logistics.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class OrderQueryDTO {
    
    private String orderNo;
    private Long customerId;
    private String customerName;
    private Integer status;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer page = 1;
    private Integer size = 10;
} 