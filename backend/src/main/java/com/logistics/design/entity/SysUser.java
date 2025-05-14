package com.logistics.design.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.logistics.design.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;
} 