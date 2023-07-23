package io.doodler.jdbc.impexp;
//package com.elraytech.maxibet.demo.impexp;
//
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//import org.apache.commons.lang3.StringUtils;
//
//import io.doodler.jdbc.Cursor;
//import io.doodler.jdbc.JdbcUtils;
//import io.doodler.jdbc.impexp.Dialect;
//
///**
// * 
// * @Description: CatalogWalker
// * @Author: Fred Feng
// * @Date: 25/03/2023
// * @Version 1.0.0
// */
//public class CatalogWalker {
//
//	public static final String NEWLINE = System.getProperty("line.separator");
//	private final ConnectionFactory source;
//	private final ConnectionFactory target;
//	private final Dialect dialect;
//
//	public CatalogWalker(ConnectionFactory source, ConnectionFactory target, Dialect dialect) {
//		this.source = source;
//		this.target =target;
//		this.dialect = dialect;
//	}
//
//	public void walk(String catalog, String schema) throws SQLException{
//		Connection connection = null;
//		try {
//		connection = source.getConnection();
//		Cursor<Map<String,Object>> tableCursor = JdbcUtils.getTableInfos(connection, catalog, schema);
//		while(tableCursor.hasNext()) {
//			Map<String ,Object> tableInfo = tableCursor.next();
//			String tableName = (String) tableInfo.get("TABLE_NAME");
//			if(shouldFilter(catalog, schema, tableName, tableInfo)) {
//				continue;
//			}
//			DDL tableDDL = createDDL(connection, catalog, schema, tableName);
//		}
//		}finally {
//			source.close(connection);
//		}
//	}
//	
//	private List<String> getPkColumnNames(Connection connection,String catalog, String schema, String tableName) throws SQLException {
//		List<String> columnNames = new ArrayList<>();
//		Cursor<Map<String, Object>> pkCursor = JdbcUtils.getPrimaryKeyInfos(connection, catalog, schema, tableName);
//		while (pkCursor.hasNext()) {
//			Map<String, Object> data = pkCursor.next();
//			if (data.containsKey("COLUMN_NAME")) {
//				columnNames.add((String) data.get("COLUMN_NAME"));
//			}
//		}
//		return columnNames;
//	}
//	
//	private DDL createDDL(Connection connection,String catalog, String schema, String tableName) throws SQLException {
//		DDL tableDDL = new DDL();
//		tableDDL.getTable().add("CREATE TABLE ".concat(tableName).concat(" ("));
//		List<String> pkColumnNames = getPkColumnNames(connection,catalog, schema,tableName);
//		Cursor<Map<String, Object>> columnCursor = JdbcUtils.getColumnInfos(connection, catalog, schema, tableName);
//		if(columnCursor.hasNext()) {
//			while (true) {
//				Map<String, Object> columnInfo = columnCursor.next();
//				String columnName = (String)columnInfo.get("COLUMN_NAME");
//				String columnDef = getColumnDef(catalog, schema, tableName,columnName, pkColumnNames.contains(columnName),columnInfo);
//				String remarks = (String)columnInfo.get("REMARKS");
//				if(StringUtils.isNotBlank(remarks)) {
//					tableDDL.getComments().add(dialect.getCreateCommentSql(catalog, schema, tableName, columnName, remarks));
//				}
//				if(columnCursor.hasNext()) {
//					tableDDL.getTable().add(columnDef+ ",");
//				}else {
//					tableDDL.getTable().add(columnDef);
//					break;
//				}
//			}
//		}
//		tableDDL.getTable().add(")");
//		
//		Cursor<Map<String, Object>> indexCursor = JdbcUtils.getIndexInfos(connection, catalog, schema, tableName);
//		while(indexCursor.hasNext()) {
//			Map<String, Object> indexInfo = indexCursor.next();
//			String columnName = (String)indexInfo.get("COLUMN_NAME");
//			String indexName = (String)indexInfo.get("INDEX_NAME");
//			tableDDL.getIndexes().add(dialect.getCreateIndexSql(catalog, schema, tableName, columnName, indexName));
//		}
//		return tableDDL;
//	}
//	
//	protected String getColumnDef(String catalog, String schema, String tableName,String columnName,boolean pk, Map<String, Object> columnInfo) {
//		StringBuilder columnDef = new StringBuilder(columnName).append(" ");
//		int dataType = (Integer)columnInfo.get("DATA_TYPE");
//		String typeName = (String)columnInfo.get("TYPE_NAME");
//		int columnSize = (Integer)columnInfo.get("COLUMN_SIZE");
//		int scale = (Integer)columnInfo.get("DECIMAL_DIGITS");
//		if(pk) {
//			columnDef.append(typeName+ " PRIMARY KEY");
//		}else {
//			columnDef.append(dialect.getTypeName(dataType,columnSize,columnSize,scale));
//			String part= (String)columnInfo.get("COLUMN_DEF");
//			if(StringUtils.isNotBlank(part)) {
//				columnDef.append(" DEFAULT " + part);
//			}
//			String nullable = (String)columnInfo.get("IS_NULLABLE");
//			columnDef.append("NO".equalsIgnoreCase(nullable)? " NOT NULL":"");
//		}
//		return columnDef.toString();
//	}
//	
//	protected boolean shouldFilter(String catalog, String schema,String tableName,Map<String ,Object> tableInfo) {
//		return true;
//	}
//
//}
