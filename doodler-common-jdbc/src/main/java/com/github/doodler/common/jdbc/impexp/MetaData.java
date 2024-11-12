package com.github.doodler.common.jdbc.impexp;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Map;

/**
 * @Description: MetaData
 * @Author: Fred Feng
 * @Date: 25/03/2023
 * @Version 1.0.0
 */
public interface MetaData {

    DatabaseMetaData getMetaData();
    
    MetaDataOperations getMetaDataOperations();

    Dialect getDialect();

    Map<String, Object> getDetail();

    void accept(MetaDataVisitor visitor) throws SQLException;

    String[] getStatements() throws SQLException;
}