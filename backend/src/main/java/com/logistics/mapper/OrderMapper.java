package com.logistics.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.logistics.entity.Order;

/**
 * 订单数据访问接口
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    
    /**
     * 根据条件查询订单
     */
    IPage<Order> findOrders(Page<Order> page,
                           @Param("orderNo") String orderNo,
                           @Param("customerId") Long customerId,
                           @Param("status") Integer status,
                           @Param("startDate") LocalDate startDate,
                           @Param("endDate") LocalDate endDate);
                           
    /**
     * 根据订单号查询订单
     */
    Order findByOrderNo(@Param("orderNo") String orderNo);
    
    /**
     * 获取客户的订单列表
     */
    List<Order> findByCustomerId(@Param("customerId") Long customerId);
} 