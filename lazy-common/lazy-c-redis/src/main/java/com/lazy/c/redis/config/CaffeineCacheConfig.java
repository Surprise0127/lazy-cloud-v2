package com.lazy.c.redis.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Caffeine 配置（本地缓存）
 * <p>
 * 条件：
 * - 存在 Caffeine 相关类
 * - 配置 spring.cache.type=caffeine
 * - 未存在其他 CacheManager Bean
 *
 * @author Surprise0127
 * @since 1.0.0
 */
@Configuration
@ConditionalOnClass(value = {
        CaffeineCacheManager.class,
        Caffeine.class
})
@ConditionalOnProperty(prefix = "spring.cache", name = "type", havingValue = "caffeine")
public class CaffeineCacheConfig {

    @Bean
    @ConditionalOnMissingBean(CacheManager.class)
    public CacheManager caffeineCacheManager() {
        return new CaffeineCacheManager();
    }

}
