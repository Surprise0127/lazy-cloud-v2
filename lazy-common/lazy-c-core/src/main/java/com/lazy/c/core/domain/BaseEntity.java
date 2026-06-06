package com.lazy.c.core.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;


/**
 * BaseEntity基础表
 * <p>
 * 所有 Entity 必须继承该类（同时也要保证表全都包含以下字段）
 *
 * @author Surprise0127
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString()
@EqualsAndHashCode(callSuper = false)
public class BaseEntity {

    /**
     * 创建者（只在插入时填充）
     */
    @Schema(hidden = true)
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private Long createBy;

    /**
     * 创建时间（只在插入时填充）
     */
    @Schema(hidden = true)
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新者（插入和更新时都填充）
     */
    @Schema(hidden = true)
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    /**
     * 更新时间（插入和更新时都填充）
     */
    @Schema(hidden = true)
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标志（0=未删除，1=已删除）
     * <p>
     * 1-表示当前数据不存在
     * 0-表示当前数据存在
     */
    @Schema(hidden = true)
    @TableField(value = "del_flag")
    @TableLogic
    private Integer delFlag;

}
