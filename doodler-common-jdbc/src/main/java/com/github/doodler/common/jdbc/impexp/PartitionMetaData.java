package com.github.doodler.common.jdbc.impexp;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.ArrayUtils;

/**
 * @Description: PartitionMetaData
 * @Author: Fred Feng
 * @Date: 25/03/2023
 * @Version 1.0.0
 */
public class PartitionMetaData implements TiedMetaData {

    private final Map<String, Object> detail;
    private final TiedMetaData tiedMetaData;
    
    private final List<PartitionTableMetaData> partitionTableMetaDatas = new ArrayList<>(); 

    public PartitionMetaData(Map<String, Object> detail, TiedMetaData tiedMetaData) {
        this.detail = detail;
        this.tiedMetaData = tiedMetaData;
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
        return tiedMetaData.getTableName();
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
    public DatabaseMetaData getMetaData() {
        return tiedMetaData.getMetaData();
    }

    @Override
    public MetaDataOperations getMetaDataOperations() {
        return tiedMetaData.getMetaDataOperations();
    }

    @Override
    public Dialect getDialect() {
        return tiedMetaData.getDialect();
    }

    @Override
    public Map<String, Object> getDetail() {
        return detail;
    }

    @Override
    public void accept(MetaDataVisitor visitor) throws SQLException {
    	String[] partitionTableNames = (String[]) detail.get("PARTITION_TABLE_NAMES");
    	if(ArrayUtils.isNotEmpty(partitionTableNames)) {
    		for(String partitionTableName: partitionTableNames) {
    			Optional<TableMetaData> opt = tiedMetaData.unwrap(SchemaMetaData.class).findTableMetaData(partitionTableName);
    			if(opt.isPresent()) {
    				partitionTableMetaDatas.add(new PartitionTableMetaData(partitionTableName, opt.get().getDetail(), this));
    			}
    		}
    	}
    	for(PartitionTableMetaData partitionTableMetaData: partitionTableMetaDatas) {
    		partitionTableMetaData.accept(visitor);
    	}
        visitor.visit(this);
    }

    @Override
    public String[] getStatements() throws SQLException {
        String catalogName = getCatalogName();
        String schemaName = getSchemaName();
        String tableName = getTableName();
        String partitionType = (String) detail.get("PARTITION_TYPE");
        String columnNames = (String) detail.get("PARTITION_COLUMN_NAMES");
        String statement = getDialect().getDefinePartitionTableStatement(catalogName, schemaName, tableName, partitionType,
                columnNames);
        return new String[]{statement};
    }
}