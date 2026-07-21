package com.lazy.admin.model;

import com.lazy.shared.model.AggregateRoot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;

/**
 * 系统用户（领域聚合根）
 * <p>
 * 核心用户实体，管理用户身份和认证信息。
 * 不包含任何持久化框架注解，保持领域纯净。
 *
 * @author Surprise0127
 * @since 1.0.0
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysUser extends AggregateRoot<Long> {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户名（登录账号）
     */
    private String username;

    /**
     * 加密后的密码
     */
    private String password;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 租户ID（值对象引用，不直接持有 SysTenant 聚合根）
     */
    private Long tenantId;

    /**
     * 账号状态
     * <p>
     * 0=禁用，1=正常，2=锁定
     */
    private Integer status;

    /**
     * 昵称/显示名称
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String avatar;

    // ==================== 业务方法 ====================

    /**
     * 激活账号
     */
    public void activate() {
        this.status = 1;
    }

    /**
     * 禁用账号
     */
    public void disable() {
        this.status = 0;
    }

    /**
     * 锁定账号
     */
    public void lock() {
        this.status = 2;
    }

    /**
     * 判断账号是否正常
     *
     * @return true=正常可用
     */
    public boolean isActive() {
        return this.status != null && this.status == 1;
    }

    /**
     * 修改密码（传入已加密的密码）
     *
     * @param encodedPassword 加密后的密码
     */
    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

}
