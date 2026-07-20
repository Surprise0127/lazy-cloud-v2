package com.lazy.infra.json.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static com.lazy.infra.json.constants.DateFormatConstant.*;


/**
 * JSON自动配置类
 *
 * @author Surprise0127
 * @since 1.0.0
 */
@AutoConfiguration
public class JsonAutoConfiguration {

    /**
     * Jackson2 对象映射生成器定制器
     *
     * @return Jackson2ObjectMapperBuilderCustomizer
     */
    @Bean
    @ConditionalOnClass(ObjectMapper.class)
    @ConditionalOnMissingBean(Jackson2ObjectMapperBuilderCustomizer.class)
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {

        return jacksonObjectMapperBuilder -> {

            // 1. 基础配置
            jacksonObjectMapperBuilder
                    // 设置时区
                    .timeZone(TIMEZONE)
                    .featuresToEnable( // 启用以下特性
                            // 允许使用单引号包裹字段名和值
                            JsonParser.Feature.ALLOW_SINGLE_QUOTES,
                            // 允许字段名（key）不使用引号
                            JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES,
                            // 当 JSON 为空时，解析为 null
                            DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT
                    )
                    .featuresToDisable( // 禁用以下特性
                            // 禁用时间戳格式
                            SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                            // 当 JSON 为空时，抛出异常
                            SerializationFeature.FAIL_ON_EMPTY_BEANS,
                            // 当 JSON 有未知字段时，抛出异常
                            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
                    );

            // 2. 日期时间配置（简化版）
            JavaTimeModule javaTimeModule = new JavaTimeModule();

            // LocalDateTime
            javaTimeModule.addSerializer(LocalDateTime.class,
                    new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATETIME_PATTERN)));
            javaTimeModule.addDeserializer(LocalDateTime.class,
                    new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATETIME_PATTERN)));

            // LocalDate
            javaTimeModule.addSerializer(LocalDate.class,
                    new LocalDateSerializer(DateTimeFormatter.ofPattern(DATE_PATTERN)));
            javaTimeModule.addDeserializer(LocalDate.class,
                    new LocalDateDeserializer(DateTimeFormatter.ofPattern(DATE_PATTERN)));

            // LocalTime
            javaTimeModule.addSerializer(LocalTime.class,
                    new LocalTimeSerializer(DateTimeFormatter.ofPattern(TIME_PATTERN)));
            javaTimeModule.addDeserializer(LocalTime.class,
                    new LocalTimeDeserializer(DateTimeFormatter.ofPattern(TIME_PATTERN)));

            // 3. 数字类型序列化配置
            SimpleModule numberModule = new SimpleModule();
            // Long
            numberModule.addSerializer(Long.class, ToStringSerializer.instance);
            numberModule.addSerializer(long.class, ToStringSerializer.instance);

            // Double/Float — 用 BigDecimal.toPlainString() 避免科学计数法
            StdSerializer<Double> doubleSerializer = new StdSerializer<>(Double.class) {
                @Override
                public void serialize(Double value, JsonGenerator gen, SerializerProvider provider) {
                    try {
                        if (value == null) {
                            gen.writeNull();
                        } else {
                            gen.writeString(BigDecimal.valueOf(value).toPlainString());
                        }
                    } catch (java.io.IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            numberModule.addSerializer(Double.class, doubleSerializer);
            numberModule.addSerializer(double.class, doubleSerializer);

            StdSerializer<Float> floatSerializer = new StdSerializer<>(Float.class) {
                @Override
                public void serialize(Float value, JsonGenerator gen, SerializerProvider provider) {
                    try {
                        if (value == null) {
                            gen.writeNull();
                        } else {
                            gen.writeString(BigDecimal.valueOf(value).toPlainString());
                        }
                    } catch (java.io.IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            numberModule.addSerializer(Float.class, floatSerializer);
            numberModule.addSerializer(float.class, floatSerializer);

            // BigInteger
            numberModule.addSerializer(BigInteger.class, ToStringSerializer.instance);
            // BigDecimal
            numberModule.addSerializer(BigDecimal.class, ToStringSerializer.instance);

            // 4. 使用modules注册相关模块
            jacksonObjectMapperBuilder.modules(numberModule, javaTimeModule);
        };
    }
}
