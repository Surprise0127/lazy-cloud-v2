package com.lazy.admin.port.outgoing;

import com.lazy.admin.model.SysTenant;

import java.util.Optional;

/**
 * 系统租户仓储接口（领域层）
 * <p>
 * 定义租户持久化的契约，由基础设施层实现。
 *
 * @author Surprise0127
 * @since 1.0.0
 */
public interface ISysTenantRepository {

    /**
     * 保存租户（新增或更新）
     *
     * @param tenant 租户聚合根
     * @return 保存后的租户
     */
    SysTenant save(SysTenant tenant);

    /**
     * 根据ID查询租户
     *
     * @param id 租户ID
     * @return 租户聚合根（可能为空）
     */
    Optional<SysTenant> findById(Long id);

    /**
     * 根据租户编码查询租户
     *
     * @param tenantCode 租户编码
     * @return 租户聚合根（可能为空）
     */
    Optional<SysTenant> findByTenantCode(String tenantCode);

}
