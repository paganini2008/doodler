package com.github.doodler.common.jdbc.impexp;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;
import com.github.doodler.common.jdbc.ConnectionFactory;
import com.github.doodler.common.jdbc.JdbcUtils;

/**
 * @Description: PooledConnectionFactory
 * @Author: Fred Feng
 * @Date: 25/03/2023
 * @Version 1.0.0
 */
public class PooledConnectionFactory implements ConnectionFactory {

	private final DataSource dataSource;

	public PooledConnectionFactory(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	@Override
	public void close(Connection connection) throws SQLException {
		JdbcUtils.closeQuietly(connection);
	}

	@Override
	public String toString() {
		return dataSource.toString();
	}
}