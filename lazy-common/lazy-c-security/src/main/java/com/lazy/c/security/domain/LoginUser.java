package com.lazy.c.security.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 登录用户信息
 * <p>
 * 实现 Spring Security {@link UserDetails} 接口，承载已认证用户的核心信息。
 *
 * @author Surprise0127
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser implements UserDetails, Serializable {

    /**
     * 用户唯一标识符
     */
    private String uniqueIdentifier;

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.uniqueIdentifier;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }
}
