package com.logistics.design.controller;

import com.logistics.design.common.Result;
import com.logistics.design.entity.SysUser;
import com.logistics.design.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户控制器
 */
@Tag(name = "用户管理", description = "用户管理相关接口")
@RestController
@RequestMapping("/users")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @Operation(summary = "根据用户名查询用户", description = "通过用户名获取用户信息")
    @GetMapping("/username/{username}")
    public Result<SysUser> getByUsername(@PathVariable String username) {
        SysUser user = sysUserService.getByUsername(username);
        return Result.success(user);
    }
} 