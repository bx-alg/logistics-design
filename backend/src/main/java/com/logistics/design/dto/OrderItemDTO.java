package com.logistics.design.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单明细数据传输对象
 */
@Data
public class OrderItemDTO {
    /**
     * 明细ID，创建时不需要，更新时必须
     */
    private Long id;
    
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 数量
     */
    private Integer quantity;
    
    /**
     * 单价
     */
    private BigDecimal unitPrice;
} 