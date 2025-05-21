package com.logistics.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.logistics.entity.OrderItem;

/**
 * 订单明细数据访问接口
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
    
    /**
     * 根据订单ID查询订单明细
     */
    List<OrderItem> findByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 根据订单ID删除订单明细
     */
    void deleteByOrderId(@Param("orderId") Long orderId);
} 