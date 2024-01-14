package com.github.doodler.common.jdbc.impexp;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Map;

/**
 * @Description: PartitionExpressionMetaData
 * @Author: Fred Feng
 * @Date: 17/05/2023
 * @Version 1.0.0
 */
public class PartitionExpressionMetaData implements TiedMetaData {

	private final String tableName;
	private final TiedMetaData tiedMetaData;

	public PartitionExpressionMetaData(String tableName, TiedMetaData tiedMetaData) {
		this.tableName = tableName;
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
		return tableName;
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
	public MetaDataOperations getMetaDataOperations() {
		return tiedMetaData.getMetaDataOperations();
	}

	@Override
	public Dialect getDialect() {
		return tiedMetaData.getDialect();
	}

	@Override
	public Map<String, Object> getDetail() {
		return tiedMetaData.getDetail();
	}

	@Override
	public void accept(MetaDataVisitor visitor) throws SQLException {
		visitor.visit(this);
	}

	@Override
	public String[] getStatements() throws SQLException {
		String partitionExpression = (String) getDetail().get("PARTITION_EXPRESSION");
		return new String[]{partitionExpression};
	}
}