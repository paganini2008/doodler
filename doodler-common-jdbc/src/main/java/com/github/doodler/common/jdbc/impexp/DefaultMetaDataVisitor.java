package com.github.doodler.common.jdbc.impexp;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;

import org.apache.commons.lang3.ArrayUtils;
import com.github.doodler.common.jdbc.impexp.DdlScripter.Catalog;
import com.github.doodler.common.jdbc.impexp.DdlScripter.PartitionTable;
import com.github.doodler.common.jdbc.impexp.DdlScripter.Schema;
import com.github.doodler.common.jdbc.impexp.DdlScripter.Table;

/**
 * @Description: DefaultMetaDataVisitor
 * @Author: Fred Feng
 * @Date: 25/03/2023
 * @Version 1.0.0
 */
public class DefaultMetaDataVisitor implements MetaDataVisitor {

    private final Exporter.ExportConfiguration configuration;
    private final DdlScripter ddlScripter;

    public DefaultMetaDataVisitor(Exporter.ExportConfiguration configuration, DdlScripter ddlScripter) {
        this.configuration = configuration;
        this.ddlScripter = ddlScripter;
    }

    @Override
    public Exporter.ExportConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public DdlScripter getDdlScripter() {
        return ddlScripter;
    }

    @Override
    public void visit(ServerMetaData metaData) throws SQLException {
        String[] statements = metaData.getStatements();
        if (ArrayUtils.isNotEmpty(statements) && configuration.isShowCreateUserSql()) {
            getDdlScripter().getBeforeStatements().addAll(Arrays.asList(statements));
        }
    }

    @Override
    public void visit(CatalogMetaData metaData) throws SQLException {
        String[] statements = metaData.getStatements();
        if (ArrayUtils.isNotEmpty(statements) && configuration.isShowCreateCatalogSql()) {
            Catalog catalog = getDdlScripter().getCatalog(metaData.getCatalogName());
            catalog.getCatalogStatements().addAll(Arrays.asList(statements));
        }
    }

    @Override
    public void visit(SchemaMetaData metaData) throws SQLException {
        String[] statements = metaData.getStatements();
        if (ArrayUtils.isNotEmpty(statements) && configuration.isShowCreateSchemaSql()) {
            Schema schema = getDdlScripter().getSchema(metaData.getCatalogName(), metaData.getSchemaName());
            schema.getSchemaStatements().addAll(Arrays.asList(statements));
        }
    }

    @Override
    public void visit(TableMetaData metaData) throws SQLException {
        String[] statements = metaData.getStatements();
        if (ArrayUtils.isNotEmpty(statements)) {
            Table table = getDdlScripter().getTable(metaData.getCatalogName(), metaData.getSchemaName(),
                    metaData.getTableName());
            table.getCreateTableStatements().addAll(Arrays.asList(statements));
            if (configuration.isTableRecreated()) {
                String dropStatement = configuration.getDialect().getDropTableStatement(metaData.getCatalogName(),
                        metaData.getSchemaName(),
                        metaData.getTableName());
                table.getBeforeStatements().add(0, dropStatement);
            }
        }
    }
    
    @Override
    public void visit(PartitionTableMetaData metaData) throws SQLException {
        String[] statements = metaData.getStatements();
        if (ArrayUtils.isNotEmpty(statements)) {
            PartitionTable table = getDdlScripter().getPartitionTable(metaData.getCatalogName(), metaData.getSchemaName(),
                    metaData.getTableName());
            table.getCreateTableStatements().addAll(Arrays.asList(statements));
            if (configuration.isTableRecreated()) {
                String dropStatement = configuration.getDialect().getDropTableStatement(metaData.getCatalogName(),
                        metaData.getSchemaName(),
                        metaData.getTableName());
                table.getBeforeStatements().add(0, dropStatement);
            }
        }
    }

    @Override
    public void visit(ColumnMetaData metaData) throws SQLException {
        String[] statements = metaData.getStatements();
        if (ArrayUtils.isNotEmpty(statements)) {
            Table table = getDdlScripter().getTable(metaData.getCatalogName(), metaData.getSchemaName(),
                    metaData.getTableName());
            table.getColumnStatements().addAll(Arrays.asList(statements));
        }
    }

    @Override
    public void visit(PrimaryKeyMetaData metaData) throws SQLException {
        String[] statements = metaData.getStatements();
        if (ArrayUtils.isNotEmpty(statements)) {
        	String tableName = metaData.getTableName();
        	Optional<TableMetaData> opt = metaData.unwrap(SchemaMetaData.class).findTableMetaData(tableName);
        	if(opt.get().isPartitionTable()) {
        		PartitionTable table = getDdlScripter().getPartitionTable(metaData.getCatalogName(), metaData.getSchemaName(),
                        metaData.getTableName());
                table.getPrimaryKeyStatements().addAll(Arrays.asList(statements));
        	}else {
                Table table = getDdlScripter().getTable(metaData.getCatalogName(), metaData.getSchemaName(),
                        metaData.getTableName());
                table.getPrimaryKeyStatements().addAll(Arrays.asList(statements));
        	}
        }
    }

	@Override
	public void visit(PartitionExpressionMetaData metaData) throws SQLException {
		String[] statements = metaData.getStatements();
		if (ArrayUtils.isNotEmpty(statements)) {
			PartitionTable table = getDdlScripter().getPartitionTable(metaData.getCatalogName(), metaData.getSchemaName(),
                    metaData.getTableName());
			table.getPartitionStatements().addAll(Arrays.asList(statements));
		}
	}

	@Override
    public void visit(CommentMetaData metaData) throws SQLException {
        String[] statements = metaData.getStatements();
        if (ArrayUtils.isNotEmpty(statements)) {
            Table table = getDdlScripter().getTable(metaData.getCatalogName(), metaData.getSchemaName(),
                    metaData.getTableName());
            table.getCommentStatements().addAll(Arrays.asList(statements));
        }
    }

    @Override
    public void visit(IndexMetaData metaData) throws SQLException {
        String[] statements = metaData.getStatements();
        if (ArrayUtils.isNotEmpty(statements)) {
            Table table = getDdlScripter().getTable(metaData.getCatalogName(), metaData.getSchemaName(),
                    metaData.getTableName());
            table.getIndexStatements().addAll(Arrays.asList(statements));
        }
    }

    @Override
    public void visit(CombinedIndexMetaData metaData) throws SQLException {
        String[] statements = metaData.getStatements();
        if (ArrayUtils.isNotEmpty(statements)) {
            Table table = getDdlScripter().getTable(metaData.getCatalogName(), metaData.getSchemaName(),
                    metaData.getTableName());
            table.getIndexStatements().addAll(Arrays.asList(statements));
        }
    }

    @Override
    public void visit(PartitionMetaData metaData) throws SQLException {
        String[] statements = metaData.getStatements();
        if (ArrayUtils.isNotEmpty(statements)) {
            Table table = getDdlScripter().getTable(metaData.getCatalogName(), metaData.getSchemaName(),
                    metaData.getTableName());
            table.getPartitionStatements().addAll(Arrays.asList(statements));
        }
    }
}