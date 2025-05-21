package com.logistics.controller;

import com.logistics.dto.OrderDTO;
import com.logistics.dto.OrderStatisticsDTO;
import com.logistics.service.OrderService;
import com.logistics.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public Result<OrderDTO> create(@Valid @RequestBody OrderDTO orderDTO) {
        return Result.success(orderService.create(orderDTO));
    }

    @PutMapping("/{id}")
    public Result<OrderDTO> update(@PathVariable Long id, @Valid @RequestBody OrderDTO orderDTO) {
        return Result.success(orderService.update(id, orderDTO));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<OrderDTO> findById(@PathVariable Long id) {
        return Result.success(orderService.findById(id));
    }

    @GetMapping
    public Result<Page<OrderDTO>> findAll(
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        return Result.success(orderService.findAll(keyword, pageable));
    }

    @GetMapping("/search")
    public Result<Page<OrderDTO>> search(
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            Pageable pageable) {
        return Result.success(orderService.findByConditions(
                orderNo, customerId, status, startTime, endTime, pageable));
    }

    @PutMapping("/{id}/status")
    public Result<OrderDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        return Result.success(orderService.updateStatus(id, status));
    }

    @PutMapping("/batch/status")
    public Result<Void> updateStatusBatch(
            @RequestBody List<Long> ids,
            @RequestParam Integer status) {
        orderService.updateStatusBatch(ids, status);
        return Result.success();
    }

    @PutMapping("/{id}/delivery")
    public Result<OrderDTO> confirmDelivery(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime actualDeliveryDate) {
        return Result.success(orderService.confirmDelivery(id, actualDeliveryDate));
    }

    @PutMapping("/{id}/cancel")
    public Result<OrderDTO> cancelOrder(
            @PathVariable Long id,
            @RequestParam String cancelReason) {
        return Result.success(orderService.cancelOrder(id, cancelReason));
    }

    @GetMapping("/export")
    public Result<List<OrderDTO>> export(
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return Result.success(orderService.exportOrders(
                orderNo, customerId, status, startTime, endTime));
    }

    @GetMapping("/statistics")
    public Result<OrderStatisticsDTO> getStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return Result.success(orderService.getOrderStatistics(startTime, endTime));
    }
} 