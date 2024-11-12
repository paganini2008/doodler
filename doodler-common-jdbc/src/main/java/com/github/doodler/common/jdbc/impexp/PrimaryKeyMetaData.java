package com.github.doodler.common.jdbc.impexp;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Map;

/**
 * @Description: PrimaryKeyMetaData
 * @Author: Fred Feng
 * @Date: 25/03/2023
 * @Version 1.0.0
 */
public class PrimaryKeyMetaData implements TiedMetaData {

    private final String columnName;
    private final Map<String, Object> detail;
    private final TiedMetaData tiedMetaData;

    public PrimaryKeyMetaData(String columnName, Map<String, Object> detail, TiedMetaData tiedMetaData) {
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

    @Override
	public <T extends TiedMetaData> T unwrap(Class<T> clz) {
    	try {
    	return clz.cast(tiedMetaData);
    	}catch (RuntimeException e) {
			return tiedMetaData.unwrap(clz);
		}
	}

	public String getColumnName() {
        return columnName;
    }

    @Override
    public DatabaseMetaData getMetaData() {
        return tiedMetaData.getMetaData();
    }

    @Override
    public MetaDataOperations getMetaDataOperations() {
        return tiedMetaData.getMetaDataOperations();
    }

    @Override
    public Dialect getDialect() {
        return tiedMetaData.getDialect();
    }

    @Override
    public void accept(MetaDataVisitor visitor) throws SQLException {
        visitor.visit(this);
    }

    @Override
    public String[] getStatements() throws SQLException {
        String catalogName = getCatalogName();
        String schemaName = getSchemaName();
        String tableName = getTableName();
        String pkName = (String) detail.get("PK_NAME");
        String statement = getDialect().getCreatePrimaryKeyStatement(catalogName, schemaName, tableName, columnName,
                pkName);
        return new String[]{statement};
    }

    @Override
    public Map<String, Object> getDetail() {
        return detail;
    }

}