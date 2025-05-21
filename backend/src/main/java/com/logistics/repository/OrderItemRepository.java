package com.logistics.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.logistics.entity.OrderItem;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    List<OrderItem> findByOrderIdAndDeletedEquals(Long orderId, Boolean deleted);
    
    List<OrderItem> findByOrderIdAndDeletedFalse(Long orderId);
    
    void deleteByOrderId(Long orderId);
} 