package com.lazy.infra.mybatis.po;

import com.baomidou.mybatisplus.annotation.*;
import com.lazy.shared.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 持久化对象基类（带 MyBatis-Plus 注解）
 * <p>
 * 所有持久化对象 PO 必须继承该类。
 * 字段显式添加 MP 注解，隔离领域层与技术框架。
 *
 * @author Surprise0127
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(autoResultMap = true)
public abstract class BasePO extends BaseEntity<Long> {

    /**
     * 主键ID（雪花算法自动生成）
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 创建时间（插入时自动填充）
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间（插入和更新时自动填充）
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标志
     * <p>
     * 0=未删除，1=已删除
     */
    @TableField(value = "del_flag")
    @TableLogic
    private Integer delFlag;

}
