package com.lazy.lazyadmin.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lazy.c.core.domain.R;
import com.lazy.lazyadmin.domain.dto.LoginDto;
import com.lazy.lazyadmin.domain.entity.SysUser;
import com.lazy.lazyadmin.service.SysUserService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录接口管理
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
class AuthController {

    private final SysUserService sysUserService;

    /**
     * 用户登录
     *
     * @param loginDto loginDto
     * @return R
     */
    @PostMapping("/doLogin")
    public R<SysUser> login(@RequestBody LoginDto loginDto) {

        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<SysUser>()
                .eq("username", loginDto.getAccount())
                .eq("password_hash", loginDto.getPassword());

        SysUser one = sysUserService.getOne(queryWrapper);

        return R.ok(one);
    }

    /**
     * 用户登出
     *
     * @param sysUser SysUser
     * @return R
     */
    @PostMapping("/doLogOut")
    public R<Void> logOut(@RequestBody SysUser sysUser) {

        return null;
    }

}
