package com.github.doodler.common.jdbc.impexp;

import java.sql.Connection;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import com.github.doodler.common.jdbc.ConnectionFactory;
import com.github.doodler.common.jdbc.SimpleConnectionFactory;
import com.github.doodler.common.jdbc.page.MapBasedPageReader;
import com.github.doodler.common.page.EachPage;
import com.github.doodler.common.page.PageReader;
import com.github.doodler.common.page.PageRequest;
import com.github.doodler.common.page.PageResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: Exporter
 * @Author: Fred Feng
 * @Date: 28/03/2023
 * @Version 1.0.0
 */
@Slf4j
@Getter
@Setter
@ToString
public class Exporter {

	private final ExportHandler exportHandler;
    private ExportConfiguration configuration = new ExportConfiguration();
    private MetaDataOperations metaDataOperations = new MetaDataOperations();

    public Exporter(ExportHandler exportHandler) {
    	this.exportHandler = exportHandler;
    }

    /**
     * @Description: Export
     * @Author: Fred Feng
     * @Date: 30/03/2023
     * @Version 1.0.0
     */
    @Getter
    @Setter
    @ToString
    public static class ExportConfiguration {

        private int port;
        private String hostname;
        private DbType dbType;
        private String username;
        private String password;
        private String url;
        private String defaultCatalogName;
        private String[] includedCatalogNames;
        private String[] includedSchemaNames;
        private String[] includedTableNames;
        private String includedTableNamePattern;
        private String[] includedTableNamesForBackupingData;
        private boolean idReused = false;
        private boolean showCreateUserSql = true;
        private boolean showCreateCatalogSql = true;
        private boolean showCreateSchemaSql = true;
        private boolean tableRecreated = true;
        private boolean failFast = true;
        private boolean connectionPoolEnabled = true;
        private int pageSize = 100;
        private Dialect dialect;
    }

    public void exportDdl() throws Exception {
        exportHandler.start();
        DdlScripter ddlScripter = new DdlScripter(configuration.getDialect());
        ConnectionFactory connectionFactory = createConnectionFactory();
        ServerMetaData serverMetaData = null;
        Connection connection = null;
        try {
            connection = connectionFactory.getConnection();
            serverMetaData = new ServerMetaData(configuration.getUsername(), configuration.getPassword(),
                    connection.getMetaData(),
                    metaDataOperations, configuration.getDialect());
            MetaDataVisitor metaDataVisitor = new DefaultMetaDataVisitor(configuration, ddlScripter);
            serverMetaData.accept(metaDataVisitor);
            exportHandler.exportDdl(ddlScripter);
        } finally {
            exportHandler.releaseExternalResource();
            connectionFactory.close(connection);
            connectionFactory.destroy();
        }
    }

    public void exportDdlAndData() throws Exception {
        exportHandler.start();
        DdlScripter ddlScripter = new DdlScripter(configuration.getDialect());
        ConnectionFactory connectionFactory = createConnectionFactory();
        ServerMetaData serverMetaData = null;
        Connection connection = null;
        try {
            connection = connectionFactory.getConnection();
            serverMetaData = new ServerMetaData(configuration.getUsername(), configuration.getPassword(),
                    connection.getMetaData(),
                    metaDataOperations, configuration.getDialect());
            MetaDataVisitor metaDataVisitor = new DefaultMetaDataVisitor(configuration, ddlScripter);
            serverMetaData.accept(metaDataVisitor);
            exportHandler.exportDdl(ddlScripter);
        } finally {
            connectionFactory.close(connection);
        }

        serverMetaData.getCatalogMetaDatas().forEach(catalogMd -> {
            final String catalogName = catalogMd.getCatalogName();
            catalogMd.getSchemaMetaDatas().forEach(schemaMd -> {
                final String schemaName = schemaMd.getSchemaName();
                schemaMd.getTableMetaDatas().forEach(tableMd -> {
                    final String tableName = tableMd.getTableName();
                    if (ArrayUtils.isEmpty(configuration.getIncludedTableNamesForBackupingData())
                            || ArrayUtils.contains(configuration.getIncludedTableNamesForBackupingData(), tableName)) {
                        scanTable(catalogName, schemaName, tableName, tableMd, connectionFactory);
                    }
                });
            });
        });
        exportHandler.releaseExternalResource();
        connectionFactory.destroy();
    }

