package com.github.doodler.common.jdbc.impexp;

import com.github.doodler.common.jdbc.JdbcUtils;
import com.github.doodler.common.utils.CaseInsensitiveMap;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description: PostgreSQLMetaDataOperations
 * @Author: Fred Feng
 * @Date: 29/03/2023
 * @Version 1.0.0
 */
public class PostgreSQLMetaDataOperations extends MetaDataOperations {

    @Override
    public List<Map<String, Object>> getTableInfos(DatabaseMetaData databaseMetaData, String catalogName, String schemaName)
            throws SQLException {
        List<Map<String, Object>> tableInfos = super.getTableInfos(databaseMetaData, catalogName, schemaName);

        String sql = "SELECT a.oid,a.relname FROM pg_class a WHERE a.relnamespace = (SELECT oid FROM pg_namespace WHERE nspname='%s') AND a.relkind='p'";
        List<Map<String, Object>> partitionTablePairs = JdbcUtils.fetchAll(databaseMetaData.getConnection(),
                String.format(sql, schemaName));
        if (CollectionUtils.isEmpty(partitionTablePairs)) {
            return tableInfos;
        }
        sql = "SELECT c.relname, pc.relname as parentname, pg_get_expr(c.relpartbound, c.oid) as expr"
                + " FROM pg_class c JOIN pg_inherits i ON c.oid = i.inhrelid" + " JOIN pg_class pc ON pc.oid = i.inhparent"
                + " JOIN pg_namespace pn ON pn.oid = pc.relnamespace" +
                "	WHERE pn.nspname='%s' AND pc.relkind='p' AND pc.relname='%s'";
        List<Map<String, Object>> partitionDefinitionInfos = new ArrayList<>();
        for (Map<String, Object> detail : partitionTablePairs) {
            Long oid = (Long) detail.get("oid");
            String inheritedTableName = (String) detail.get("relname");
            List<Map<String, Object>> partitionTableInherits = JdbcUtils.fetchAll(databaseMetaData.getConnection(),
                    String.format(sql, schemaName, inheritedTableName));
            Map<String, Object> firstInherit = partitionTableInherits.get(0);
            Optional<Map<String, Object>> opt = tableInfos.stream()
                    .filter(info -> ((String) firstInherit.get("relname")).equals(info.get("TABLE_NAME"))).findFirst();
            if (opt.isPresent()) {
                Map<String, Object> copyTableInfo = new CaseInsensitiveMap<>();
                copyTableInfo.putAll(opt.get());
                copyTableInfo.put("TABLE_NAME", inheritedTableName);
                copyTableInfo.put("PARTITION_TABLE_NAMES",
                        partitionTableInherits.stream().map(inherit -> (String) inherit.get("relname")).toArray(
                                l -> new String[l]));
                copyTableInfo.put("IS_PARTITIONED", true);
                Map<String, Object> partitionDefinitionInfo = getPartitionDefinitionInfo(databaseMetaData, catalogName,
                        schemaName, inheritedTableName, oid);
                if (partitionDefinitionInfo != null) {
                    copyTableInfo.putAll(partitionDefinitionInfo);
                }
                partitionDefinitionInfos.add(copyTableInfo);
            }

            for (Map<String, Object> inherit : partitionTableInherits) {
                String partitionTableName = (String) inherit.get("relname");
                String partitionExpression = (String) inherit.get("expr");
                opt = tableInfos.stream().filter(info -> partitionTableName.equals(info.get("TABLE_NAME"))).findFirst();
                if (opt.isPresent()) {
                    Map<String, Object> info = opt.get();
                    info.put("IS_PARTITION_TABLE", true);
                    info.put("INHERITED_TABLE_NAME", inheritedTableName);
                    info.put("PARTITION_EXPRESSION", partitionExpression);
                }
            }
        }
        tableInfos.addAll(partitionDefinitionInfos);
        return tableInfos;
    }

    @Override
    public List<Map<String, Object>> getColumnInfos(DatabaseMetaData databaseMetaData, String catalogName,
                                                    String schemaName,
                                                    String tableName) throws SQLException {
        List<Map<String, Object>> columnInfos = super.getColumnInfos(databaseMetaData, catalogName, schemaName, tableName);
        boolean partitionTable = false;
        if (CollectionUtils.isEmpty(columnInfos) &&
                (partitionTable = isPartitionTable(databaseMetaData, schemaName, tableName))) {
            List<String> partitionTableNames = findPartitionTableNamesByInheritedTable(databaseMetaData, schemaName,
                    tableName);
            for (String partitionTableName : partitionTableNames) {
                List<Map<String, Object>> infos = super.getColumnInfos(databaseMetaData, catalogName, schemaName,
                        partitionTableName);
                if (CollectionUtils.isNotEmpty(infos)) {
                    columnInfos = infos;
                    break;
                }
            }
        }
        if (CollectionUtils.isEmpty(columnInfos)) {
            throw new ImpExpException("No columns found in table: " + tableName);
        }
        String sql = "SELECT a.attnum,c.relname,a.attname,t.typname,a.attlen,a.atttypmod,a.attnotnull,b.description";
        sql += " FROM pg_class c,pg_attribute a LEFT OUTER JOIN pg_description b ON a.attrelid=b.objoid AND a.attnum = b.objsubid,pg_type t";
        sql += " WHERE c.relname = '%s' and a.attrelid = c.oid and a.atttypid = t.oid";
        List<Map<String, Object>> columnDetails = JdbcUtils.fetchAll(databaseMetaData.getConnection(),
                String.format(sql, tableName));
        if (CollectionUtils.isNotEmpty(columnDetails)) {
            Map<String, Map<String, Object>> mutableColumnInfos = columnDetails.stream()
                    .collect(Collectors.toMap(info -> (String) info.get("attname"), Function.identity(), (a, b) -> a));
            for (Map<String, Object> columnInfo : columnInfos) {
                if (StringUtils.isBlank((String) columnInfo.get("REMARKS"))) {
                    Map<String, Object> mutableColumnInfo = mutableColumnInfos.get(columnInfo.get("COLUMN_NAME"));
                    if (mutableColumnInfo != null) {
                        columnInfo.put("REMARKS", mutableColumnInfo.get("description"));
                    }
                }
                if (partitionTable) {
                    columnInfo.put("TABLE_NAME", tableName);
                }
            }
        }
        return columnInfos;
    }

