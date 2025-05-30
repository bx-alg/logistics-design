package com.logistics.service;

import com.logistics.dto.OrderStatusDTO;
import java.util.List;
 
public interface OrderStatusService {
    List<OrderStatusDTO> findAllActive();
    OrderStatusDTO findByCode(Integer code);
} 