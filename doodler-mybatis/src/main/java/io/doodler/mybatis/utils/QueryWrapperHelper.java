package io.doodler.mybatis.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.CaseFormat;
import java.lang.reflect.Field;
import org.apache.commons.lang3.StringUtils;

/**
 * @author John Chen
 */
public class QueryWrapperHelper {

    public static <T> QueryWrapper<T> initQueryWrapper(Object dto) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        if (null == dto) {
            return queryWrapper;
        }
        Field[] fields = dto.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            QueryCondition condition = field.getAnnotation(QueryCondition.class);
            try {
                Object value = field.get(dto);
                String fieldName = field.getName();
                if (null != value && StringUtils.isNotBlank(value.toString()) && null != condition) {
                    addConditionToSql(condition.op(), condition.field(), fieldName, value, queryWrapper);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return queryWrapper;
    }

    private static String getDbField(String dtoFieldName, String field) {
        if (StringUtils.isNotBlank(field)) {
            return field;
        }
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, dtoFieldName);
    }

    private static void addConditionToSql(WrapperOp op, String dbField, String dtoFieldName, Object value,
                                          QueryWrapper<?> queryWrapper) {
        switch (op) {
            case EQ:
                queryWrapper.eq(getDbField(dtoFieldName, dbField), value);
                break;
            case GT:
                queryWrapper.gt(getDbField(dtoFieldName, dbField), value);
                break;
            case GE:
                queryWrapper.ge(getDbField(dtoFieldName, dbField), value);
                break;
            case LT:
                queryWrapper.lt(getDbField(dtoFieldName, dbField), value);
                break;
            case LE:
                queryWrapper.le(getDbField(dtoFieldName, dbField), value);
                break;
            case LIKE:
                queryWrapper.like(getDbField(dtoFieldName, dbField), value);
                break;
            case LIKE_LEFT:
                queryWrapper.likeLeft(getDbField(dtoFieldName, dbField), value);
                break;
            case LIKE_RIGHT:
                queryWrapper.likeRight(getDbField(dtoFieldName, dbField), value);
                break;
            case NOT_LIKE:
                queryWrapper.notLike(getDbField(dtoFieldName, dbField), value);
                break;
            case NE:
                queryWrapper.ne(getDbField(dtoFieldName, dbField), value);
                break;
            default:
        }
    }
}