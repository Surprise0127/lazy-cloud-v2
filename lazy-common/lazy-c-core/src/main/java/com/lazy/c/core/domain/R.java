package com.lazy.c.core.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serial;
import java.io.Serializable;

/**
 * 统一响应类
 * <p>
 * 仅应用于非流式接口
 *
 * @param code      响应状态码
 * @param msg       响应消息
 * @param data      响应数据
 * @param timestamp 响应时间戳
 * @param <T>       T
 * @author Surprise0127
 * @since 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record R<T>(
        int code,
        String msg,
        T data,
        long timestamp
) implements Serializable {

    @Serial
    private static final long serialVersionUID = -820262775215959015L;

    public static R<Void> ok() {
        return R.ok(null);
    }

    public static <T> R<T> ok(T data) {
        return new R<>(200, "操作成功", data, System.currentTimeMillis());
    }

    public static R<Void> fail() {
        return R.fail("操作失败");
    }

    public static R<Void> fail(String msg) {
        return R.fail(msg, null);
    }

    public static <T> R<T> fail(String msg, T data) {
        return new R<>(500, msg, data, System.currentTimeMillis());
    }

    public static R<Void> of(int code, String msg) {
        return R.of(code, msg, null);
    }

    public static <T> R<T> of(int code, String msg, T data) {
        return new R<>(code, msg, data, System.currentTimeMillis());
    }
}