package com.github.doodler.common.jdbc.impexp;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * @Description: ColumnMetaData
 * @Author: Fred Feng
 * @Date: 25/03/2023
 * @Version 1.0.0
 */
public class ColumnMetaData implements TiedMetaData {

    private final String columnName;
    private final Map<String, Object> detail;
    private final TiedMetaData tiedMetaData;

    private final List<CommentMetaData> commentMetaDatas = new ArrayList<>();

    public ColumnMetaData(String columnName, Map<String, Object> detail, TiedMetaData tiedMetaData) {
        this.columnName = columnName;
        this.detail = detail;
        this.tiedMetaData = tiedMetaData;
    }

    @Override
    public void accept(MetaDataVisitor visitor) throws SQLException {
        visitor.visit(this);

        String comment = (String) detail.get("REMARKS");
        if (StringUtils.isNotBlank(comment)) {
            commentMetaDatas.add(new CommentMetaData(columnName, detail, this));
        }
        for (CommentMetaData commentMetaData : commentMetaDatas) {
            commentMetaData.accept(visitor);
        }
    }

    @Override
    public String[] getStatements() throws SQLException {
        Dialect dialect = getDialect();
        String catalogName = getCatalogName();
        String schemaName = getSchemaName();
        String tableName = getTableName();
        int dataType = (Integer) detail.get("DATA_TYPE");
        String typeName = (String) detail.get("TYPE_NAME");
        int columnSize = (Integer) detail.get("COLUMN_SIZE");
        int columnScale = (Integer) detail.get("DECIMAL_DIGITS");
        String defaultValue = (String) detail.get("COLUMN_DEF");
        boolean nullable = "YES".equalsIgnoreCase((String) detail.get("IS_NULLABLE"));
        String statement;
        if (unwrap(TableMetaData.class).isAutoIncrementColumn(columnName)) {
            statement = dialect.getIncrementalColumnStatement(catalogName, schemaName, tableName, columnName, dataType,
                    typeName,
                    columnSize, columnScale, defaultValue, nullable);
        } else {
            statement = dialect.getColumnStatement(catalogName, schemaName, tableName, columnName, dataType, typeName,
                    columnSize,
                    columnScale, defaultValue, nullable);
        }
        return new String[]{statement};
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

    @Override
    public <T extends TiedMetaData> T unwrap(Class<T> clz) {
        try {
            return clz.cast(tiedMetaData);
        } catch (RuntimeException e) {
            return tiedMetaData.unwrap(clz);
        }
    }

    public String getColumnName() {
        return columnName;
    }

    @Override
    public Map<String, Object> getDetail() {
        return detail;
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
    public MetaDataOperations getMetaDataOperations() {
        return tiedMetaData.getMetaDataOperations();
    }
}