    public void exportData() throws Exception {
        exportHandler.start();
        DdlScripter ddlScripter = new DdlScripter(configuration.getDialect());
        ConnectionFactory connectionFactory = createConnectionFactory();
        ServerMetaData serverMetaData = null;
        Connection connection = null;
        try {
            connection = connectionFactory.getConnection();
            serverMetaData = new ServerMetaData(configuration.getUsername(), configuration.getPassword(),
                    connection.getMetaData(),
                    metaDataOperations, configuration.getDialect());
            MetaDataVisitor metaDataVisitor = new DefaultMetaDataVisitor(configuration, ddlScripter);
            serverMetaData.accept(metaDataVisitor);
        } finally {
            connectionFactory.close(connection);
        }

        serverMetaData.getCatalogMetaDatas().forEach(catalogMd -> {
            final String catalogName = catalogMd.getCatalogName();
            catalogMd.getSchemaMetaDatas().forEach(schemaMd -> {
                final String schemaName = schemaMd.getSchemaName();
                schemaMd.getTableMetaDatas().forEach(tableMd -> {
                    final String tableName = tableMd.getTableName();
                    if (ArrayUtils.isEmpty(configuration.getIncludedTableNamesForBackupingData())
                            || ArrayUtils.contains(configuration.getIncludedTableNamesForBackupingData(), tableName)) {
                        scanTable(catalogName, schemaName, tableName, tableMd, connectionFactory);
                    }
                });
            });
        });
        exportHandler.releaseExternalResource();
        connectionFactory.destroy();
    }

    private ConnectionFactory createConnectionFactory() {
        String jdbcUrl = configuration.getUrl();
        if (StringUtils.isBlank(jdbcUrl)) {
            if (StringUtils.isBlank(configuration.getHostname())) {
                throw new IllegalArgumentException("Database server host name must be required.");
            }
            if (configuration.getPort() <= 0) {
                throw new IllegalArgumentException("Database server port is invalid.");
            }
            String catalogName = configuration.getDefaultCatalogName();
            jdbcUrl = configuration.getDbType().getUrl(configuration.getHostname(), configuration.getPort(),
                    catalogName);
        }
        if (configuration.isConnectionPoolEnabled()) {
            return new HikariDataSourceConnectionFactory(configuration.getDbType().getDriverClassName(), jdbcUrl,
                    configuration.getUsername(), configuration.getPassword());
        }
        return new SimpleConnectionFactory(configuration.getDbType().getDriverClassName(), jdbcUrl,
                configuration.getUsername(), configuration.getPassword());
    }

    private void scanTable(String catalogName, String schemaName, String tableName, TableMetaData tableMetaData,
                           ConnectionFactory connectionFactory) {
        String sql = tableMetaData.getDialect().getSelectTableStatement(catalogName, schemaName, tableName);
        PageReader<Map<String, Object>> pageReader = new MapBasedPageReader(connectionFactory, sql, -1);
        PageResponse<Map<String, Object>> pageResponse = pageReader.list(PageRequest.of(1, configuration.getPageSize()));
        for (EachPage<Map<String, Object>> eachPage : pageResponse) {
            try {
                exportHandler.exportData(catalogName, schemaName, tableName, tableMetaData, eachPage, configuration.isIdReused(), connectionFactory);
            } catch (Exception e) {
                if (configuration.isFailFast()) {
                	throw new ImpExpException(e.getMessage(), e);
                } else {
                    if (log.isErrorEnabled()) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        }
    }
}