package com.github.doodler.common.page;

import java.sql.SQLException;

/**
 * @Description: Countable
 * @Author: Fred Feng
 * @Date: 08/03/2023
 * @Version 1.0.0
 */
public interface Countable {

    default long rowCount() throws SQLException {
        return Integer.MAX_VALUE;
    }
}