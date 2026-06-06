package com.lazy.c.security.util;

import com.lazy.c.security.config.properties.JwtProperty;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * JWT 工具类
 * <p>
 * 基于 JJWT 0.12.x 官方 API，提供 Token 的生成、解析、验证能力。
 */
@Slf4j
public class JwtUtils {

    /**
     * Token 类型 Claim Key（区分 access / refresh）
     */
    public static final String TOKEN_TYPE = "TypeOfToken";

    /**
     * Access Token 类型标识
     */
    public static final String TOKEN_TYPE_ACCESS = "access";

    /**
     * Refresh Token 类型标识
     */
    public static final String TOKEN_TYPE_REFRESH = "refresh";

    /**
     * 签发者
     */
    private final String issuer;

    /**
     * Access Token 过期时间
     */
    private final Long accessTokenExpire;

    /**
     * Refresh Token 过期时间
     */
    private final Long refreshTokenExpire;

    /**
     * 密钥
     */
    private final SecretKey signingKey;

    public JwtUtils(JwtProperty jwtProperty) {
        this.issuer = jwtProperty.getIssuer();
        this.accessTokenExpire = jwtProperty.getAccessTokenExpire();
        this.refreshTokenExpire = jwtProperty.getRefreshTokenExpire();
        this.signingKey = Keys.hmacShaKeyFor(jwtProperty.getSecret().getBytes(StandardCharsets.UTF_8));
    }


    /**
     * 生成 Access Token
     *
     * @param uniqueIdentifier 用户唯一标识符
     * @param claims           自定义 Claims
     * @return Access Token 字符串
     */
    public String generateAccessToken(Long uniqueIdentifier, Map<String, Object> claims) {
        return buildToken(
                uniqueIdentifier,
                claims,
                TOKEN_TYPE_ACCESS,
                accessTokenExpire
        );
    }

    /**
     * 生成 Refresh Token
     * <p>
     * Refresh Token 仅携带最小信息（uniqueIdentifier），不包含业务 Claims
     *
     * @param uniqueIdentifier 用户唯一标识符
     * @return Refresh Token 字符串
     */
    public String generateRefreshToken(Long uniqueIdentifier) {
        return buildToken(
                uniqueIdentifier,
                new HashMap<>(),
                TOKEN_TYPE_REFRESH,
                refreshTokenExpire
        );
    }

    /**
     * 解析并验证 Token，返回 Claims
     * <p>
     * 如果 Token 无效（过期、篡改、格式错误等），将抛出相应异常。
     *
     * @param token JWT 字符串（不含 "Bearer " 前缀）
     * @return Claims 载荷
     * @throws ExpiredJwtException Token 已过期
     * @throws JwtException        Token 无效（签名错误、格式错误等）
     */
    public Claims parseToken(String token) {

        return Jwts.parser()
                .verifyWith(signingKey)
                .clockSkewSeconds(30)
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 从 HTTP Authorization 请求头中解析出 JWT Token
     * <p>
     * 输入字符串应为 {@code "Bearer <token>"} 格式，方法会移除 "Bearer " 前缀，
     * 返回纯 JWT 字符串。若输入为 null、不以 "Bearer " 开头，或去除前缀后为空字符串，
     * 则返回 null。
     *
     * @param authorizationHeader Authorization 请求头的原始值（可为 null）
     * @return 纯 JWT 字符串，若无效则返回 null
     */
    public String resolveToken(String authorizationHeader) {
        return (authorizationHeader != null && authorizationHeader.startsWith("Bearer "))
                ? authorizationHeader.substring(7) : null;
    }

    /**
     * 从 Claims 中获取 Token 类型（access / refresh）
     *
     * @param claims JWT 载荷（不可为 null）
     * @return Token 类型字符串（{@link #TOKEN_TYPE_ACCESS} 或 {@link #TOKEN_TYPE_REFRESH}），
     * 若 claims 为 null 或不包含该 claim，则返回 null
     */
    public String getTokenType(Claims claims) {
        if (claims == null) {
            return null;
        }
        Object type = claims.get(TOKEN_TYPE);
        return type instanceof String ? (String) type : null;
    }

    /**
     * 从 Claims 中获取 JWT ID（JTI）
     *
     * @param claims JWT 载荷（不可为 null）
     * @return JWT ID 字符串，若 claims 为 null 或不包含该 claim，则返回 null
     */
    public String getJti(Claims claims) {
        if (claims == null) {
            return null;
        }
        Object jti = claims.get(Claims.ID);
        return jti instanceof String ? (String) jti : null;
    }

    /**
     * 构建 JWT Token
     *
     * @param uniqueIdentifier 用户唯一标识符
     * @param extraClaims      额外的自定义 Claims
     * @param tokenType        Token 类型标识
     * @param expireSeconds    过期时间（秒）
     * @return 签名后的 JWT 字符串
     */
    private String buildToken(Long uniqueIdentifier,
                              Map<String, Object> extraClaims,
                              String tokenType,
                              Long expireSeconds) {

        Date now = new Date();
        Date expiration = new Date(now.getTime() + expireSeconds * 1000);

        var builder = Jwts.builder()
                .id(UUID.randomUUID().toString().replace("-", ""))
                // 标准 Claims
                .issuer(issuer)
                .subject(String.valueOf(uniqueIdentifier))
                .issuedAt(now)
                .expiration(expiration)
                // 自定义 Claims
                .claim(TOKEN_TYPE, tokenType);

        // 额外的自定义 Claims，过滤掉所有保留 key
        if (extraClaims != null && !extraClaims.isEmpty()) {
            Set<String> reservedKeys = Set.of(
                    Claims.ISSUER,          // "iss"
                    Claims.SUBJECT,         // "sub"
                    Claims.EXPIRATION,      // "exp"
                    Claims.NOT_BEFORE,      // "nbf"
                    Claims.ISSUED_AT,       // "iat"
                    Claims.ID,              // "jti"
                    TOKEN_TYPE              // "TypeOfToken"
            );
            extraClaims.entrySet().stream()
                    .filter(e -> !reservedKeys.contains(e.getKey()))
                    .forEach(e -> builder.claim(e.getKey(), e.getValue()));
        }

        // 签名并生成
        return builder.signWith(signingKey).compact();
    }
}
