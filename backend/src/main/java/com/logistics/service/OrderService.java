package com.logistics.service;

import com.logistics.dto.OrderDTO;
import com.logistics.dto.OrderStatisticsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    OrderDTO create(OrderDTO orderDTO);
    
    OrderDTO update(Long id, OrderDTO orderDTO);
    
    void delete(Long id);
    
    OrderDTO findById(Long id);
    
    Page<OrderDTO> findAll(String keyword, Pageable pageable);
    
    // 高级查询
    Page<OrderDTO> findByConditions(
        String orderNo,
        Long customerId,
        Integer status,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Pageable pageable
    );
    
    // 更新订单状态
    OrderDTO updateStatus(Long id, Integer status);
    
    // 批量更新订单状态
    void updateStatusBatch(List<Long> ids, Integer status);
    
    // 确认送达
    OrderDTO confirmDelivery(Long id, LocalDateTime actualDeliveryDate);
    
    // 取消订单
    OrderDTO cancelOrder(Long id, String cancelReason);
    
    // 导出订单
    List<OrderDTO> exportOrders(
        String orderNo,
        Long customerId,
        Integer status,
        LocalDateTime startTime,
        LocalDateTime endTime
    );
    
    // 获取订单统计信息
    OrderStatisticsDTO getOrderStatistics(
        LocalDateTime startTime,
        LocalDateTime endTime
    );
} 