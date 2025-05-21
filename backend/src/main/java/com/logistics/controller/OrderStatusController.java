package com.logistics.controller;

import com.logistics.common.Result;
import com.logistics.dto.OrderStatusDTO;
import com.logistics.service.OrderStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/order-status")
public class OrderStatusController {

    @Autowired
    private OrderStatusService orderStatusService;

    @GetMapping("/list")
    public Result<List<OrderStatusDTO>> list() {
        return Result.success(orderStatusService.findAllActive());
    }

    @GetMapping("/{code}")
    public Result<OrderStatusDTO> getByCode(@PathVariable Integer code) {
        return Result.success(orderStatusService.findByCode(code));
    }
} 