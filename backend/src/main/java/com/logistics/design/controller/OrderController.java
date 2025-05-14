package com.logistics.design.controller;

import com.logistics.design.common.PageResult;
import com.logistics.design.common.Result;
import com.logistics.design.dto.OrderDTO;
import com.logistics.design.dto.OrderQueryDTO;
import com.logistics.design.service.OrderService;
import com.logistics.design.vo.OrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 订单管理控制器
 */
@Tag(name = "订单管理", description = "订单管理相关接口")
@RestController
@RequestMapping("/orders")
public class OrderController {

    @Resource
    private OrderService orderService;

    /**
     * 创建订单
     *
     * @param orderDTO 订单信息
     * @return 结果
     */
    @Operation(summary = "创建订单")
    @PostMapping
    public Result<Long> createOrder(@RequestBody OrderDTO orderDTO) {
        Long orderId = orderService.createOrder(orderDTO);
        return Result.success(orderId);
    }

    /**
     * 更新订单
     *
     * @param orderDTO 订单信息
     * @return 结果
     */
    @Operation(summary = "更新订单")
    @PutMapping
    public Result<Boolean> updateOrder(@RequestBody OrderDTO orderDTO) {
        if (orderDTO.getId() == null) {
            return Result.error("订单ID不能为空");
        }
        boolean success = orderService.updateOrder(orderDTO);
        return success ? Result.success(true) : Result.error("更新订单失败");
    }

    /**
     * 删除订单
     *
     * @param id 订单ID
     * @return 结果
     */
    @Operation(summary = "删除订单")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteOrder(@Parameter(description = "订单ID") @PathVariable Long id) {
        boolean success = orderService.deleteOrder(id);
        return success ? Result.success(true) : Result.error("删除订单失败");
    }

    /**
     * 查询订单详情
     *
     * @param id 订单ID
     * @return 订单详情
     */
    @Operation(summary = "查询订单详情")
    @GetMapping("/{id}")
    public Result<OrderVO> getOrderDetail(@Parameter(description = "订单ID") @PathVariable Long id) {
        OrderVO orderVO = orderService.getOrderDetail(id);
        return orderVO != null ? Result.success(orderVO) : Result.error("订单不存在");
    }

    /**
     * 分页查询订单列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    @Operation(summary = "分页查询订单列表")
    @GetMapping("/page")
    public Result<PageResult<OrderVO>> pageOrder(OrderQueryDTO queryDTO) {
        PageResult<OrderVO> pageResult = orderService.pageOrder(queryDTO);
        return Result.success(pageResult);
    }

    /**
     * 更新订单状态
     *
     * @param id     订单ID
     * @param status 订单状态
     * @return 结果
     */
    @Operation(summary = "更新订单状态")
    @PutMapping("/{id}/status/{status}")
    public Result<Boolean> updateOrderStatus(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @Parameter(description = "订单状态") @PathVariable Integer status) {
        boolean success = orderService.updateOrderStatus(id, status);
        return success ? Result.success(true) : Result.error("更新订单状态失败");
    }
} 