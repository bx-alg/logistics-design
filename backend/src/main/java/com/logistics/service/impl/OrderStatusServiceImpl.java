package com.logistics.service.impl;

import com.logistics.dto.OrderStatusDTO;
import com.logistics.entity.OrderStatus;
import com.logistics.repository.OrderStatusRepository;
import com.logistics.service.OrderStatusService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderStatusServiceImpl implements OrderStatusService {

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    @Override
    public List<OrderStatusDTO> findAllActive() {
        return orderStatusRepository.findByIsActiveTrueOrderBySequenceAsc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OrderStatusDTO findByCode(Integer code) {
        OrderStatus status = orderStatusRepository.findByCode(code);
        return status != null ? convertToDTO(status) : null;
    }

    private OrderStatusDTO convertToDTO(OrderStatus status) {
        OrderStatusDTO dto = new OrderStatusDTO();
        BeanUtils.copyProperties(status, dto);
        return dto;
    }
} 