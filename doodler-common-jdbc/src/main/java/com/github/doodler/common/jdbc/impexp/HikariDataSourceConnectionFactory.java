package com.github.doodler.common.jdbc.impexp;

import java.sql.Connection;
import java.sql.SQLException;
import com.github.doodler.common.jdbc.ConnectionFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * @Description: HikariDataSourceConnectionFactory
 * @Author: Fred Feng
 * @Date: 31/03/2023
 * @Version 1.0.0
 */
public class HikariDataSourceConnectionFactory implements ConnectionFactory {

    public HikariDataSourceConnectionFactory(String driverClassName, String jdbcUrl, String username, String password) {
        this.dataSource = createDefaultDataSource(driverClassName, jdbcUrl, username, password);
    }

    private final HikariDataSource dataSource;

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void destroy() {
        dataSource.close();
    }

    private HikariDataSource createDefaultDataSource(String driverClassName, String jdbcUrl, String username,
                                                     String password) {
        final HikariConfig config = new HikariConfig();
        config.setDriverClassName(driverClassName);
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setMinimumIdle(1);
        config.setMaximumPoolSize(20);
        config.setMaxLifetime(60 * 1000);
        config.setIdleTimeout(60 * 1000);
        config.setValidationTimeout(3000);
        config.setReadOnly(false);
        config.setAutoCommit(true);
        config.setConnectionTestQuery("SELECT 1");
        config.setConnectionTimeout(60 * 1000);
        return new HikariDataSource(config);
    }
}