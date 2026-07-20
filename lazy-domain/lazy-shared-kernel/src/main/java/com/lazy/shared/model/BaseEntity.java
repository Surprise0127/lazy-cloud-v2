package com.lazy.shared.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 领域层基础实体（纯净，无任何持久化框架注解）
 * <p>
 * 所有领域实体必须继承该类，泛型 ID 支持不同类型的聚合根标识。
 *
 * @param <ID> 实体标识类型
 * @author Surprise0127
 * @since 1.0.0
 */
@Getter
@Setter
public abstract class BaseEntity<ID> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID（泛型，支持 Long / String / UUID 等）
     */
    private ID id;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标志
     * <p>
     * 0=未删除，1=已删除
     */
    private Integer delFlag;

}
