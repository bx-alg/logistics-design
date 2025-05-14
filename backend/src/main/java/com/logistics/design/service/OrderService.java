package com.logistics.design.service;

import com.logistics.design.common.PageResult;
import com.logistics.design.dto.OrderDTO;
import com.logistics.design.dto.OrderQueryDTO;
import com.logistics.design.vo.OrderVO;

/**
 * 订单服务接口
 */
public interface OrderService {
    /**
     * 创建订单
     * 
     * @param orderDTO 订单信息
     * @return 订单ID
     */
    Long createOrder(OrderDTO orderDTO);
    
    /**
     * 更新订单
     * 
     * @param orderDTO 订单信息
     * @return 是否成功
     */
    boolean updateOrder(OrderDTO orderDTO);
    
    /**
     * 删除订单
     * 
     * @param id 订单ID
     * @return 是否成功
     */
    boolean deleteOrder(Long id);
    
    /**
     * 查询订单详情
     * 
     * @param id 订单ID
     * @return 订单详情
     */
    OrderVO getOrderDetail(Long id);
    
    /**
     * 分页查询订单列表
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageResult<OrderVO> pageOrder(OrderQueryDTO queryDTO);
    
    /**
     * 更新订单状态
     * 
     * @param id 订单ID
     * @param status 订单状态
     * @return 是否成功
     */
    boolean updateOrderStatus(Long id, Integer status);
} 