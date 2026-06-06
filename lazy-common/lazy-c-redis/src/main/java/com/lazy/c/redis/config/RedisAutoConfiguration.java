package com.lazy.c.redis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.CompositeCodec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * Redis 客户端自动配置
 * <p>
 * 使用独立的 ObjectMapper（不复用 Web 层的 Jackson 配置），
 * 避免 Long→String 等前端序列化规则污染 Redis 存储。
 *
 * @author Surprise0127
 * @since 1.0.0
 */
@EnableCaching
@AutoConfiguration
@Import({
        CaffeineCacheConfig.class,
        RedissonCacheConfig.class,
})
public class RedisAutoConfiguration {

    /**
     * Redisson 自动配置定制器
     * <p>
     * 编码策略：
     * <ul>
     *   <li>Key：{@link StringCodec}（可读字符串）</li>
     *   <li>Value / HashValue：{@link JsonJacksonCodec}（JSON，携带类型信息以支持反序列化）</li>
     * </ul>
     *
     * @return RedissonAutoConfigurationCustomizer
     */
    @Bean
    @ConditionalOnClass(value = {RedissonClient.class})
    @ConditionalOnMissingBean(RedissonAutoConfigurationCustomizer.class)
    public RedissonAutoConfigurationCustomizer redissonCustomizer() {

        return config -> {
            // 独立的 ObjectMapper —— 仅用于 Redis 序列化，不受 Web 层配置影响
            ObjectMapper objectMapper = new ObjectMapper();

            // Java 8 日期时间支持
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            // 宽松反序列化：忽略未知字段、空 Bean 不报错
            objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

            // 字段可见性：允许序列化所有字段（包括私有字段）
            objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

            // 类型信息：仅对非具体类型（接口/抽象类）和数组写入类型标识
            // 避免 NON_FINAL 为每个非 final 类都加 ["java.util.HashSet",...] 前缀
            objectMapper.activateDefaultTyping(
                    LaissezFaireSubTypeValidator.instance,
                    ObjectMapper.DefaultTyping.NON_CONCRETE_AND_ARRAYS
            );

            JsonJacksonCodec jsonCodec = new JsonJacksonCodec(objectMapper);

            // 复合编码器：key 可读，value 为 JSON
            CompositeCodec codec = new CompositeCodec(
                    StringCodec.INSTANCE, // key 使用字符串
                    jsonCodec,            // value 使用 JSON 编码器
                    jsonCodec             // hashValue 使用 JSON 编码器
            );

            config.setUseScriptCache(true)
                    .setCodec(codec);
        };
    }

}
