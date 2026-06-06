package com.lazy.lazyadmin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lazy.c.core.domain.R;
import com.lazy.lazyadmin.domain.entity.SysUser;
import com.lazy.lazyadmin.service.SysUserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理接口
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final SysUserService sysUserService;


    /**
     * 查询用户
     *
     * @param sysUser 用户信息
     * @return R
     */
    @GetMapping("/query/user")
    public R<SysUser> queryUser(SysUser sysUser) {
        SysUser user = sysUserService.getById(sysUser);
        return R.ok(user);
    }

    /**
     * 查询用户列表
     *
     * @return R
     */
    @GetMapping("/query/users")
    public R<List<SysUser>> queryUsers(int current, int size) {
        Page<SysUser> page = new Page<>(current, size);
        List<SysUser> users = sysUserService.list(page);
        return R.ok(users);
    }

    /**
     * 查询用户列表
     * - 条件查询
     *
     * @param sysUser 用户信息
     * @return R
     */
    @PostMapping("/query/users")
    public R<List<SysUser>> queryUsersBy(@RequestBody SysUser sysUser) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("nickname", sysUser.getNickname());
        List<SysUser> users = sysUserService.list(queryWrapper);
        return R.ok(users);
    }

    /**
     * 新增用户
     *
     * @param sysUser 用户信息
     * @return R
     */
    @PostMapping("/add/user")
    public R<?> addUser(@RequestBody SysUser sysUser) {
        sysUserService.save(sysUser);
        return R.ok();
    }

    /**
     * 更新用户
     *
     * @param sysUser 用户信息
     * @return R
     */
    @PostMapping("/edit/user")
    public R<Void> editUser(@RequestBody SysUser sysUser) {
        sysUserService.updateById(sysUser);
        return R.ok();
    }

    /**
     * 删除用户
     *
     * @param sysUser 用户信息
     * @return R
     */
    @PostMapping("/delete/user")
    public R<Void> deleteUser(@RequestBody SysUser sysUser) {
        sysUserService.removeById(sysUser);
        return R.ok();
    }

    /**
     * 批量删除用户
     *
     * @param ids 用户id列表
     * @return R
     */
    @PostMapping("/delete/users")
    public R<Void> deleteUsers(@RequestBody @Valid List<@NotNull SysUser> ids) {
        boolean b = sysUserService.removeBatchByIds(ids);
        System.out.printf("" + b);
        return R.ok();
    }
}

