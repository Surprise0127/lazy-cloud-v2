package com.lazy.c.security.service;

/**
 * Token 黑名单服务接口
 * <p>
 * 用于检查 Token 是否已被注销（如用户主动登出）。
 * 请自行基于 Redis 提供具体实现
 * <p>
 * 如果容器中没有此 Bean，默认使用空实现（不检查黑名单）。
 *
 * @author Surprise0127
 * @since 1.0.0
 */
public interface TokenBlacklistService {

    /**
     * 检查 Token 是否已被加入黑名单
     *
     * @param jti JWT ID（唯一标识）
     * @return true=已注销（黑名单中存在），false=有效
     */
    boolean isBlacklisted(String jti);

    /**
     * 将 Token 加入黑名单（登出时调用）
     *
     * @param jti              JWT ID
     * @param remainingSeconds Token 剩余有效期（秒），用作黑名单 TTL
     */
    void addToBlacklist(String jti, long remainingSeconds);
}