    @Override
    public List<Map<String, Object>> getIndexInfos(DatabaseMetaData databaseMetaData, String catalogName, String schemaName,
                                                   String tableName) throws SQLException {
        List<Map<String, Object>> indexInfos = super.getIndexInfos(databaseMetaData, catalogName, schemaName, tableName);
        if (CollectionUtils.isNotEmpty(indexInfos)) {
            return indexInfos;
        }
        if (isPartitionTable(databaseMetaData, schemaName, tableName)) {
            List<String> partitionTableNames = findPartitionTableNamesByInheritedTable(databaseMetaData, schemaName,
                    tableName);
            for (String partitionTableName : partitionTableNames) {
                List<Map<String, Object>> infos = super.getIndexInfos(databaseMetaData, catalogName, schemaName,
                        partitionTableName);
                if (CollectionUtils.isNotEmpty(infos)) {
                    indexInfos = infos;
                    break;
                }
            }
        }
        return indexInfos;
    }

    @SneakyThrows
    private boolean isPartitionTable(DatabaseMetaData databaseMetaData, String schemaName, String tableName) {
        String sql = "SELECT count(*) FROM pg_class a WHERE a.relnamespace = (SELECT oid FROM pg_namespace WHERE nspname='%s') AND a.relkind='p' AND a.relname='%s'";
        return JdbcUtils.fetchOne(databaseMetaData.getConnection(), String.format(sql, schemaName, tableName),
                Integer.class) > 0;
    }

    @SneakyThrows
    private List<String> findPartitionTableNamesByInheritedTable(DatabaseMetaData databaseMetaData, String schemaName,
                                                                 String tableName) {
        String sql = "SELECT c.relname, pc.relname as parentname, pg_get_expr(c.relpartbound, c.oid) as expr"
                + " FROM pg_class c JOIN pg_inherits i ON c.oid = i.inhrelid" + " JOIN pg_class pc ON pc.oid = i.inhparent"
                + " JOIN pg_namespace pn ON pn.oid = pc.relnamespace" +
                "	WHERE pn.nspname='%s' AND pc.relkind='p' AND pc.relname='%s'";
        List<Map<String, Object>> extenstionTableInfos = JdbcUtils.fetchAll(databaseMetaData.getConnection(),
                String.format(sql, schemaName, tableName));
        return extenstionTableInfos.stream().map(data -> (String) data.get("relname")).collect(Collectors.toList());
    }

    @SneakyThrows
    private Map<String, Object> getPartitionDefinitionInfo(DatabaseMetaData databaseMetaData, String catalogName,
                                                           String schemaName,
                                                           String tableName, Long oid) {
        Map<String, Object> info = new HashMap<>();
        String sql = "select partstrat,array_to_string(partattrs,',') as partattrs from pg_partitioned_table where partrelid=%s";
        Map<String, Object> data = JdbcUtils.fetchOne(databaseMetaData.getConnection(), String.format(sql, oid));
        String partstrat = (String) data.get("partstrat");
        String partitionType;
        switch (partstrat.toLowerCase()) {
            case "h":
                partitionType = "HASH";
                break;
            case "l":
                partitionType = "LIST";
                break;
            case "r":
                partitionType = "RANGE";
                break;
            default:
                throw new UnsupportedOperationException("Unknown partition table type: " + partstrat);
        }

        info.put("PARTITION_TYPE", partitionType);
        String partattrs = (String) data.get("partattrs");
        sql = "SELECT a.attnum,c.relname,a.attname,a.attlen,a.atttypmod,a.attnotnull FROM pg_class c,pg_attribute a WHERE c.relkind = 'p' and c.relname = '%s' and a.attnum in (%s) and a.attrelid = c.oid";
        List<Map<String, Object>> columnInfos = JdbcUtils.fetchAll(databaseMetaData.getConnection(),
                String.format(sql, tableName, partattrs));
        if (CollectionUtils.isNotEmpty(columnInfos)) {
            String partitionColumnNames = columnInfos.stream().map(m -> (String) m.get("attname")).collect(
                    Collectors.joining(","));
            info.put("PARTITION_COLUMN_NAMES", partitionColumnNames);
        }
        return info;
    }
}