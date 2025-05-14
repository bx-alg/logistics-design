package com.logistics.design.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.logistics.design.entity.SysUser;

/**
 * 用户Service接口
 */
public interface SysUserService extends IService<SysUser> {
    
    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    SysUser getByUsername(String username);
} 