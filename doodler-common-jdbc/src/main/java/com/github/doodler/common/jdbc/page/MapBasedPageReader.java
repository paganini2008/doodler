package com.github.doodler.common.jdbc.page;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import com.github.doodler.common.jdbc.ConnectionFactory;
import com.github.doodler.common.jdbc.JdbcUtils;
import com.github.doodler.common.jdbc.impexp.PooledConnectionFactory;
import com.github.doodler.common.page.DefaultPageContent;
import com.github.doodler.common.page.PageContent;
import com.github.doodler.common.page.PageReader;

/**
 * @Description: MapBasedPageReader
 * @Author: Fred Feng
 * @Date: 24/03/2023
 * @Version 1.0.0
 */
public class MapBasedPageReader implements PageReader<Map<String, Object>> {

    public MapBasedPageReader(DataSource dataSource, String sql, long maxTotalRecords) {
        this(new PooledConnectionFactory(dataSource), sql, maxTotalRecords);
    }

    public MapBasedPageReader(ConnectionFactory connectionFactory, String sql, long maxTotalRecords) {
        this(connectionFactory, sql, new Object[0], maxTotalRecords);
    }

    public MapBasedPageReader(DataSource dataSource, String sql, Object[] args, long maxTotalRecords) {
        this(new PooledConnectionFactory(dataSource), sql, args, maxTotalRecords);
    }

    public MapBasedPageReader(ConnectionFactory connectionFactory, String sql, Object[] args, long maxTotalRecords) {
        this.connectionFactory = connectionFactory;
        this.sql = sql;
        this.args = args;
        this.maxTotalRecords = maxTotalRecords;
    }

    private final ConnectionFactory connectionFactory;
    private final String sql;
    private final Object[] args;
    private final long maxTotalRecords;

    @Override
    public PageContent<Map<String, Object>> list(int pageNumber, int offset, int limit,
                                                 Object nextToken) throws SQLException {
        Connection connection = null;
        try {
            connection = connectionFactory.getConnection();
            String boundSql = String.format("%s limit %d offset %d", sql, limit, offset);
            List<Map<String, Object>> list = JdbcUtils.fetchAll(connection, boundSql, args);
            return new DefaultPageContent<>(list, null);
        } finally {
            connectionFactory.close(connection);
        }
    }

    @Override
    public long rowCount() throws SQLException {
        if (maxTotalRecords > 0) {
            return Long.min(Integer.MAX_VALUE, maxTotalRecords);
        } else {
            Connection connection = null;
            try {
                connection = connectionFactory.getConnection();
                String countSql = String.format("SELECT COUNT(1) FROM (%s) T", sql);
                return JdbcUtils.fetchOne(connection, countSql, args, Long.class);
            } finally {
                connectionFactory.close(connection);
            }
        }
    }
}