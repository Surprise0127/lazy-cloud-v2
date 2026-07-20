package com.lazy.admin.model;

import com.lazy.shared.model.AggregateRoot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;

/**
 * 系统租户（领域聚合根）
 * <p>
 * 多租户核心实体，管理租户基本信息。
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
public class SysTenant extends AggregateRoot<Long> {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 租户名称
     */
    private String tenantName;

    /**
     * 租户编码（唯一标识，用于路由等场景）
     */
    private String tenantCode;

    /**
     * 租户状态
     * <p>
     * 0=禁用，1=正常，2=过期
     */
    private Integer status;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系人手机号
     */
    private String contactPhone;

    /**
     * 租户描述
     */
    private String description;

}
