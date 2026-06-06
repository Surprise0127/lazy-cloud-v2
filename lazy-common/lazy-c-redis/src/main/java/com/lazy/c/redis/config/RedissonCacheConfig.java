package com.lazy.c.redis.config;

import org.redisson.api.RedissonClient;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.transaction.TransactionAwareCacheManagerProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Redisson 配置（分布式缓存）
 * <p>
 * 条件：
 * - spring.cache.type=redis
 * - 存在 RedissonClient
 * - 未存在其他 CacheManager Bean
 * <p>
 * 支持事务管理：
 * - 配置 RedissonTransactionManager 用于 Redis 事务
 * - CacheManager 支持 Spring 事务，确保缓存操作与数据库事务一致
 *
 * @author Surprise0127
 * @date 2026/1/20 12:00
 */
@Configuration
@ConditionalOnClass(value = {
        RedissonClient.class,
        RedissonSpringCacheManager.class
})
@ConditionalOnProperty(prefix = "spring.cache", name = "type", havingValue = "redis")
public class RedissonCacheConfig {

    /**
     * Redisson 事务管理器
     * 用于支持 Redis 事务操作
     *
     * @param redissonClient Redisson 客户端
     * @return RedissonTransactionManager
     */
    @Bean
    @ConditionalOnMissingBean(name = "redissonTransactionManager")
    public PlatformTransactionManager redissonTransactionManager(RedissonClient redissonClient) {
        return new RedissonTransactionManager(redissonClient);
    }

    /**
     * Redisson 缓存管理器
     * 使用 TransactionAwareCacheManagerProxy 包装，支持 Spring 事务
     * 确保缓存操作与数据库事务保持一致（事务提交后才写入缓存）
     *
     * @param redissonClient Redisson 客户端
     * @return CacheManager
     */
    @Bean
    @ConditionalOnMissingBean(CacheManager.class)
    public CacheManager redissonCacheManager(RedissonClient redissonClient) {
        RedissonSpringCacheManager cacheManager = new RedissonSpringCacheManager(redissonClient);
        // 使用 TransactionAwareCacheManagerProxy 包装，支持事务
        return new TransactionAwareCacheManagerProxy(cacheManager);
    }
}
