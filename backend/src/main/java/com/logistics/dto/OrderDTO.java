package com.logistics.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class OrderDTO {
    
    private Long id;
    private String orderNo;
    @NotNull(message = "客户ID不能为空")
    private Long customerId;
    private String customerName;
    private String customerCode;
    @NotNull(message = "订单金额不能为空")
    private BigDecimal totalAmount;
    private Integer status = 0; // 0:待处理 1:处理中 2:已发货 3:已送达 4:已完成 5:已取消
    private String statusName;
    @NotBlank(message = "收货地址不能为空")
    private String address;
    @NotBlank(message = "联系人不能为空")
    private String contactName;
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String contactPhone;
    private String remark;
    @NotNull(message = "预计送达时间不能为空")
    private LocalDateTime expectedDeliveryDate;
    private LocalDateTime actualDeliveryDate;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean deleted = false;
    private List<OrderItemDTO> orderItems;
    
    // 订单状态描述映射
    public String getStatusName() {
        if (status == null) {
            return "";
        }
        
        switch (status) {
            case 0:
                return "待确认";
            case 1:
                return "已确认";
            case 2:
                return "生产中";
            case 3:
                return "已发货";
            case 4:
                return "已完成";
            case 5:
                return "已取消";
            default:
                return "未知状态";
        }
    }
} 