package com.github.doodler.common.jdbc.impexp;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

/**
 * @Description: Dialect
 * @Author: Fred Feng
 * @Date: 24/03/2023
 * @Version 1.0.0
 */
public abstract class Dialect {

    private final TypeNames typeNames = new TypeNames();

    protected Dialect() {

        // SQL-92
        registerColumnType(Types.BIT, "bit");
        registerColumnType(Types.BOOLEAN, "boolean");
        registerColumnType(Types.TINYINT, "tinyint");
        registerColumnType(Types.SMALLINT, "smallint");
        registerColumnType(Types.INTEGER, "integer");
        registerColumnType(Types.BIGINT, "bigint");
        registerColumnType(Types.FLOAT, "float($p)");
        registerColumnType(Types.DOUBLE, "double precision");
        registerColumnType(Types.NUMERIC, "numeric($p,$s)");
        registerColumnType(Types.REAL, "real");

        registerColumnType(Types.DATE, "date");
        registerColumnType(Types.TIME, "time");
        registerColumnType(Types.TIMESTAMP, "timestamp");

        registerColumnType(Types.VARBINARY, "bit varying($l)");
        registerColumnType(Types.LONGVARBINARY, "bit varying($l)");
        registerColumnType(Types.BLOB, "blob");

        registerColumnType(Types.CHAR, "char($l)");
        registerColumnType(Types.VARCHAR, "varchar($l)");
        registerColumnType(Types.LONGVARCHAR, "varchar($l)");
        registerColumnType(Types.CLOB, "clob");

        registerColumnType(Types.NCHAR, "nchar($l)");
        registerColumnType(Types.NVARCHAR, "nvarchar($l)");
        registerColumnType(Types.LONGNVARCHAR, "nvarchar($l)");
        registerColumnType(Types.NCLOB, "nclob");
    }

    protected void registerColumnType(int code, long capacity, String name) {
        typeNames.put(code, capacity, name);
    }

    protected void registerColumnType(int code, String name) {
        typeNames.put(code, name);
    }

    public String getTypeName(int code) {
        final String result = typeNames.get(code);
        if (result == null) {
            throw new TypeMappingException("No default type mapping for (java.sql.Types) " + code);
        }
        return result;
    }

    public String getTypeName(int code, long length, int precision, int scale) {
        final String result = typeNames.get(code, length, precision, scale);
        if (result == null) {
            throw new TypeMappingException(
                    String.format("No type mapping for java.sql.Types code: %s, length: %s", code, length));
        }
        return result;
    }

    public abstract String getCreateUserStatement(String username, String password);

    public String[] getStatementAfterUserCreated(String username) {
        return null;
    }

    public abstract String getCreateDatabaseStatement(String catalog, String username);

    public String[] getStatementAfterDatabaseCreated(String catalog, String username) {
        return null;
    }

    public abstract String getCreateSchemaStatement(String catalog, String schema, String username);

    public String[] getStatementAfterSchemaCreated(String catalog, String schema, String username) {
        return null;
    }

    public String getDefaultSchemaName(String catalog) {
        return null;
    }

    public String getDropTableStatement(String catalog, String schema, String tableName) {
        return String.format("DROP TABLE IF EXISTS %s CASCADE", tableName);
    }

    public String getCreateTableStatement(String catalog, String schema, String tableName) {
        return String.format("CREATE TABLE IF NOT EXISTS %s", tableName);
    }
    
    public abstract String getSequenceNameStatement(String catalog, String schema, String tableName, String columnName);
    
    public abstract String getDefaultSequenceName(String catalog, String schema, String tableName, String columnName);
    
    public abstract String getAlterSequenceStartValueStatement(String catalog, String schema, String tableName, String sequenceName, long startValue);

    public String[] getStatementAfterTableCreated(String catalog, String schema, String tableName, String username) {
        return null;
    }

    public String getCreatePrimaryKeyStatement(String catalog, String schema, String tableName, String columnName,
                                               String pkeyName) {
        return String.format("CONSTRAINT %s PRIMARY KEY (%s)", pkeyName, columnName);
    }

    public abstract String getCreateCommentStatement(String catalog, String schema, String tableName, String columnName,
                                                     String comment);

