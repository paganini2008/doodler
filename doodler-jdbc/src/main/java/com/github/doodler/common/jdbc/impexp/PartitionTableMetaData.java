package com.github.doodler.common.jdbc.impexp;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description: PartitionTableMetaData
 * @Author: Fred Feng
 * @Date: 17/05/2023
 * @Version 1.0.0
 */
public class PartitionTableMetaData implements TiedMetaData {

	private final String tableName;
	private final Map<String, Object> detail;
	private final TiedMetaData tiedMetaData;

	public PartitionTableMetaData(String tableName, Map<String, Object> detail, TiedMetaData tiedMetaData) {
		this.tableName = tableName;
		this.detail = detail;
		this.tiedMetaData = tiedMetaData;
	}

	private final List<PrimaryKeyMetaData> primaryKeyMetaDatas = new ArrayList<>();

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
		return tableName;
	}

	@Override
	public <T extends TiedMetaData> T unwrap(Class<T> clz) {
    	try {
    	return clz.cast(tiedMetaData);
    	}catch (RuntimeException e) {
			return tiedMetaData.unwrap(clz);
		}
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
	public Map<String, Object> getDetail() {
		return detail;
	}

	@Override
	public void accept(MetaDataVisitor visitor) throws SQLException {
		String catalogName = getCatalogName();
		String schemaName = getSchemaName();
		String tableName = getTableName();
		List<Map<String, Object>> infoList = getMetaDataOperations().getPrimaryKeyInfos(getMetaData(), catalogName,
				schemaName, tableName);
		for (Map<String, Object> pkInfo : infoList) {
			String columnName = (String) pkInfo.get("COLUMN_NAME");
			primaryKeyMetaDatas.add(new PrimaryKeyMetaData(columnName, pkInfo, this));
		}
		for (PrimaryKeyMetaData primaryKeyMetaData : primaryKeyMetaDatas) {
			primaryKeyMetaData.accept(visitor);
		}
		new PartitionExpressionMetaData(tableName, this).accept(visitor);

		visitor.visit(this);
	}

	@Override
	public String[] getStatements() throws SQLException {
		String catalogName = getCatalogName();
		String schemaName = getSchemaName();
		String tableName = getTableName();
		String inheritedTableName = (String) detail.get("INHERITED_TABLE_NAME");
		String statement = getDialect().getCreatePartitionTableStatement(catalogName, schemaName, tableName,
				inheritedTableName);
		return new String[]{statement};
	}
}