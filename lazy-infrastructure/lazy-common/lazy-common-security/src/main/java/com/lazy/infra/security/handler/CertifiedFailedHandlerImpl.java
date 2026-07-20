package com.lazy.infra.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lazy.shared.model.R;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 认证失败处理器
 * <p>
 * 当用户未认证或 Token 无效时，返回 401 状态码
 *
 * @author Surprise0127
 * @since 1.0.0
 */
@Slf4j
public class CertifiedFailedHandlerImpl implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public CertifiedFailedHandlerImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        log.debug("认证失败: {}", authException.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        R<?> result = R.of(401, "认证失败,请重新登录");

        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
