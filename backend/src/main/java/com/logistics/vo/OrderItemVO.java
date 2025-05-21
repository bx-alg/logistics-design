package com.logistics.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * 订单明细视图对象
 */
@Data
public class OrderItemVO {
    
    /**
     * 主键ID
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
     * 商品名称
     */
    private String productName;
    
    /**
     * 商品编码
     */
    private String productCode;
    
    /**
     * 数量
     */
    private Integer quantity;
    
    /**
     * 单位
     */
    private String unit;
    
    /**
     * 单价
     */
    private BigDecimal unitPrice;
    
    /**
     * 总价
     */
    private BigDecimal totalPrice;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
} 