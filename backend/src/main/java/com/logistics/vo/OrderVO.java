package com.logistics.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

/**
 * 订单视图对象
 */
@Data
public class OrderVO {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 订单编号
     */
    private String orderNo;
    
    /**
     * 客户ID
     */
    private Long customerId;
    
    /**
     * 客户名称
     */
    private String customerName;
    
    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;
    
    /**
     * 订单状态：0-待确认，1-已确认，2-生产中，3-已发货，4-已完成，5-已取消
     */
    private Integer status;
    
    /**
     * 状态名称
     */
    private String statusName;
    
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
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 订单明细
     */
    private List<OrderItemVO> orderItems;
} 