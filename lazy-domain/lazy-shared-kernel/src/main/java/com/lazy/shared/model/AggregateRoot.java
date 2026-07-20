package com.lazy.shared.model;

import java.io.Serial;

/**
 * 聚合根基类（DDD 框架层）
 * <p>
 * 所有领域聚合根必须继承此类，泛型 ID 继承自 BaseEntity。
 * 不重复声明 id 字段，避免与 BaseEntity 冲突。
 *
 * @param <ID> 聚合根标识类型
 * @author Surprise0127
 * @since 1.0.0
 */
public abstract class AggregateRoot<ID> extends BaseEntity<ID> {

    @Serial
    private static final long serialVersionUID = 1L;

}
