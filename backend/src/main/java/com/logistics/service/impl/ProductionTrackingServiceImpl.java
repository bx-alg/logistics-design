package com.logistics.service.impl;

import com.logistics.dto.ProductionTrackingDTO;
import com.logistics.dto.ProductionSummaryDTO;
import com.logistics.entity.Order;
import com.logistics.repository.OrderRepository;
import com.logistics.service.ProductionTrackingService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.JoinType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.logistics.entity.OrderItem;

@Service
public class ProductionTrackingServiceImpl implements ProductionTrackingService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Page<ProductionTrackingDTO> findByConditions(String orderNo, String customerName,
                                                      Integer status, LocalDateTime startDate,
                                                      LocalDateTime endDate, Pageable pageable) {
        Specification<Order> spec = (root, query, cb) -> {
            root.fetch("customer", JoinType.LEFT);
            root.fetch("orderItems", JoinType.LEFT).fetch("product", JoinType.LEFT);
            
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("deleted"), false));

            if (StringUtils.hasText(orderNo)) {
                predicates.add(cb.like(root.get("orderNo"), "%" + orderNo + "%"));
            }
            if (StringUtils.hasText(customerName)) {
                predicates.add(cb.like(root.get("customerName"), "%" + customerName + "%"));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createTime"), startDate));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createTime"), endDate));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return orderRepository.findAll(spec, pageable).map(this::convertToDTO);
    }

    @Override
    public ProductionSummaryDTO getProductionSummary() {
        ProductionSummaryDTO summary = new ProductionSummaryDTO();
        
        // 统计各状态订单数量
        summary.setWaiting(orderRepository.countByStatusAndDeletedFalse(0));
        summary.setProcessing(orderRepository.countByStatusInAndDeletedFalse(List.of(1, 2)));
        summary.setShipped(orderRepository.countByStatusAndDeletedFalse(3));
        summary.setCompleted(orderRepository.countByStatusAndDeletedFalse(4));
        
        return summary;
    }

    @Override
    @Transactional
    public ProductionTrackingDTO updateProgress(Long id, Integer progress, String description) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        // 更新进度
        order.setProgress(progress);
        if (StringUtils.hasText(description)) {
            order.setRemark(description);
        }

        // 根据进度更新状态
        if (progress == 100) {
            order.setStatus(3); // 已发货
        } else if (progress >= 1) {
            order.setStatus(2); // 生产中
        }

        orderRepository.save(order);
        return convertToDTO(order);
    }

    private ProductionTrackingDTO convertToDTO(Order order) {
        ProductionTrackingDTO dto = new ProductionTrackingDTO();
        dto.setId(order.getId());
        dto.setOrderNo(order.getOrderNo());
        dto.setCustomerName(order.getCustomer() != null ? order.getCustomer().getCustomerName() : null);
        
        // 获取第一个订单项的产品名称作为主要产品
        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            OrderItem firstItem = order.getOrderItems().get(0);
            if (firstItem.getProduct() != null) {
                dto.setProductName(firstItem.getProduct().getProductName());
            }
        }
        
        dto.setStatus(order.getStatus());
        dto.setProgress(order.getProgress());
        dto.setStartTime(order.getCreateTime());
        dto.setEstimatedEndTime(order.getExpectedDeliveryDate());
        dto.setDescription(order.getRemark());
        
        return dto;
    }
} 