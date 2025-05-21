package com.logistics.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderStatisticsDTO {
    private Long totalOrders; // 订单总数
    private Long pendingOrders; // 待处理订单数
    private Long processingOrders; // 处理中订单数
    private Long shippedOrders; // 已发货订单数
    private Long deliveredOrders; // 已送达订单数
    private Long completedOrders; // 已完成订单数
    private Long cancelledOrders; // 已取消订单数
    private BigDecimal totalAmount; // 订单总金额
    private BigDecimal avgAmount; // 平均订单金额
    private Long totalCustomers; // 下单客户数
} 