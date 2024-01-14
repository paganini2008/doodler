package com.github.doodler.common.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 
 * @Description: PreparedStatementCallback
 * @Author: Fred Feng
 * @Date: 24/01/2020
 * @Version 1.0.0
 */
@FunctionalInterface
public interface PreparedStatementCallback {

	void setValues(PreparedStatement ps) throws SQLException;
	
}
