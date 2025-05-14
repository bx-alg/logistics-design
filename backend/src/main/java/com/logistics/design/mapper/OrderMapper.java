package com.logistics.design.mapper;

import com.logistics.design.entity.Order;
import com.logistics.design.dto.OrderQueryDTO;
import com.logistics.design.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单Mapper接口
 */
@Mapper
public interface OrderMapper {
    /**
     * 插入订单
     * 
     * @param order 订单信息
     * @return 影响行数
     */
    int insert(Order order);
    
    /**
     * 更新订单
     * 
     * @param order 订单信息
     * @return 影响行数
     */
    int update(Order order);
    
    /**
     * 根据ID删除订单
     * 
     * @param id 订单ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据ID查询订单
     * 
     * @param id 订单ID
     * @return 订单信息
     */
    Order selectById(@Param("id") Long id);
    
    /**
     * 根据ID查询订单详情
     * 
     * @param id 订单ID
     * @return 订单详情
     */
    OrderVO selectOrderVOById(@Param("id") Long id);
    
    /**
     * 查询订单列表
     * 
     * @param query 查询参数
     * @return 订单列表
     */
    List<OrderVO> selectOrderList(OrderQueryDTO query);
    
    /**
     * 查询订单总数
     * 
     * @param query 查询参数
     * @return 订单总数
     */
    long selectOrderCount(OrderQueryDTO query);
    
    /**
     * 更新订单状态
     * 
     * @param id 订单ID
     * @param status 订单状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    /**
     * 生成订单编号
     * 
     * @return 订单编号
     */
    String generateOrderNo();
} 