package com.lazy.lazyadmin.domain.dto;

import lombok.Data;

/**
 * 登录DTO
 */
@Data
public class LoginDto {

    /**
     * 账号
     */
    private String account;

    /**
     * 密码
     */
    private String password;
}
