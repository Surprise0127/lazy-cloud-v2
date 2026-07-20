package com.lazy.admin.port.outgoing;

import com.lazy.admin.model.SysUser;

import java.util.Optional;

/**
 * 系统用户仓储接口（领域层）
 * <p>
 * 定义用户持久化的契约，由基础设施层实现。
 * 领域服务通过此接口访问用户数据，不直接依赖 Mapper。
 *
 * @author Surprise0127
 * @since 1.0.0
 */
public interface ISysUserRepository {

    /**
     * 保存用户（新增或更新）
     *
     * @param user 用户聚合根
     * @return 保存后的用户
     */
    SysUser save(SysUser user);

    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户聚合根（可能为空）
     */
    Optional<SysUser> findById(Long id);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户聚合根（可能为空）
     */
    Optional<SysUser> findByUsername(String username);

    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户聚合根（可能为空）
     */
    Optional<SysUser> findByPhone(String phone);

}
