package com.github.doodler.common.jdbc.impexp;

import java.util.Map;
import com.github.doodler.common.jdbc.ConnectionFactory;
import com.github.doodler.common.page.EachPage;

/**
 * @Description: ExportHandler
 * @Author: Fred Feng
 * @Date: 30/03/2023
 * @Version 1.0.0
 */
public interface ExportHandler {

    default void start() {
    }

    default void releaseExternalResource() {
    }

    void exportDdl(DdlScripter ddlScripter) throws Exception;

    void exportData(String catalogName, 
    		        String schemaName, 
    		        String tableName, 
    		        TableMetaData tableMetaData, 
    		        EachPage<Map<String, Object>> eachPage,
    		        boolean idReused, 
    		        ConnectionFactory connectionFactory) throws Exception;
}