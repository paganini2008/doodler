/**
 * Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.doodler.common.jdbc;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import com.github.doodler.common.jdbc.page.DefaultPageContent;
import com.github.doodler.common.jdbc.page.PageContent;
import com.github.doodler.common.jdbc.page.PageReader;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @Description: NamedParameterJdbcPageReader
 * @Author: Fred Feng
 * @Date: 31/08/2024
 * @Version 1.0.0
 */
public class NamedParameterJdbcPageReader<T> implements PageReader<T> {

    private final String countSql;
    private final String paginationSql;
    private final SqlParameterSource sqlParameterSource;
    private final RowMapper<T> rowMapper;
    private final NamedParameterJdbcOperations jdbcOperations;

    public NamedParameterJdbcPageReader(String countSql, String paginationSql,
            SqlParameterSource sqlParameterSource, RowMapper<T> rowMapper,
            NamedParameterJdbcOperations jdbcOperations) {
        this.countSql = countSql;
        this.paginationSql = paginationSql;
        this.sqlParameterSource = sqlParameterSource;
        this.rowMapper = rowMapper;
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public long rowCount() {
        if (StringUtils.isBlank(countSql)) {
            return Integer.MAX_VALUE;
        }
        return jdbcOperations.queryForObject(countSql, sqlParameterSource, Long.class);
    }

    @Override
    public PageContent<T> list(int pageNumber, int offset, int limit, Object nextToken) {
        List<T> list = jdbcOperations.query(paginationSql,
                new InternalSqlParameterSource(sqlParameterSource, limit, offset), rowMapper);
        return new DefaultPageContent<>(list, null);
    }

    public NamedParameterJdbcOperations getJdbcOperations() {
        return jdbcOperations;
    }

    @RequiredArgsConstructor
    private static class InternalSqlParameterSource implements SqlParameterSource {

        private final SqlParameterSource source;
        private final int limit;
        private final int offset;

        @Override
        public boolean hasValue(String paramName) {
            return source.hasValue(paramName) || "limit".equalsIgnoreCase(paramName)
                    || "offset".equalsIgnoreCase(paramName)
                    || "startrow".equalsIgnoreCase(paramName)
                    || "endrow".equalsIgnoreCase(paramName);
        }

        @Override
        public Object getValue(String paramName) throws IllegalArgumentException {
            switch (paramName.toLowerCase()) {
                case "limit":
                    return limit;
                case "offset":
                case "startrow":
                    return offset;
                case "endrow":
                    return offset + limit;
                default:
                    return source.getValue(paramName);
            }

        }

    }

}
