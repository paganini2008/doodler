package com.github.doodler.common.mybatis.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.apache.ibatis.type.LocalDateTimeTypeHandler;

/**
 * @Description: TimestampLocalDateTimeTypeHandler
 * @Author: Fred Feng
 * @Date: 23/12/2022
 * @Version 1.0.0
 */
public class TimestampLocalDateTimeTypeHandler extends LocalDateTimeTypeHandler {

    @Override
    public LocalDateTime getResult(ResultSet rs, String columnName) throws SQLException {
        Object object = rs.getObject(columnName);
        if (object instanceof Timestamp) {
            return LocalDateTime
                    .ofInstant(((Timestamp) object).toInstant(), ZoneOffset.ofHours(0));
        }
        return super.getResult(rs, columnName);
    }
}