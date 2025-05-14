package com.logistics.design.mapper;

import com.logistics.design.entity.OrderItem;
import com.logistics.design.vo.OrderItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单明细Mapper接口
 */
@Mapper
public interface OrderItemMapper {
    /**
     * 批量插入订单明细
     * 
     * @param items 订单明细列表
     * @return 影响行数
     */
    int batchInsert(@Param("items") List<OrderItem> items);
    
    /**
     * 根据订单ID删除订单明细
     * 
     * @param orderId 订单ID
     * @return 影响行数
     */
    int deleteByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 根据订单ID查询订单明细
     * 
     * @param orderId 订单ID
     * @return 订单明细列表
     */
    List<OrderItem> selectByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 根据订单ID查询订单明细视图对象
     * 
     * @param orderId 订单ID
     * @return 订单明细视图对象列表
     */
    List<OrderItemVO> selectItemVOByOrderId(@Param("orderId") Long orderId);
} 