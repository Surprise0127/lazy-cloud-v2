package com.lazy.c.security.filter;

import com.lazy.c.security.domain.LoginUser;
import com.lazy.c.security.service.IUserPermissionService;
import com.lazy.c.security.service.TokenBlacklistService;
import com.lazy.c.security.util.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 认证过滤器
 * <p>
 * 每个请求都会经过此过滤器，从 Authorization 请求头中提取并验证 JWT Token，
 * 成功后将用户信息设置到 {@link SecurityContextHolder} 中。
 * <p>
 * <b>处理流程：</b>
 * <ol>
 *   <li>从 Authorization 请求头提取 Bearer Token</li>
 *   <li>解析并验证 Token（签名 + 过期时间）</li>
 *   <li>校验 Token 类型为 Access Token（拒绝 Refresh Token 访问资源）</li>
 *   <li>检查 JTI 黑名单（登出后的 Token 不可用）</li>
 *   <li>构建 {@link LoginUser} 并设置 {@link SecurityContextHolder}</li>
 * </ol>
 * <p>
 * Token 无效时不直接写响应，放行让 Spring Security 的
 * {@link org.springframework.security.web.AuthenticationEntryPoint} 统一返回 401。
 *
 * @author Surprise0127
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final TokenBlacklistService tokenBlacklistService;
    private final IUserPermissionService userPermissionService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        // 1. 从请求头提取 Token
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = jwtUtils.resolveToken(bearerToken);

        if (token == null) {
            // 无 Token，放行（白名单路径正常访问，受保护路径由 Spring Security 返回 401）
            filterChain.doFilter(request, response);
            return;
        }

        // 2. 解析并验证 Token
        Claims claims;

        try {
            claims = jwtUtils.parseToken(token);
        } catch (ExpiredJwtException e) {
            log.debug("Token 已过期: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        } catch (JwtException e) {
            log.debug("Token 无效: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        // 3. 校验 Token 类型必须为 Access Token
        String tokenType = jwtUtils.getTokenType(claims);
        if (!JwtUtils.TOKEN_TYPE_ACCESS.equals(tokenType)) {
            log.debug("非 Access Token，拒绝认证: type={}", tokenType);
            filterChain.doFilter(request, response);
            return;
        }

        // 4. 检查 JTI 与 黑名单（Token 是否已被注销，黑名单默认不检查）
        String jti = jwtUtils.getJti(claims);
        if (jti != null && tokenBlacklistService.isBlacklisted(jti)) {
            log.debug("Token 已注销: jti={}", jti);
            filterChain.doFilter(request, response);
            return;
        }

        // 5. 从 Claims 构建 LoginUser
        String uniqueIdentifier = claims.getSubject();
        if (uniqueIdentifier == null || uniqueIdentifier.isBlank()) {
            log.debug("Token 缺少 subject，拒绝认证: subject={}", uniqueIdentifier);
            filterChain.doFilter(request, response);
            return;
        }
        LoginUser loginUser = LoginUser.builder()
                .uniqueIdentifier(uniqueIdentifier)
                .build();

        // 6. 加载用户权限（从 Redis 缓存或数据库）
        userPermissionService.loadPermissions(loginUser, claims);

        // 7. 设置 SecurityContext
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.info("JWT 认证成功: uniqueIdentifier={}", uniqueIdentifier);

        // 8. 继续过滤链
        filterChain.doFilter(request, response);
    }
}
