package com.github.doodler.common.jdbc.impexp;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import com.github.doodler.common.jdbc.Cursor;
import com.github.doodler.common.jdbc.JdbcUtils;

/**
 * @Description: MetaDataOperations
 * @Author: Fred Feng
 * @Date: 29/03/2023
 * @Version 1.0.0
 */
public class MetaDataOperations {

    public List<Map<String, Object>> getCatalogInfos(DatabaseMetaData databaseMetaData) throws SQLException {
        Cursor<Map<String, Object>> cursor = JdbcUtils.getCatalogInfos(databaseMetaData);
        return cursor != null ? cursor.list() : Collections.emptyList();
    }

    public List<Map<String, Object>> getSchemaInfos(DatabaseMetaData databaseMetaData, String catalogName)
            throws SQLException {
        Cursor<Map<String, Object>> cursor = JdbcUtils.getSchemaInfos(databaseMetaData, catalogName);
        return cursor != null ? cursor.list() : Collections.emptyList();
    }

    public List<Map<String, Object>> getTableInfos(DatabaseMetaData databaseMetaData, String catalogName, String schemaName)
            throws SQLException {
        Cursor<Map<String, Object>> cursor = JdbcUtils.getTableInfos(databaseMetaData, catalogName, schemaName);
        return cursor != null ? cursor.list() : Collections.emptyList();
    }

    public List<Map<String, Object>> getColumnInfos(DatabaseMetaData databaseMetaData, String catalogName,
                                                    String schemaName,
                                                    String tableName) throws SQLException {
        Cursor<Map<String, Object>> cursor = JdbcUtils.getColumnInfos(databaseMetaData, catalogName, schemaName, tableName);
        return cursor != null ? cursor.list() : Collections.emptyList();
    }

    public List<Map<String, Object>> getPrimaryKeyInfos(DatabaseMetaData databaseMetaData, String catalogName,
                                                        String schemaName,
                                                        String tableName) throws SQLException {
        Cursor<Map<String, Object>> cursor = JdbcUtils.getPrimaryKeyInfos(databaseMetaData, catalogName, schemaName,
                tableName);
        return cursor != null ? cursor.list() : Collections.emptyList();
    }

    public List<Map<String, Object>> getIndexInfos(DatabaseMetaData databaseMetaData, String catalogName,
                                                   String schemaName,
                                                   String tableName) throws SQLException {
        Cursor<Map<String, Object>> cursor = JdbcUtils.getIndexInfos(databaseMetaData, catalogName, schemaName,
                tableName);
        return cursor != null ? cursor.list() : Collections.emptyList();
    }

    public List<Map<String, Object>> getImportedKeyInfos(DatabaseMetaData databaseMetaData, String catalogName,
                                                         String schemaName,
                                                         String tableName) throws SQLException {
        Cursor<Map<String, Object>> cursor = JdbcUtils.getImportedKeyInfos(databaseMetaData, catalogName, schemaName,
                tableName);
        return cursor != null ? cursor.list() : Collections.emptyList();
    }
   
}