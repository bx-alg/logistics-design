package com.logistics.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.logistics.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    
    Order findByOrderNoAndDeletedEquals(String orderNo, Boolean deleted);
    
    @Query("SELECT o FROM Order o WHERE o.deleted = false " +
           "AND (:orderNo IS NULL OR o.orderNo LIKE CONCAT('%', :orderNo, '%')) " +
           "AND (:customerId IS NULL OR o.customerId = :customerId) " +
           "AND (:status IS NULL OR o.status = :status) " +
           "AND (:startDate IS NULL OR o.createTime >= :startDate) " +
           "AND (:endDate IS NULL OR o.createTime <= :endDate) " +
           "ORDER BY o.createTime DESC")
    Page<Order> findOrders(
        @Param("orderNo") String orderNo,
        @Param("customerId") Long customerId,
        @Param("status") Integer status,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        Pageable pageable
    );
    
    List<Order> findByCustomerIdAndDeletedEquals(Long customerId, Boolean deleted);

    Long countByStatusAndDeletedFalse(Integer status);
    Long countByStatusInAndDeletedFalse(List<Integer> statuses);
} 