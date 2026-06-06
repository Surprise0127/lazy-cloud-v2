package com.c.mybatis.hander;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.PGobject;

import java.lang.reflect.Field;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 基于Jackson2自定义PgJsonbTypeHandler
 * <p>
 * 该 Handler 只针对 postgresql 的 JSONB
 */
@Slf4j
@MappedTypes({Object.class})
@MappedJdbcTypes(JdbcType.OTHER)
public class PgJsonbTypeHandler extends AbstractJsonTypeHandler<Object> {

    private static ObjectMapper objectMapper;

    public PgJsonbTypeHandler(Class<?> type) {
        super(type);
    }

    public PgJsonbTypeHandler(Class<?> type, Field field) {
        super(type, field);
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper == null ? Instance.OBJECT_MAPPER : objectMapper;
    }

    public static void setObjectMapper(ObjectMapper objectMapper) {
        Assert.notNull(objectMapper, "ObjectMapper should not be null");
        PgJsonbTypeHandler.objectMapper = objectMapper;
    }

    /**
     * 反序列化json
     *
     * @param json json字符串
     * @return T
     */
    @Override
    public Object parse(String json) {
        ObjectMapper objectMapper = getObjectMapper();
        TypeFactory typeFactory = objectMapper.getTypeFactory();
        JavaType javaType = typeFactory.constructType(getFieldType());
        try {
            return objectMapper.readValue(json, javaType);
        } catch (JacksonException e) {
            log.error("deserialize json: {} to {} error ", json, javaType, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 序列化json
     *
     * @param obj 对象信息
     * @return json字符串
     */
    @Override
    public String toJson(Object obj) {
        try {
            return getObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("serialize {} to json error ", obj, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType)
            throws SQLException {
        // 先调用自己的 toJson 序列化
        String json = toJson(parameter);
        PGobject pgObject = new PGobject();
        pgObject.setType("jsonb");
        pgObject.setValue(json);
        ps.setObject(i, pgObject);
    }

    @Override
    public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return unwrapAndParse(rs.getObject(columnName));
    }

    @Override
    public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return unwrapAndParse(rs.getObject(columnIndex));
    }

    @Override
    public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return unwrapAndParse(cs.getObject(columnIndex));
    }

    /**
     * 展开并解析JSON
     * <p>
     * 从 JDBC 结果中提取 JSON 字符串，兼容 PGobject 和普通 String
     *
     * @param value v
     * @return Object
     */
    private Object unwrapAndParse(Object value) {
        if (value == null) return null;
        String jsonStr = switch (value) {
            case PGobject pg -> pg.getValue();
            case String str -> str;
            default -> throw new IllegalArgumentException("Unsupported JSONB value type: " + value.getClass());
        };
        return parse(jsonStr);
    }


    private static class Instance {
        private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    }

}