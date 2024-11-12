package com.github.doodler.common.jdbc.impexp;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.github.doodler.common.utils.MapUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: TableMetaData
 * @Author: Fred Feng
 * @Date: 25/03/2023
 * @Version 1.0.0
 */
@Slf4j
public class TableMetaData implements TiedMetaData {

    private final String tableName;
    private final Map<String, Object> detail;
    private final TiedMetaData tiedMetaData;

    public TableMetaData(String tableName, Map<String, Object> detail, TiedMetaData tiedMetaData) {
        this.tableName = tableName;
        this.detail = detail;
        this.tiedMetaData = tiedMetaData;
    }

    private final List<ColumnMetaData> columnMetaDatas = new ArrayList<>();
    private final List<PrimaryKeyMetaData> primaryKeyMetaDatas = new ArrayList<>();
    private final Map<String, List<IndexMetaData>> indexMetaDatas = new HashMap<>();
    private final List<PartitionMetaData> partitionMetaDatas = new ArrayList<>();

    public Optional<ColumnMetaData> findColumnMetaData(String columnName) {
        return columnMetaDatas.stream().filter(md -> md.getColumnName().equals(columnName)).findFirst();
    }

    @Override
    public void accept(MetaDataVisitor visitor) throws SQLException {
    	if(isPartitionTable()) {
    		return;
    	}
    	if(log.isInfoEnabled()) {
    		log.info("Begin to process table: {}",tableName);
    	}
        final boolean partitioned = isPartitioned();
        DatabaseMetaData databaseMetaData = getMetaData();
        List<Map<String, Object>> infoList = null;
        if (!partitioned) {
            infoList = getMetaDataOperations().getPrimaryKeyInfos(databaseMetaData, getCatalogName(),
            		getSchemaName(), tableName);
            for (Map<String, Object> pkInfo : infoList) {
                String columnName = (String) pkInfo.get("COLUMN_NAME");
                primaryKeyMetaDatas.add(new PrimaryKeyMetaData(columnName, pkInfo, this));
            }
            for (PrimaryKeyMetaData primaryKeyMetaData : primaryKeyMetaDatas) {
                primaryKeyMetaData.accept(visitor);
            }
        }

        infoList = getMetaDataOperations().getColumnInfos(databaseMetaData,
                getCatalogName(),getSchemaName(), tableName);
        for (Map<String, Object> columnInfo : infoList) {
            String columnName = (String) columnInfo.get("COLUMN_NAME");
            columnMetaDatas.add(new ColumnMetaData(columnName, columnInfo, this));
        }
        for (ColumnMetaData columnMetaData : columnMetaDatas) {
            columnMetaData.accept(visitor);
        }

        infoList = getMetaDataOperations().getIndexInfos(databaseMetaData, getCatalogName(),
                getSchemaName(), tableName);
        for (Map<String, Object> indexInfo : infoList) {
            String indexName = (String) indexInfo.get("INDEX_NAME");
            String columnName = (String) indexInfo.get("COLUMN_NAME");
            List<IndexMetaData> subList = MapUtils.getOrCreate(indexMetaDatas, indexName, ArrayList::new);
            subList.add(new IndexMetaData(indexName, columnName, indexInfo, this));
        }
        for (Map.Entry<String, List<IndexMetaData>> entry : indexMetaDatas.entrySet()) {
            if (entry.getValue().size() == 1) {
                entry.getValue().get(0).accept(visitor);
            } else if (entry.getValue().size() > 1) {
                new CombinedIndexMetaData(entry.getKey(), entry.getValue(), this).accept(visitor);
            }
        }

        if (partitioned) {
            partitionMetaDatas.add(new PartitionMetaData(detail, this));
        }
        for (PartitionMetaData partitionMetaData : partitionMetaDatas) {
            partitionMetaData.accept(visitor);
        }

        visitor.visit(this);
        
    	if(log.isInfoEnabled()) {
    		log.info("End to process table: {}",tableName);
    	}
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
    	}catch (RuntimeException e) {
			return tiedMetaData.unwrap(clz);
		}
	}

	@Override
    public Map<String, Object> getDetail() {
        return detail;
    }

    @Override
    public DatabaseMetaData getMetaData() {
        return tiedMetaData.getMetaData();
    }

    @Override
    public Dialect getDialect() {
        return tiedMetaData.getDialect();
    }

    @Override
    public MetaDataOperations getMetaDataOperations() {
        return tiedMetaData.getMetaDataOperations();
    }

    @Override
    public String[] getStatements() throws SQLException {
        String catalogName = getCatalogName();
        String schemaName = getSchemaName();
        String tableName = getTableName();
        String statement = getDialect().getCreateTableStatement(catalogName, schemaName, tableName);
        return new String[]{statement};
    }

    public List<ColumnMetaData> getColumnMetaDatas() {
        return columnMetaDatas;
    }

    public List<PrimaryKeyMetaData> getPrimaryKeyMetaDatas() {
        return primaryKeyMetaDatas;
    }

    public List<IndexMetaData> getIndexMetaDatas() {
        List<IndexMetaData> total = new ArrayList<>();
        for (List<IndexMetaData> subList : indexMetaDatas.values()) {
            total.addAll(subList);
        }
        return total;
    }

    public boolean isAutoIncrementColumn(String columnName) {
        return columnMetaDatas.stream().anyMatch(
                md -> md.getColumnName().equals(columnName) &&
                        "YES".equalsIgnoreCase((String) md.getDetail().get("IS_AUTOINCREMENT")));
    }

    public boolean isPrimaryKeyColumn(String columnName) {
        return primaryKeyMetaDatas.stream().anyMatch(md -> md.getColumnName().equals(columnName));
    }
    
    public boolean isPartitioned() {
    	return detail.containsKey("IS_PARTITIONED") && (Boolean) detail.get("IS_PARTITIONED");
    }

    public boolean isPartitionTable() {
        return detail.containsKey("IS_PARTITION_TABLE") && (Boolean) detail.get("IS_PARTITION_TABLE");
    }
}