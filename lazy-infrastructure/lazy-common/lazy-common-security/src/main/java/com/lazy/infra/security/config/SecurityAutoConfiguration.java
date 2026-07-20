package com.lazy.infra.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lazy.infra.core.util.YamlPropertySourceFactory;
import com.lazy.infra.security.config.properties.JwtProperty;
import com.lazy.infra.security.filter.JwtAuthenticationFilter;
import com.lazy.infra.security.handler.AccessDeniedHandlerImpl;
import com.lazy.infra.security.handler.CertifiedFailedHandlerImpl;
import com.lazy.infra.security.service.TokenBlacklistService;
import com.lazy.infra.security.util.JwtUtils;
import com.lazy.admin.service.IUserPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security自动配置类
 *
 * @author Surprise0127
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass(value = HttpSecurity.class)
@EnableConfigurationProperties({JwtProperty.class})
@PropertySource(value = "classpath:application-c-security.yaml", factory = YamlPropertySourceFactory.class)
public class SecurityAutoConfiguration {

    private final JwtProperty jwtProperty;

    public SecurityAutoConfiguration(JwtProperty jwtProperty) {
        this.jwtProperty = jwtProperty;
    }

    /**
     * 密码编码器
     * <p>
     * 使用 BCrypt 算法加密密码
     *
     * @return PasswordEncoder
     */
    @Bean
    @ConditionalOnMissingBean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * JWT 工具类
     *
     * @return JwtUtils
     */
    @Bean
    @ConditionalOnMissingBean
    public JwtUtils jwtUtils() {
        return new JwtUtils(jwtProperty);
    }

    /**
     * Token 黑名单服务（默认空实现）
     * <p>
     * 业务模块可提供基于 Redis 的实现来覆盖此 Bean
     *
     * @return TokenBlacklistService
     */
    @Bean
    @ConditionalOnMissingBean
    public TokenBlacklistService tokenBlacklistService() {
        return new TokenBlacklistService() {
            @Override
            public boolean isBlacklisted(String jti) {
                return false;
            }

            @Override
            public void addToBlacklist(String jti, long remainingSeconds) {
                log.warn("TokenBlacklistService 未配置实现，Token 注销功能不可用");
            }
        };
    }

    /**
     * 用户权限加载服务（领域服务适配）
     * <p>
     * 默认空实现，业务模块可通过实现 {@link IUserPermissionService} 并注入
     * {@link com.lazy.admin.port.outgoing.ISysUserRepository} 来覆盖此 Bean。
     *
     * @return IUserPermissionService 默认实现
     */
    @Bean
    @ConditionalOnMissingBean
    public IUserPermissionService userPermissionService() {
        return new IUserPermissionService() {
            // 使用接口默认实现（返回空权限集合）
        };
    }

    /**
     * JWT 认证过滤器
     *
     * @param jwtUtils              JWT 工具类
     * @param tokenBlacklistService Token 黑名单服务
     * @param userPermissionService 用户权限加载服务
     * @return JwtAuthenticationFilter
     */
    @Bean
    @ConditionalOnMissingBean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtils jwtUtils,
                                                           TokenBlacklistService tokenBlacklistService,
                                                           IUserPermissionService userPermissionService) {
        return new JwtAuthenticationFilter(jwtUtils, tokenBlacklistService, userPermissionService);
    }

    /**
     * 认证失败处理器（401）
     *
     * @param objectMapper JSON 序列化
     * @return AuthenticationEntryPoint
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthenticationEntryPoint authenticationEntryPoint(ObjectMapper objectMapper) {
        return new CertifiedFailedHandlerImpl(objectMapper);
    }

    /**
     * 访问拒绝处理器（403）
     *
     * @param objectMapper JSON 序列化
     * @return AccessDeniedHandler
     */
    @Bean
    @ConditionalOnMissingBean
    public AccessDeniedHandler accessDeniedHandler(ObjectMapper objectMapper) {
        return new AccessDeniedHandlerImpl(objectMapper);
    }

    /**
     * 安全过滤链配置
     *
     * @param http                     HttpSecurity
     * @param jwtAuthenticationFilter  JWT 认证过滤器
     * @param authenticationEntryPoint 认证失败处理器
     * @param accessDeniedHandler      访问拒绝处理器
     * @return SecurityFilterChain
     * @throws Exception Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter,
                                                   AuthenticationEntryPoint authenticationEntryPoint,
                                                   AccessDeniedHandler accessDeniedHandler) throws Exception {

        String[] ignoreUrls = jwtProperty.getIgnoreUrls().toArray(new String[0]);

        return http
                // 禁用CSRF，使用JWT
                .csrf(AbstractHttpConfigurer::disable)

                // 启用CORS
                .cors(Customizer.withDefaults())

                // 无状态会话管理
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 禁用默认登录方式
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)

                // 添加 JWT 认证过滤器（在 UsernamePasswordAuthenticationFilter 之前）
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // 配置异常处理
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))

                // 配置请求授权
                .authorizeHttpRequests(auth -> auth
                        // 白名单URL
                        .requestMatchers(ignoreUrls).permitAll()
                        // 其他请求需要认证
                        .anyRequest().authenticated())
                .build();
    }

}
