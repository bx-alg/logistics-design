package com.logistics.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class OrderItemDTO {
    
    private Long id;
    private Long orderId;
    @NotNull(message = "产品ID不能为空")
    private Long productId;
    private String productName;
    private String productCode;
    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量必须大于0")
    private Integer quantity;
    @NotNull(message = "单价不能为空")
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean deleted = false;
} 