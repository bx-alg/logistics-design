package com.logistics.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Data
public class CustomerDTO {
    private Long id;

    @NotBlank(message = "客户名称不能为空")
    private String customerName;

    @NotBlank(message = "客户编码不能为空")
    private String customerCode;

    private String contactName;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String contactPhone;

    @Email(message = "邮箱格式不正确")
    private String email;

    private String address;

    private Integer status = 1;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Boolean deleted = false;
} 