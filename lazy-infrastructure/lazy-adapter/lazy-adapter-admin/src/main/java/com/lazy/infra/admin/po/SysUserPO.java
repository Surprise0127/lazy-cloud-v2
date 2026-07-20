package com.lazy.infra.admin.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lazy.infra.mybatis.po.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 系统用户持久化对象
 * <p>
 * 映射数据库表 sys_user
 *
 * @author Surprise0127
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUserPO extends BasePO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户名（登录账号）
     */
    @TableField("username")
    private String username;

    /**
     * 加密后的密码
     */
    @TableField("password")
    private String password;

    /**
     * 手机号
     */
    @TableField("phone")
    private String phone;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 租户ID
     */
    @TableField("tenant_id")
    private Long tenantId;

    /**
     * 账号状态（0=禁用，1=正常，2=锁定）
     */
    @TableField("status")
    private Integer status;

    /**
     * 昵称/显示名称
     */
    @TableField("nickname")
    private String nickname;

    /**
     * 头像URL
     */
    @TableField("avatar")
    private String avatar;

}
