package com.lazy.c.security.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.ArrayList;
import java.util.List;

/**
 * JWT 配置属性
 *
 * @author Surprise0127
 * @since 1.0.0
 */
@Data
@RefreshScope
@ConfigurationProperties(prefix = "jwt")
public class JwtProperty {

    /**
     * JWT Secret Key 签发密钥
     * <p>
     * 当前密钥为演示密钥，生产环境请自行从外部加载
     */
    private String secret = "6XJhK6jKif61rssuWi6HB" +
            "KUQKIRDAyKCKBX7C3LEz9erHgweuoMajvjFhr" +
            "MpS4OLFqfjCz43i9JVU6y7Psu06UmY27CgdYY" +
            "i7hdINHZ323RHWX9uDetnq2YJxZfLozKCLJla" +
            "NZHH9hK5Gc9bncc5lYiKUkRkgPuv8L2dMXszI" +
            "eKy2HsG9oM8V4r6E7UZpbKOrTzV14lxCCz4Po" +
            "IuBUuTvtRrOToejKujQLKFG8YlwzHIMvHacLq" +
            "XH8dh8Otd5zp2";

    /**
     * 签发者
     * <p>
     * 当前签发者为演示签发者，生产环境请自行从外部加载
     */
    private String issuer = "lazy-surprise";

    /**
     * Access Token 过期时间（秒）
     * <p>
     * 默认：2 小时（7200 秒）
     */
    private Long accessTokenExpire = 7200L;

    /**
     * Refresh Token 过期时间（秒）
     * <p>
     * 默认：7 天（604800 秒）
     */
    private Long refreshTokenExpire = 604800L;


    /**
     * 忽略鉴权的 URL 列表
     * <p>
     * 匹配这些路径模式的请求将跳过 JWT 认证。
     * 支持 Ant 风格路径匹配（如 {@code /../auth/**}）。
     */
    private List<String> ignoreUrls = new ArrayList<>();
}

