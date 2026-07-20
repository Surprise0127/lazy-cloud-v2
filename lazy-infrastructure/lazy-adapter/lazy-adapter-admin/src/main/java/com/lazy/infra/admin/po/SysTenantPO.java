package com.lazy.infra.admin.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lazy.infra.mybatis.po.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 系统租户持久化对象
 * <p>
 * 映射数据库表 sys_tenant
 *
 * @author Surprise0127
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_tenant")
public class SysTenantPO extends BasePO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 租户名称
     */
    @TableField("tenant_name")
    private String tenantName;

    /**
     * 租户编码（唯一标识）
     */
    @TableField("tenant_code")
    private String tenantCode;

    /**
     * 租户状态（0=禁用，1=正常，2=过期）
     */
    @TableField("status")
    private Integer status;

    /**
     * 联系人姓名
     */
    @TableField("contact_name")
    private String contactName;

    /**
     * 联系人手机号
     */
    @TableField("contact_phone")
    private String contactPhone;

    /**
     * 租户描述
     */
    @TableField("description")
    private String description;

}
