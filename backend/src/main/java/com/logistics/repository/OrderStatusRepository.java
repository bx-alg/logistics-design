package com.logistics.repository;

import com.logistics.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, Long> {
    List<OrderStatus> findByIsActiveTrueOrderBySequenceAsc();
    OrderStatus findByCode(Integer code);
} 