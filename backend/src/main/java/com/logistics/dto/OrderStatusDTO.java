package com.logistics.dto;

import lombok.Data;

@Data
public class OrderStatusDTO {
    private Long id;
    private Integer code;
    private String name;
    private String description;
    private Integer sequence;
    private Boolean isActive;
} 