    public String getIndexNameStatement(String catalog, String schema, String tableName, String[] columnNames,
                                        boolean unique,
                                        String indexType) {
        final String indexNamePattern = "%s_%s_%s";
        if (unique) {
            return String.format(indexNamePattern, "uidx", tableName, StringUtils.join(columnNames, "_"));
        }
        return String.format(indexNamePattern, "idx", tableName, StringUtils.join(columnNames, "_"));
    }

    public String getCreateIndexStatement(String catalog, String schema, String tableName, boolean partition,
                                          String[] columnNames,
                                          String indexName, boolean unique, String indexType) {
        if (unique) {
            return String.format("CREATE UNIQUE INDEX IF NOT EXISTS %s ON %s (%s)", indexName, tableName,
                    StringUtils.join(columnNames, ","));
        }
        return String.format("CREATE INDEX IF NOT EXISTS %s ON %s (%s)", indexName, tableName,
                StringUtils.join(columnNames, ","));
    }

    public abstract String getIncrementalColumnStatement(String catalog, String schema, String tableName, String columnName,
                                                         int dataType,
                                                         String typeName, int columnSize, int columnScale,
                                                         String defaultValue, boolean nullable);

    public String getColumnStatement(String catalog, String schema, String tableName, String columnName, int dataType,
                                     String typeName,
                                     int columnSize, int columnScale, String defaultValue, boolean nullable) {
        StringBuilder columnDef = new StringBuilder(StringHelper.textLeft(columnName, 30));
        String columnTypeName = getTypeName(dataType, columnSize, columnSize, columnScale);
        columnDef.append(StringHelper.textLeft(columnTypeName, 30));
        if (StringUtils.isNotBlank(defaultValue)) {
            columnDef.append(" DEFAULT " + defaultValue);
        }
        if (!nullable) {
            columnDef.append(" NOT NULL");
        }
        return columnDef.toString();
    }

    public String getDefinePartitionTableStatement(String catalog, String schema, String tableName, String partitionType,
                                                   String columnNames) {
        return String.format("PARTITION BY %s (%s)", partitionType, columnNames);
    }
    
    public abstract String getCreatePartitionTableStatement(String catalog, String schema, String tableName, String inheritedTableName);

    public String getScriptCommentPrefix() {
        return "-- ";
    }

    public String getInsertTableStatement(String catalog, String schema, String tableName, String[] columnNames) {
        String columns = StringUtils.join(columnNames, ",");
        String values = Arrays.stream(columnNames).map(name -> "?").collect(Collectors.joining(","));
        return String.format("INSERT INTO %s(%s) VALUES (%s)", tableName, columns, values);
    }

    public String getInsertTableStatement(String catalog, String schema, String tableName, String[] columnNames,
                                          Object[] columnValues) {
        String columns = StringUtils.join(columnNames, ",");
        String values = StringUtils.join(columnValues, ",");
        return String.format("INSERT INTO %s(%s) VALUES (%s)", tableName, columns, values);
    }

    public String getSelectTableStatement(String catalog, String schema, String tableName) {
        return String.format("SELECT * FROM %s", tableName);
    }
    
    public String getSelectMaxColumnStatement(String catalog, String schema, String tableName, String columnName) {
    	return String.format("SELECT max(%s) FROM %s", columnName, tableName);
    }

    public String getStringValue(String catalog, String schema, String tableName, String columnName, Object columnValue) {
        if (columnValue == null) {
            return "NULL";
        }
        if (columnValue instanceof Number) {
            if (columnValue instanceof BigDecimal) {
                return ((BigDecimal) columnValue).toPlainString();
            } else if (columnValue instanceof Double) {
                return BigDecimal.valueOf((Double) columnValue).toPlainString();
            } else if (columnValue instanceof Float) {
                return BigDecimal.valueOf((Float) columnValue).toPlainString();
            }
            return columnValue.toString();
        } else if (columnValue instanceof Character) {
            return "'" + String.valueOf((Character) columnValue) + "'";
        } else if (columnValue instanceof CharSequence) {
            return "'" + columnValue.toString() + "'";
        } else if (columnValue instanceof Boolean) {
            return String.valueOf((Boolean) columnValue);
        }
        throw new UnsupportedOperationException("Input type: " + columnValue.getClass());
    }
}