package com.logistics.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.logistics.dto.OrderDTO;
import com.logistics.dto.OrderItemDTO;
import com.logistics.dto.OrderQueryDTO;
import com.logistics.entity.Customer;
import com.logistics.entity.Order;
import com.logistics.entity.OrderItem;
import com.logistics.entity.Product;
import com.logistics.mapper.OrderMapper;
import com.logistics.mapper.OrderItemMapper;
import com.logistics.repository.CustomerRepository;
import com.logistics.repository.OrderItemRepository;
import com.logistics.repository.OrderRepository;
import com.logistics.repository.ProductRepository;
import com.logistics.service.OrderService;
import com.logistics.utils.SnowflakeIdGenerator;
import com.logistics.dto.OrderStatisticsDTO;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private OrderItemMapper orderItemMapper;
    
    @Autowired
    private SnowflakeIdGenerator idGenerator;
    
    @Override
    @Transactional
    public OrderDTO create(OrderDTO orderDTO) {
        // 验证客户是否存在
        Customer customer = customerRepository.findById(orderDTO.getCustomerId())
                .orElseThrow(() -> new RuntimeException("客户不存在"));

        // 生成订单号
        String orderNo = generateOrderNo();
        
        // 创建订单
        final Order order = new Order();
        BeanUtils.copyProperties(orderDTO, order);
        order.setOrderNo(orderNo);
        orderRepository.save(order);

        // 保存订单项
        if (orderDTO.getOrderItems() != null && !orderDTO.getOrderItems().isEmpty()) {
            List<OrderItem> orderItems = orderDTO.getOrderItems().stream()
                    .map(itemDTO -> {
                        OrderItem item = new OrderItem();
                        BeanUtils.copyProperties(itemDTO, item);
                        item.setOrderId(order.getId());
                        // 计算总价
                        item.setTotalPrice(itemDTO.getUnitPrice()
                                .multiply(new BigDecimal(itemDTO.getQuantity())));
                        return item;
                    })
                    .collect(Collectors.toList());
            orderItemRepository.saveAll(orderItems);
        }

        return convertToDTO(order, customer);
    }
    
    @Override
    @Transactional
    public OrderDTO update(Long id, OrderDTO orderDTO) {
        final Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        
        // 验证客户是否存在
        Customer customer = customerRepository.findById(orderDTO.getCustomerId())
                .orElseThrow(() -> new RuntimeException("客户不存在"));

        // 更新订单基本信息
        BeanUtils.copyProperties(orderDTO, order, "id", "orderNo", "createTime", "deleted");
        orderRepository.save(order);

        // 更新订单项
        if (orderDTO.getOrderItems() != null) {
            // 删除原有订单项
            orderItemRepository.deleteByOrderId(id);
            
            // 保存新的订单项
            List<OrderItem> orderItems = orderDTO.getOrderItems().stream()
                    .map(itemDTO -> {
                        OrderItem item = new OrderItem();
                        BeanUtils.copyProperties(itemDTO, item);
                        item.setOrderId(order.getId());
                        item.setTotalPrice(itemDTO.getUnitPrice()
                                .multiply(new BigDecimal(itemDTO.getQuantity())));
                        return item;
                    })
                    .collect(Collectors.toList());
            orderItemRepository.saveAll(orderItems);
        }

        return convertToDTO(order, customer);
    }
    
    @Override
    @Transactional
    public void delete(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        order.setDeleted(true);
        orderRepository.save(order);
        
        // 删除关联的订单项
        orderItemRepository.deleteByOrderId(id);
    }
    
    @Override
    public OrderDTO findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        Customer customer = customerRepository.findById(order.getCustomerId())
                .orElseThrow(() -> new RuntimeException("客户不存在"));
        return convertToDTO(order, customer);
    }
    
    @Override
    public Page<OrderDTO> findAll(String keyword, Pageable pageable) {
        Specification<Order> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("deleted"), false));
            
            if (StringUtils.hasText(keyword)) {
                predicates.add(cb.or(
                    cb.like(root.get("orderNo"), "%" + keyword + "%"),
                    cb.like(root.get("contactName"), "%" + keyword + "%"),
                    cb.like(root.get("contactPhone"), "%" + keyword + "%")
                ));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return orderRepository.findAll(spec, pageable)
                .map(order -> {
                    Customer customer = customerRepository.findById(order.getCustomerId())
                            .orElseThrow(() -> new RuntimeException("客户不存在"));
                    return convertToDTO(order, customer);
                });
    }
    
    @Override
    public Page<OrderDTO> findByConditions(String orderNo, Long customerId,
                                         Integer status, LocalDateTime startTime,
                                         LocalDateTime endTime, Pageable pageable) {
        Specification<Order> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("deleted"), false));

            if (StringUtils.hasText(orderNo)) {
                predicates.add(cb.like(root.get("orderNo"), "%" + orderNo + "%"));
            }
            if (customerId != null) {
                predicates.add(cb.equal(root.get("customerId"), customerId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (startTime != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createTime"), startTime));
            }
            if (endTime != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createTime"), endTime));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return orderRepository.findAll(spec, pageable)
                .map(order -> {
                    Customer customer = customerRepository.findById(order.getCustomerId())
                            .orElseThrow(() -> new RuntimeException("客户不存在"));
                    return convertToDTO(order, customer);
                });
    }
    
    @Override
    @Transactional
    public OrderDTO updateStatus(Long id, Integer status) {
        final Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        order.setStatus(status);
        orderRepository.save(order);
        
        Customer customer = customerRepository.findById(order.getCustomerId())
                .orElseThrow(() -> new RuntimeException("客户不存在"));
        return convertToDTO(order, customer);
    }
    
    @Override
    @Transactional
    public void updateStatusBatch(List<Long> ids, Integer status) {
        List<Order> orders = orderRepository.findAllById(ids);
        orders.forEach(order -> order.setStatus(status));
        orderRepository.saveAll(orders);
    }
    
    @Override
    @Transactional
    public OrderDTO confirmDelivery(Long id, LocalDateTime actualDeliveryDate) {
        final Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        order.setStatus(3); // 已送达
        order.setActualDeliveryDate(actualDeliveryDate);
        orderRepository.save(order);
        
        Customer customer = customerRepository.findById(order.getCustomerId())
                .orElseThrow(() -> new RuntimeException("客户不存在"));
        return convertToDTO(order, customer);
    }
    
    @Override
    @Transactional
    public OrderDTO cancelOrder(Long id, String cancelReason) {
        final Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        order.setStatus(5); // 已取消
        order.setRemark(cancelReason);
        orderRepository.save(order);
        
        Customer customer = customerRepository.findById(order.getCustomerId())
                .orElseThrow(() -> new RuntimeException("客户不存在"));
        return convertToDTO(order, customer);
    }
    
    @Override
    public List<OrderDTO> exportOrders(String orderNo, Long customerId,
                                     Integer status, LocalDateTime startTime,
                                     LocalDateTime endTime) {
        Specification<Order> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("deleted"), false));

            if (StringUtils.hasText(orderNo)) {
                predicates.add(cb.like(root.get("orderNo"), "%" + orderNo + "%"));
            }
            if (customerId != null) {
                predicates.add(cb.equal(root.get("customerId"), customerId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (startTime != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createTime"), startTime));
            }
            if (endTime != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createTime"), endTime));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return orderRepository.findAll(spec).stream()
                .map(order -> {
                    Customer customer = customerRepository.findById(order.getCustomerId())
                            .orElseThrow(() -> new RuntimeException("客户不存在"));
                    return convertToDTO(order, customer);
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public OrderStatisticsDTO getOrderStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        Specification<Order> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("deleted"), false));

            if (startTime != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createTime"), startTime));
            }
            if (endTime != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createTime"), endTime));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        List<Order> orders = orderRepository.findAll(spec);
        
        OrderStatisticsDTO statistics = new OrderStatisticsDTO();
        statistics.setTotalOrders((long) orders.size());
        statistics.setPendingOrders(orders.stream().filter(o -> o.getStatus() == 0).count());
        statistics.setProcessingOrders(orders.stream().filter(o -> o.getStatus() == 1).count());
        statistics.setShippedOrders(orders.stream().filter(o -> o.getStatus() == 2).count());
        statistics.setDeliveredOrders(orders.stream().filter(o -> o.getStatus() == 3).count());
        statistics.setCompletedOrders(orders.stream().filter(o -> o.getStatus() == 4).count());
        statistics.setCancelledOrders(orders.stream().filter(o -> o.getStatus() == 5).count());
        
        BigDecimal totalAmount = orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        statistics.setTotalAmount(totalAmount);
        
        if (!orders.isEmpty()) {
            statistics.setAvgAmount(totalAmount.divide(new BigDecimal(orders.size()), 2, RoundingMode.HALF_UP));
        }
        
        statistics.setTotalCustomers(orders.stream()
                .map(Order::getCustomerId)
                .distinct()
                .count());
        
        return statistics;
    }
    
    private String generateOrderNo() {
        // 生成订单号：ORD + 年月日 + 6位序号
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String sequence = String.format("%06d", orderRepository.count() + 1);
        return "ORD" + dateStr + sequence;
    }
    
    private OrderDTO convertToDTO(Order order, Customer customer) {
        OrderDTO dto = new OrderDTO();
        BeanUtils.copyProperties(order, dto);
        
        // 设置客户信息
        dto.setCustomerName(customer.getCustomerName());
        dto.setCustomerCode(customer.getCustomerCode());
        
        // 获取订单项
        List<OrderItem> orderItems = orderItemRepository.findByOrderIdAndDeletedFalse(order.getId());
        List<OrderItemDTO> orderItemDTOs = orderItems.stream()
                .map(item -> {
                    OrderItemDTO itemDTO = new OrderItemDTO();
                    BeanUtils.copyProperties(item, itemDTO);
                    return itemDTO;
                })
                .collect(Collectors.toList());
        dto.setOrderItems(orderItemDTOs);
        
        return dto;
    }
} 