package com.lazy.c.security.service;


import com.lazy.c.security.domain.LoginUser;
import io.jsonwebtoken.Claims;

/**
 * 用户权限加载服务接口
 * <p>
 * 在 JWT 认证过滤器中，Token 只携带 uniqueIdentifier 最小信息。
 * 通过此接口从缓存（Redis）或数据库加载完整的权限和角色信息，填充到 {@link LoginUser} 中。
 * <p>
 * 业务模块（如 lazy-admin）提供基于 Redis 缓存的具体实现。
 * 如果容器中没有此 Bean，默认使用空实现（不加载权限）。
 *
 * @author Surprise0127
 * @since 1.0.0
 */
public interface IUserPermissionService {

    /**
     * 加载用户权限信息，填充到 LoginUser 中
     * <p>
     * 实现方应从 Redis 缓存或数据库中加载用户的角色和权限标识。
     *
     * @param loginUser 已包含 uniqueIdentifier 的登录用户对象
     * @param claims    Claims
     */
    void loadPermissions(LoginUser loginUser, Claims claims);
}
