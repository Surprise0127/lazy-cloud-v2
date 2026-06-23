package com.lazy.c.json.constants;

/**
 * 日期格式常量
 * <p>
 * 当前常量目前只应用于 json 格式化
 *
 * @author Surprise0127
 * @since 1.0.0
 */
public interface DateFormatConstant {

    /**
     * 时区：GMT+8（中国时区）
     */
    String TIMEZONE = "GMT+8";

    /**
     * 日期格式：yyyy-MM-dd
     */
    String DATE_PATTERN = "yyyy-MM-dd";

    /**
     * 时间格式：HH:mm:ss
     */
    String TIME_PATTERN = "HH:mm:ss";

    /**
     * 日期时间格式：yyyy-MM-dd HH:mm:ss
     */
    String DATETIME_PATTERN = DATE_PATTERN + " " + TIME_PATTERN;

}
