package com.logistics.design.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 订单数据传输对象
 */
@Data
public class OrderDTO {
    /**
     * 订单ID，创建时不需要，更新时必须
     */
    private Long id;
    
    /**
     * 客户ID
     */
    private Long customerId;
    
    /**
     * 订单总额
     */
    private BigDecimal totalAmount;
    
    /**
     * 订单状态：0-待确认，1-已确认，2-生产中，3-已发货，4-已完成，5-已取消
     */
    private Integer status;
    
    /**
     * 收货地址
     */
    private String address;
    
    /**
     * 联系人
     */
    private String contactName;
    
    /**
     * 联系电话
     */
    private String contactPhone;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 预计交付日期
     */
    private LocalDate expectedDeliveryDate;
    
    /**
     * 实际交付日期
     */
    private LocalDate actualDeliveryDate;
    
    /**
     * 订单明细列表
     */
    private List<OrderItemDTO> orderItems;
} 