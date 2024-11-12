package com.github.doodler.common.jdbc.impexp;

import java.sql.SQLException;

/**
 * @Description: MetaDataVisitor
 * @Author: Fred Feng
 * @Date: 25/03/2023
 * @Version 1.0.0
 */
public interface MetaDataVisitor {

    void visit(ServerMetaData metaData) throws SQLException;

    void visit(CatalogMetaData metaData) throws SQLException;

    void visit(SchemaMetaData metaData) throws SQLException;

    void visit(TableMetaData metaData) throws SQLException;

    void visit(ColumnMetaData metaData) throws SQLException;

    void visit(PrimaryKeyMetaData metaData) throws SQLException;
    
    void visit(CommentMetaData metaData) throws SQLException;

    void visit(IndexMetaData metaData) throws SQLException;

    void visit(CombinedIndexMetaData metaData) throws SQLException;

    void visit(PartitionMetaData metaData) throws SQLException;

    void visit(PartitionTableMetaData metaData) throws SQLException;
    
    void visit(PartitionExpressionMetaData metaData) throws SQLException;

    Exporter.ExportConfiguration getConfiguration();

    DdlScripter getDdlScripter();
}