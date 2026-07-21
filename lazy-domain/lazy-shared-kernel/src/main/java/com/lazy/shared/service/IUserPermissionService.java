package com.lazy.shared.service;

import java.util.Collection;
import java.util.Collections;

/**
 * 用户权限加载服务接口（共享内核）
 * <p>
 * 从缓存（Redis）或数据库加载用户权限信息。
 * 各限界上下文通过实现此接口提供具体的权限加载逻辑。
 * <p>
 * 接口签名保持领域纯净，不包含任何 JWT / Security 框架概念。
 *
 * @author Surprise0127
 * @since 1.0.0
 */
public interface IUserPermissionService {

    /**
     * 加载用户权限
     *
     * @param uniqueIdentifier 用户唯一标识（username / phone / userId）
     * @return 权限字符串集合，默认返回空集合
     */
    default Collection<String> loadPermissions(String uniqueIdentifier) {
        return Collections.emptyList();
    }
}
