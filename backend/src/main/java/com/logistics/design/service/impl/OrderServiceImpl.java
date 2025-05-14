package com.logistics.design.service.impl;

import com.logistics.design.common.PageResult;
import com.logistics.design.dto.OrderDTO;
import com.logistics.design.dto.OrderItemDTO;
import com.logistics.design.dto.OrderQueryDTO;
import com.logistics.design.entity.Order;
import com.logistics.design.entity.OrderItem;
import com.logistics.design.mapper.OrderItemMapper;
import com.logistics.design.mapper.OrderMapper;
import com.logistics.design.service.OrderService;
import com.logistics.design.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单服务实现类
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderMapper orderMapper;
    
    @Resource
    private OrderItemMapper orderItemMapper;
    
    /**
     * 创建订单
     *
     * @param orderDTO 订单信息
     * @return 订单ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(OrderDTO orderDTO) {
        // 1. 创建订单
        Order order = new Order();
        BeanUtils.copyProperties(orderDTO, order);
        
        // 2. 生成订单编号
        String orderNo = orderMapper.generateOrderNo();
        order.setOrderNo(orderNo);
        
        // 3. 设置初始状态
        order.setStatus(0); // 待确认
        
        // 4. 计算订单总金额（防止前端传入错误金额）
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        
        for (OrderItemDTO itemDTO : orderDTO.getOrderItems()) {
            OrderItem item = new OrderItem();
            BeanUtils.copyProperties(itemDTO, item);
            
            // 计算明细总价
            BigDecimal itemTotal = itemDTO.getUnitPrice().multiply(new BigDecimal(itemDTO.getQuantity()));
            item.setTotalPrice(itemTotal);
            
            // 累加订单总金额
            totalAmount = totalAmount.add(itemTotal);
            
            orderItems.add(item);
        }
        
        order.setTotalAmount(totalAmount);
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        order.setDeleted(0);
        
        // 5. 保存订单
        orderMapper.insert(order);
        
        // 6. 保存订单明细
        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            item.setCreateTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            item.setDeleted(0);
        }
        
        orderItemMapper.batchInsert(orderItems);
        
        return order.getId();
    }
    
    /**
     * 更新订单
     *
     * @param orderDTO 订单信息
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateOrder(OrderDTO orderDTO) {
        // 1. 检查订单是否存在
        Order existOrder = orderMapper.selectById(orderDTO.getId());
        if (existOrder == null || existOrder.getDeleted() == 1) {
            return false;
        }
        
        // 2. 只有待确认状态的订单才能修改
        if (existOrder.getStatus() != 0) {
            return false;
        }
        
        // 3. 更新订单信息
        Order order = new Order();
        BeanUtils.copyProperties(orderDTO, order);
        
        // 4. 计算订单总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        
        for (OrderItemDTO itemDTO : orderDTO.getOrderItems()) {
            OrderItem item = new OrderItem();
            BeanUtils.copyProperties(itemDTO, item);
            
            // 计算明细总价
            BigDecimal itemTotal = itemDTO.getUnitPrice().multiply(new BigDecimal(itemDTO.getQuantity()));
            item.setTotalPrice(itemTotal);
            
            // 累加订单总金额
            totalAmount = totalAmount.add(itemTotal);
            
            orderItems.add(item);
        }
        
        order.setTotalAmount(totalAmount);
        order.setUpdateTime(LocalDateTime.now());
        
        // 5. 保存订单
        orderMapper.update(order);
        
        // 6. 删除原有订单明细
        orderItemMapper.deleteByOrderId(order.getId());
        
        // 7. 保存新的订单明细
        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            item.setCreateTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            item.setDeleted(0);
        }
        
        orderItemMapper.batchInsert(orderItems);
        
        return true;
    }
    
    /**
     * 删除订单
     *
     * @param id 订单ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteOrder(Long id) {
        // 1. 检查订单是否存在
        Order order = orderMapper.selectById(id);
        if (order == null || order.getDeleted() == 1) {
            return false;
        }
        
        // 2. 逻辑删除订单
        orderMapper.deleteById(id);
        
        // 3. 逻辑删除订单明细
        orderItemMapper.deleteByOrderId(id);
        
        return true;
    }
    
    /**
     * 查询订单详情
     *
     * @param id 订单ID
     * @return 订单详情
     */
    @Override
    public OrderVO getOrderDetail(Long id) {
        return orderMapper.selectOrderVOById(id);
    }
    
    /**
     * 分页查询订单列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    @Override
    public PageResult<OrderVO> pageOrder(OrderQueryDTO queryDTO) {
        // 查询总数
        long total = orderMapper.selectOrderCount(queryDTO);
        if (total == 0) {
            return PageResult.of(new ArrayList<>(), 0L, queryDTO.getPageNum(), queryDTO.getPageSize());
        }
        
        // 查询数据
        List<OrderVO> list = orderMapper.selectOrderList(queryDTO);
        
        return PageResult.of(list, total, queryDTO.getPageNum(), queryDTO.getPageSize());
    }
    
    /**
     * 更新订单状态
     *
     * @param id 订单ID
     * @param status 订单状态
     * @return 是否成功
     */
    @Override
    public boolean updateOrderStatus(Long id, Integer status) {
        // 1. 检查订单是否存在
        Order order = orderMapper.selectById(id);
        if (order == null || order.getDeleted() == 1) {
            return false;
        }
        
        // 2. 检查状态变更是否合法
        if (!checkStatusChange(order.getStatus(), status)) {
            return false;
        }
        
        // 3. 更新状态
        return orderMapper.updateStatus(id, status) > 0;
    }
    
    /**
     * 检查订单状态变更是否合法
     * 
     * @param oldStatus 旧状态
     * @param newStatus 新状态
     * @return 是否合法
     */
    private boolean checkStatusChange(Integer oldStatus, Integer newStatus) {
        // 订单状态：0-待确认，1-已确认，2-生产中，3-已发货，4-已完成，5-已取消
        
        // 只能向前推进状态，不能回退
        if (newStatus < oldStatus && newStatus != 5) {
            return false;
        }
        
        // 已取消状态不能变更
        if (oldStatus == 5) {
            return false;
        }
        
        // 已完成状态只能变更为已取消
        if (oldStatus == 4 && newStatus != 5) {
            return false;
        }
        
        // 状态只能逐步推进，不能跳级
        if (newStatus > oldStatus + 1 && newStatus != 5) {
            return false;
        }
        
        return true;
    }
} 