package io.doodler.jdbc.impexp;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import io.doodler.jdbc.impexp.DdlScripter.Catalog;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: ScriptExportHandler
 * @Author: Fred Feng
 * @Date: 30/03/2023
 * @Version 1.0.0
 */
@Slf4j
public class ScriptExportHandler implements ExportHandler {

    private static final String NEWLINE = System.getProperty("line.separator");
    private final File root;
    private final boolean separate;

    public ScriptExportHandler(File root, boolean separate) {
        this.root = root;
        this.separate = separate;
    }

    private String lastTableName;

    @Override
    public void exportDdl(DdlScripter ddlScripter) throws Exception {
        for (Map.Entry<String, Catalog> entry : ddlScripter.getCatalogs().entrySet()) {
            String catalogName = entry.getKey();
            if (log.isInfoEnabled()) {
                log.info("Switch to catalog: {}", catalogName);
            }
            List<String> sqls = entry.getValue().getPrettyScripts();
            if (CollectionUtils.isNotEmpty(sqls)) {
                File catalogDir = new File(root, catalogName);
                FileUtils.forceMkdir(catalogDir);
                List<String> sqlLines = new ArrayList<>(sqls);
                sqlLines.addAll(0, ddlScripter.getBeforeStatements());
                sqlLines.addAll(ddlScripter.getAfterStatements());
                if (log.isInfoEnabled()) {
                    sqlLines.forEach(sql -> {
                        log.info("Generated ddl: {}", sql);
                    });
                }

                File outputFile = new File(catalogDir, "schema.sql");
                FileOutputStream fos = null;
                try {
                    fos = FileUtils.openOutputStream(outputFile, false);
                    IOUtils.writeLines(sqlLines, NEWLINE, fos);
                    if (log.isInfoEnabled()) {
                        log.info("Successfully write to file: {}", outputFile);
                    }
                } finally {
                    IOUtils.closeQuietly(fos);
                }
            }
            if (log.isInfoEnabled()) {
                log.info("Get out of catalog: {}", catalogName);
            }
        }
    }

    @Override
    public void exportData(String catalogName, String schemaName, String tableName, TableMetaData tableMetaData,
                           List<Map<String, Object>> dataList, boolean idReused) throws Exception {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        File outputFile;
        if (separate) {
            File outputDir = FileUtils.getFile(root, catalogName, schemaName);
            FileUtils.forceMkdir(outputDir);
            outputFile = new File(outputDir, String.format("%s.sql", tableName));
        } else {
            File outputDir = new File(root, catalogName);
            FileUtils.forceMkdir(outputDir);
            outputFile = new File(outputDir, "data.sql");
        }
        List<Map<String, Object>> rowList = new ArrayList<>();
        Map<String, Object> template = tableMetaData.getColumnMetaDatas().stream()
                .filter(md -> (idReused? tableMetaData.isPrimaryKeyColumn(md.getColumnName()) : false) || !shouldFilterColumn(md.getColumnName(), tableMetaData))
                .collect(LinkedHashMap::new, (m, e) -> m.put(e.getColumnName(), null), LinkedHashMap::putAll);
        for (Map<String, Object> data : dataList) {
            Map<String, Object> row = new LinkedHashMap<>(template);
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                if (row.containsKey(entry.getKey())) {
                    row.put(entry.getKey(), entry.getValue());
                }
            }
            rowList.add(row);
        }
        List<String> sqlLines = new ArrayList<>();
        if (StringUtils.isBlank(lastTableName) || !lastTableName.equals(tableName)) {
            sqlLines.add(tableMetaData.getDialect().getScriptCommentPrefix());
            sqlLines.add(String.format("%s Table: %s", tableMetaData.getDialect().getScriptCommentPrefix(), tableName));
        }

        String[] columnNames;
        Object[] columnValues;
        String[] columnStringValues;
        String insertSql;
        for (Map<String, Object> row : rowList) {
            columnNames = row.keySet().toArray(new String[0]);
            columnValues = row.values().toArray();
            columnStringValues = getColumnStringValues(catalogName, schemaName, tableName, columnNames, columnValues,
                    tableMetaData);
            insertSql = tableMetaData.getDialect().getInsertTableStatement(catalogName, schemaName, tableName, columnNames,
                    columnStringValues);
            sqlLines.add(insertSql);
        }
        if (CollectionUtils.isNotEmpty(sqlLines)) {
            if (log.isInfoEnabled()) {
                sqlLines.forEach(sql -> {
                    log.info("Generated dml: {}", sql);
                });
            }

            sqlLines = SqlTextUtils.addEndMarks(sqlLines);
            FileOutputStream fos = null;
            try {
                fos = FileUtils.openOutputStream(outputFile, true);
                IOUtils.writeLines(sqlLines, NEWLINE, fos, Charset.defaultCharset());
                if (log.isInfoEnabled()) {
                    log.info("Successfully write to file: {}", outputFile);
                }
            } finally {
                IOUtils.closeQuietly(fos);
            }
        }
        this.lastTableName = tableName;
    }

    private String[] getColumnStringValues(String catalogName, String schemaName, String tableName,
                                           String[] columnNames, Object[] columnValues, TableMetaData tableMetaData) {
        List<String> stringValues = new ArrayList<>();
        String columnName;
        Object columnVaule;
        for (int i = 0; i < columnNames.length; i++) {
            columnName = columnNames[i];
            columnVaule = columnValues[i];
            stringValues.add(
                    tableMetaData.getDialect().getStringValue(catalogName, schemaName, tableName, columnName, columnVaule));
        }
        return stringValues.toArray(new String[0]);
    }

    protected boolean shouldFilterColumn(String columnName, TableMetaData tableMetaData) {
        return tableMetaData.isAutoIncrementColumn(columnName);
    }
    
    
}