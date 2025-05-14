package com.logistics.design.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 订单实体类
 */
@Data
public class Order {
    /**
     * 订单ID
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
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expectedDeliveryDate;
    
    /**
     * 实际交付日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate actualDeliveryDate;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    
    /**
     * 是否删除：0-未删除，1-已删除
     */
    private Integer deleted;
} 