package com.github.doodler.common.jdbc.impexp;

import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.postgresql.util.PGobject;

/**
 * @Description: PostgreSQLDialect
 * @Author: Fred Feng
 * @Date: 24/03/2023
 * @Version 1.0.0
 */
public class PostgreSQLDialect extends Dialect {

    private static final DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public PostgreSQLDialect() {
        super();
        registerColumnType(Types.BIT, "boolean");
        registerColumnType(Types.BIGINT, "int8");
        registerColumnType(Types.SMALLINT, "int2");
        registerColumnType(Types.TINYINT, "int2");
        registerColumnType(Types.INTEGER, "int4");
        // registerColumnType(Types.VARCHAR, "varchar($l)");
        registerColumnType(Types.FLOAT, "float4");
        registerColumnType(Types.DOUBLE, "float8");
        registerColumnType(Types.DATE, "date");
        registerColumnType(Types.TIME, "time");
        registerColumnType(Types.TIMESTAMP, "timestamp");
        registerColumnType(Types.VARBINARY, "bytea");
        registerColumnType(Types.BINARY, "bytea");
        registerColumnType(Types.LONGVARCHAR, "text");
        registerColumnType(Types.LONGVARBINARY, "bytea");
        registerColumnType(Types.CLOB, "text");
        registerColumnType(Types.BLOB, "oid");
        registerColumnType(Types.NUMERIC, "numeric($p, $s)");
        // registerColumnType(Types.OTHER, "uuid");
        registerColumnType(Types.OTHER, "json");
        registerColumnType(Types.JAVA_OBJECT, "json");

        // ??
        registerColumnType(Types.VARCHAR, "text");
        registerColumnType(Types.VARCHAR, 65535, "varchar($l)");

        registerSerialColumnType(Types.SMALLINT, "smallserial");
        registerSerialColumnType(Types.TINYINT, "smallserial");
        registerSerialColumnType(Types.INTEGER, "serial");
        registerSerialColumnType(Types.BIGINT, "bigserial");
    }

    private final TypeNames serialTypeNames = new TypeNames();

    protected void registerSerialColumnType(int code, String name) {
        serialTypeNames.put(code, name);
    }

    public String getSerialTypeName(int code) {
        final String result = serialTypeNames.get(code);
        if (result == null) {
            throw new ImpExpException("No default serial type mapping for (java.sql.Types) " + code);
        }
        return result;
    }

    @Override
    public String getCreateUserStatement(String username, String password) {
        return String.format("CREATE USER %s WITH PASSWORD '%s'", username, password);
    }

    @Override
    public String getCreateDatabaseStatement(String catalog, String username) {
        return String.format("CREATE DATABASE %s OWNER %s", catalog, username);
    }

    @Override
    public String[] getStatementAfterDatabaseCreated(String catalog, String username) {
        String statement = String.format("GRANT ALL PRIVILEGES ON DATABASE %s TO %s", catalog, username);
        return new String[]{statement};
    }

    @Override
    public String getDefaultSchemaName(String catalog) {
        return "public";
    }

    @Override
    public String getCreateSchemaStatement(String catalog, String schema, String username) {
        return String.format("CREATE SCHEMA IF NOT EXISTS %s AUTHORIZATION %s", schema, username);
    }

    @Override
    public String getAlterSequenceStartValueStatement(String catalog, String schema, String tableName, String sequenceName,
                                                      long startValue) {
        return String.format("ALTER SEQUENCE %s restart with %s", sequenceName, startValue);
    }

    @Override
    public String getSequenceNameStatement(String catalog, String schema, String tableName, String columnName) {
        return String.format("SELECT pg_get_serial_sequence('%s.%s','%s') as sequenceName", schema, tableName, columnName);
    }

    @Override
    public String getDefaultSequenceName(String catalog, String schema, String tableName, String columnName) {
        return String.format("%s.%s_%s_seq", schema, tableName, columnName);
    }

    @Override
    public String getCreateIndexStatement(String catalog, String schema, String tableName, boolean partition,
                                          String[] columnNames, String indexName, boolean unique, String indexType) {
        if (StringUtils.isNotBlank(indexType)) {
            return String.format("CREATE INDEX IF NOT EXISTS %s ON %s USING %s (%s)", indexName, tableName, indexType,
                    columnNames);
        }
        if (partition) {
            unique = false;
        }
        return super.getCreateIndexStatement(catalog, schema, tableName, partition, columnNames, indexName, unique,
                indexType);
    }

    @Override
    public String getCreateCommentStatement(String catalog, String schema, String tableName, String columnName,
                                            String comment) {
        return String.format("COMMENT ON COLUMN %s.%s IS '%s'", tableName, columnName, comment);
    }

    @Override
    public String getIncrementalColumnStatement(String catalog, String schema, String tableName, String columnName,
                                                int dataType,
                                                String typeName, int columnSize, int columnScale, String defaultValue,
                                                boolean nullable) {
        StringBuilder columnDef = new StringBuilder(StringHelper.textLeft(columnName, 30));
        String columnTypeName;
        try {
            columnTypeName = getSerialTypeName(dataType);
        } catch (RuntimeException e) {
            columnTypeName = getTypeName(dataType, columnSize, columnSize, columnScale);
        }
        columnDef.append(StringHelper.textLeft(columnTypeName, 30));
        if (!nullable) {
            columnDef.append(" NOT NULL");
        }
        return columnDef.toString();
    }

    @Override
    public String getStringValue(String catalog, String schema, String tableName, String columnName, Object columnValue) {
        if (columnValue instanceof CharSequence) {
            String stringValue = columnValue.toString();
            if (stringValue.contains("'")) {
                stringValue = SqlTextUtils.addEscapeChar(columnValue.toString(), '\'');
            }
            return "'" + stringValue + "'";
        } else if (columnValue instanceof LocalDateTime) {
            return "'" + ((LocalDateTime) columnValue).format(DEFAULT_DATE_TIME_FORMATTER) + "'";
        } else if (columnValue instanceof LocalDate) {
            return "'" + ((LocalDate) columnValue).format(DEFAULT_DATE_FORMATTER) + "'";
        } else if (columnValue instanceof Date) {
            if (columnValue instanceof Timestamp) {
                return "'" + DateFormatUtils.format((Timestamp) columnValue, "yyyy-MM-dd HH:mm:ss") + "'";
            } else if (columnValue instanceof java.sql.Date) {
                return "'" + DateFormatUtils.format((java.sql.Date) columnValue, "yyyy-MM-dd") + "'";
            }
        } else if (columnValue instanceof PGobject) {
            String str = ((PGobject) columnValue).getValue();
            if (StringUtils.isNotBlank(str)) {
                str = str.replaceAll("\r\n", "");
            }
            return "'" + str + "'";
        }
        return super.getStringValue(catalog, schema, tableName, columnName, columnValue);
    }

    @Override
    public String getCreatePartitionTableStatement(String catalog, String schema, String tableName,
                                                   String inheritedTableName) {
        return String.format("CREATE TABLE IF NOT EXISTS %s PARTITION OF %s", tableName, inheritedTableName);
    }

    @Override
    public String getSelectMaxColumnStatement(String catalog, String schema, String tableName, String columnName) {
        return String.format("SELECT max(%s) FROM %s.%s", columnName, schema, tableName);
    }
}