package com.github.doodler.common.jdbc.impexp;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Map;

/**
 * @Description: IndexMetaData
 * @Author: Fred Feng
 * @Date: 25/03/2023
 * @Version 1.0.0
 */
public class IndexMetaData implements TiedMetaData {

    private final String indexName;
    private final String columnName;
    private final Map<String, Object> detail;
    private final TiedMetaData tiedMetaData;

    public IndexMetaData(String indexName, String columnName, Map<String, Object> detail, TiedMetaData tiedMetaData) {
        this.indexName = indexName;
        this.columnName = columnName;
        this.detail = detail;
        this.tiedMetaData = tiedMetaData;
    }

    @Override
    public String getCatalogName() {
        return tiedMetaData.getCatalogName();
    }

    @Override
    public String getSchemaName() {
        return tiedMetaData.getSchemaName();
    }

    @Override
    public String getTableName() {
        return tiedMetaData.getTableName();
    }

    public String getColumnName() {
        return columnName;
    }

    public String getIndexName() {
        return indexName;
    }

    @Override
    public <T extends TiedMetaData> T unwrap(Class<T> clz) {
        try {
            return clz.cast(tiedMetaData);
        } catch (RuntimeException e) {
            return tiedMetaData.unwrap(clz);
        }
    }

    @Override
    public DatabaseMetaData getMetaData() {
        return tiedMetaData.getMetaData();
    }

    @Override
    public Dialect getDialect() {
        return tiedMetaData.getDialect();
    }

    @Override
    public Map<String, Object> getDetail() {
        return detail;
    }

    @Override
    public MetaDataOperations getMetaDataOperations() {
        return tiedMetaData.getMetaDataOperations();
    }

    @Override
    public void accept(MetaDataVisitor visitor) throws SQLException {
        visitor.visit(this);
    }

    @Override
    public String[] getStatements() throws SQLException {
        TableMetaData tableMetaData = unwrap(TableMetaData.class);
        if (tableMetaData.isPrimaryKeyColumn(columnName) || tableMetaData.isAutoIncrementColumn(columnName)) {
            return null;
        }

        String catalogName = getCatalogName();
        String schemaName = getSchemaName();
        String tableName = getTableName();
        boolean partition = tableMetaData.isPartitionTable();
        Integer typeIndex = (Integer) detail.get("TYPE");
        String indexType = typeIndex != null && typeIndex == DatabaseMetaData.tableIndexHashed ? "HASH" : null;
        Boolean unique = !(Boolean) detail.getOrDefault("NON_UNIQUE", true);
        String indexName = getDialect().getIndexNameStatement(catalogName, schemaName, tableName, new String[]{columnName},
                unique,
                indexType);
        String statement = getDialect().getCreateIndexStatement(catalogName, schemaName, tableName, partition,
                new String[]{columnName}, indexName,
                unique, indexType);
        return new String[]{statement};
    }
}