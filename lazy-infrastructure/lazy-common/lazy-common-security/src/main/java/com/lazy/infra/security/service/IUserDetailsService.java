package com.lazy.infra.security.service;


import com.lazy.infra.security.domain.LoginUser;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 用户详情加载服务接口
 * <p>
 * 继承 Spring Security 的 {@link UserDetailsService}，扩展业务所需的用户加载方式。
 * <ul>
 *   <li>{@code loadUserByUsername(String)} — 继承自 UserDetailsService，用户名密码登录</li>
 *   <li>{@link #loadUserByPhone(String)} — 手机号验证码登录</li>
 * </ul>
 * <p>
 * 业务模块（如 lazy-admin、lazy-customer）各自提供实现，
 * 从不同的用户表加载数据并构建 {@link LoginUser}。
 *
 * @author Surprise0127
 * @since 1.0.0
 */
public interface IUserDetailsService extends UserDetailsService {

    /**
     * 根据手机号加载用户信息
     * <p>
     * 用于手机号验证码登录场景。实现方应：
     * <ol>
     *   <li>根据手机号查询用户（排除已删除记录）</li>
     *   <li>校验账号状态</li>
     *   <li>加载角色和权限</li>
     *   <li>构建并返回 {@link LoginUser}（password 字段可为 null）</li>
     * </ol>
     *
     * @param phone 手机号
     * @return 包含完整用户信息的 LoginUser
     * @throws org.springframework.security.core.userdetails.UsernameNotFoundException 手机号未注册
     */
    LoginUser loadUserByPhone(String phone);
}
