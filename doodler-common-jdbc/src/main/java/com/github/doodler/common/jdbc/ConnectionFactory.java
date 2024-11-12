package com.github.doodler.common.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @Description: ConnectionFactory
 * @Author: Fred Feng
 * @Date: 25/03/2023
 * @Version 1.0.0
 */
public interface ConnectionFactory {

    Connection getConnection() throws SQLException;
    
    default Connection getConnection(String catalogName, String schemaName) throws SQLException{
    	Connection connection = getConnection();
    	JdbcUtils.setPath(connection, catalogName, schemaName);
    	return connection;
    }

    default void close(Connection connection) throws SQLException{
    	JdbcUtils.close(connection);
    }
    
    default void destroy() {
    }
}