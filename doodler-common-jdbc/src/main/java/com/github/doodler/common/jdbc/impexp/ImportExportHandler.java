package com.github.doodler.common.jdbc.impexp;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import com.github.doodler.common.jdbc.ConnectionFactory;
import com.github.doodler.common.jdbc.JdbcUtils;
import com.github.doodler.common.jdbc.SimpleConnectionFactory;
import com.github.doodler.common.jdbc.impexp.DdlScripter.Catalog;
import com.github.doodler.common.jdbc.impexp.DdlScripter.Schema;
import com.github.doodler.common.page.EachPage;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: ImportExportHandler
 * @Author: Fred Feng
 * @Date: 30/03/2023
 * @Version 1.0.0
 */
@Slf4j
public class ImportExportHandler implements ExportHandler {

    @Getter
    @Setter
    private ImportConfiguration configuration = new ImportConfiguration();

    @Getter
    @Setter
    @ToString
    public static class ImportConfiguration {

        private int port;
        private String hostname;
        private String defaultCatalogName;
        private DbType dbType;
        private String url;
        private String username;
        private String password;
        private boolean connectionPoolEnabled;
    }

    private ConnectionFactory connectionFactory;

    @Override
    public void start() {
        if (connectionFactory == null) {
            this.connectionFactory = createConnectionFactory();
        }
    }

    private ConnectionFactory createConnectionFactory() {
        String jdbcUrl = configuration.getUrl();
        if (StringUtils.isBlank(jdbcUrl)) {
            jdbcUrl = configuration.getDbType().getUrl(configuration.getHostname(), configuration.getPort(),
                    configuration.getDefaultCatalogName());
        }
        if (configuration.isConnectionPoolEnabled()) {
            return new HikariDataSourceConnectionFactory(configuration.getDbType().getDriverClassName(), jdbcUrl,
                    configuration.getUsername(), configuration.getPassword());
        }
        return new SimpleConnectionFactory(configuration.getDbType().getDriverClassName(), jdbcUrl,
                configuration.getUsername(),
                configuration.getPassword());
    }

    @Override
    public void exportDdl(DdlScripter ddlScripter) throws Exception {
        for (Map.Entry<String, Catalog> catalogEntry : ddlScripter.getCatalogs().entrySet()) {
            final String catalogName = catalogEntry.getKey();
            if (log.isInfoEnabled()) {
                log.info("Switch to catalog: {}", catalogName);
            }
            for (Map.Entry<String, Schema> schemaEntry : catalogEntry.getValue().getSchemas().entrySet()) {
                final String schemaName = schemaEntry.getKey();
                if (log.isInfoEnabled()) {
                    log.info("Switch to schema: {}", schemaName);
                }
                final List<String> sqls = schemaEntry.getValue().getPlainScripts();
                Connection connection = null;
                try {
                    if (configuration.getDbType().isCanSetCatalog() && configuration.getDbType().isCanSetSchema()) {
                        connection = connectionFactory.getConnection(catalogName, schemaName);
                    } else if (!configuration.getDbType().isCanSetCatalog() && configuration.getDbType().isCanSetSchema()) {
                        connection = connectionFactory.getConnection(null, schemaName);
                    } else if (configuration.getDbType().isCanSetCatalog() && !configuration.getDbType().isCanSetSchema()) {
                        connection = connectionFactory.getConnection(catalogName, null);
                    } else {
                        connection = connectionFactory.getConnection();
                    }
                    for (String sql : sqls) {
                        if (sql.endsWith(";")) {
                            sql = sql.substring(0, sql.length() - 1);
                        }
                        try {
                            JdbcUtils.update(connection, sql);
                            if (log.isInfoEnabled()) {
                                log.info("Execute ddl: {}", sql);
                            }
                        } catch (Exception e) {
                            if (log.isErrorEnabled()) {
                                log.error("Unable to execute sql: {}", sql, e);
                            }
                        }
                    }
                } finally {
                    connectionFactory.close(connection);
                }
                if (log.isInfoEnabled()) {
                    log.info("Get out of schema: {}", schemaName);
                }
            }
            if (log.isInfoEnabled()) {
                log.info("Get out of catalog: {}", catalogName);
            }
        }
    }

    @Override
    public void exportData(String catalogName, 
    		               String schemaName, 
    		               String tableName, 
    		               TableMetaData tableMetaData, 
    		               EachPage<Map<String, Object>> eachPage, 
    		               boolean idReused, 
    		               ConnectionFactory sourceConnectionFactory) throws Exception {
    	List<Map<String, Object>> dataList = eachPage.getContent();
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        List<Map<String, Object>> rowList = new ArrayList<>();
        Map<String, Object> template = tableMetaData.getColumnMetaDatas().stream()
                .filter(md -> (idReused ? tableMetaData.isPrimaryKeyColumn(md.getColumnName()) : false) ||
                        !shouldFilterColumn(md.getColumnName(), tableMetaData))
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

        if (CollectionUtils.isEmpty(rowList)) {
            return;
        }
        List<Object[]> argsList = rowList.stream().map(row -> row.values().toArray()).collect(Collectors.toList());
        String[] columns = template.keySet().toArray(new String[0]);
        String insertSql = tableMetaData.getDialect().getInsertTableStatement(catalogName, schemaName, tableName, columns);
        Connection connection = null;
        try {
            if (configuration.getDbType().isCanSetCatalog() && configuration.getDbType().isCanSetSchema()) {
                connection = connectionFactory.getConnection(catalogName, schemaName);
            } else if (!configuration.getDbType().isCanSetCatalog() && configuration.getDbType().isCanSetSchema()) {
                connection = connectionFactory.getConnection(null, schemaName);
            } else if (configuration.getDbType().isCanSetCatalog() && !configuration.getDbType().isCanSetSchema()) {
                connection = connectionFactory.getConnection(catalogName, null);
            } else {
                connection = connectionFactory.getConnection();
            }
            int[] rows = JdbcUtils.batchUpdate(connection, insertSql, argsList);
            if (log.isInfoEnabled()) {
                log.info("Execute dml: {}", insertSql);
            }
            if (log.isInfoEnabled()) {
                log.info("Add {} rows to table: {}", Arrays.stream(rows).reduce(0, Integer::sum), tableName);
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Unable to execute sql: {}", insertSql, e);
            }
        } finally {
            connectionFactory.close(connection);
        }
    }

    protected boolean shouldFilterColumn(String columnName, TableMetaData tableMetaData) {
        return tableMetaData.isAutoIncrementColumn(columnName);
    }

    @Override
    public void releaseExternalResource() {
        if (connectionFactory != null) {
            connectionFactory.destroy();
        }
    }
}