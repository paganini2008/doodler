package io.doodler.jdbc.page;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;

import io.doodler.jdbc.ConnectionFactory;
import io.doodler.jdbc.JdbcUtils;
import io.doodler.jdbc.impexp.PooledConnectionFactory;

/**
 * @Description: ObjectBasedPageReader
 * @Author: Fred Feng
 * @Date: 24/03/2023
 * @Version 1.0.0
 */
public class ObjectBasedPageReader<T> implements PageReader<T> {

    public ObjectBasedPageReader(DataSource dataSource, String sql, Class<T> resultClass, long maxTotalRecords) {
        this(new PooledConnectionFactory(dataSource), sql, resultClass, maxTotalRecords);
    }

    public ObjectBasedPageReader(ConnectionFactory connectionFactory, String sql, Class<T> resultClass,
                                 long maxTotalRecords) {
        this(connectionFactory, sql, new Object[0], resultClass, maxTotalRecords);
    }

    public ObjectBasedPageReader(DataSource dataSource, String sql, Object[] args, Class<T> resultClass,
                                 long maxTotalRecords) {
        this(new PooledConnectionFactory(dataSource), sql, args, resultClass, maxTotalRecords);
    }

    public ObjectBasedPageReader(ConnectionFactory connectionFactory, String sql, Object[] args, Class<T> resultClass,
                                 long maxTotalRecords) {
        this.connectionFactory = connectionFactory;
        this.sql = sql;
        this.args = args;
        this.resultClass = resultClass;
        this.maxTotalRecords = maxTotalRecords;
    }

    private final ConnectionFactory connectionFactory;
    private final String sql;
    private final Object[] args;
    private final Class<T> resultClass;
    private final long maxTotalRecords;

    @Override
    public List<T> list(int pageNumber, int maxResults) throws SQLException {
        Connection connection = null;
        try {
            connection = connectionFactory.getConnection();
            int offset = (pageNumber - 1) * maxResults;
            String boundSql = String.format("%s limit %d offset %d", sql, maxResults, offset);
            return JdbcUtils.fetchAll(connection, boundSql, args, resultClass);
        } finally {
            JdbcUtils.close(connection);
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
                String countSql = String.format("SELECT COUNT(1) FROM (%s)", sql);
                return JdbcUtils.fetchOne(connection, countSql, args, Long.class);
            } finally {
                JdbcUtils.close(connection);
            }
        }
    }
}