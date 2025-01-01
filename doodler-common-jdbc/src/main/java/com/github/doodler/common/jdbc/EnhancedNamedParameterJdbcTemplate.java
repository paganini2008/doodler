package com.github.doodler.common.jdbc;

import java.util.Map;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import com.github.doodler.common.page.PageReader;

/**
 * 
 * @Description: EnhancedNamedParameterJdbcTemplate
 * @Author: Fred Feng
 * @Date: 31/08/2024
 * @Version 1.0.0
 */
@Component
public class EnhancedNamedParameterJdbcTemplate extends NamedParameterJdbcTemplate {

    public EnhancedNamedParameterJdbcTemplate(JdbcOperations jdbcOperations) {
        super(jdbcOperations);
    }

    public <T> PageReader<Map<String, Object>> pageQuery(String sql,
            SqlParameterSource sqlParameterSource) {
        String countSql = String.format("select count(1) as rowCount from (%s) as this", sql);
        String paginationSql = String.format("%s limit :limit offset :offset", sql);
        return new NamedParameterJdbcPageReader<Map<String, Object>>(countSql, paginationSql,
                sqlParameterSource, new NamedColumnMapRowMapper(), this);
    }

    public <T> PageReader<Map<String, Object>> pageQuery(String countSql, String paginationSql,
            SqlParameterSource sqlParameterSource) {
        return new NamedParameterJdbcPageReader<Map<String, Object>>(countSql, paginationSql,
                sqlParameterSource, new NamedColumnMapRowMapper(), this);
    }

    public <T> PageReader<T> pageQuery(String sql, SqlParameterSource sqlParameterSource,
            Class<T> elementClass) {
        String countSql = String.format("select count(1) as rowCount from (%s) as this", sql);
        String paginationSql = String.format("%s limit :limit offset :offset", sql);
        return new NamedParameterJdbcPageReader<T>(countSql, paginationSql, sqlParameterSource,
                new SingleColumnRowMapper<T>(elementClass), this);
    }

    public <T> PageReader<T> pageQuery(String countSql, String paginationSql,
            SqlParameterSource sqlParameterSource, Class<T> elementClass) {
        return new NamedParameterJdbcPageReader<T>(countSql, paginationSql, sqlParameterSource,
                new SingleColumnRowMapper<T>(elementClass), this);
    }

    public <T> PageReader<T> pageQuery(String sql, SqlParameterSource sqlParameterSource,
            RowMapper<T> rowMapper) {
        String countSql = String.format("select count(1) as rowCount from (%s) as this", sql);
        String paginationSql = String.format("%s limit :limit offset :offset", sql);
        return new NamedParameterJdbcPageReader<T>(countSql, paginationSql, sqlParameterSource,
                rowMapper, this);
    }

    public <T> PageReader<T> pageQuery(String countSql, String paginationSql,
            SqlParameterSource sqlParameterSource, RowMapper<T> rowMapper) {
        return new NamedParameterJdbcPageReader<T>(countSql, paginationSql, sqlParameterSource,
                rowMapper, this);
    }

}
