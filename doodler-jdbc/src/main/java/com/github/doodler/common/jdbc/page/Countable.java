package com.github.doodler.common.jdbc.page;

import java.sql.SQLException;

/**
 * @Description: Countable
 * @Author: Fred Feng
 * @Date: 08/01/2020
 * @Version 1.0.0
 */
public interface Countable {

	 default long rowCount() throws SQLException {
		 return Integer.MAX_VALUE;
	 }
}