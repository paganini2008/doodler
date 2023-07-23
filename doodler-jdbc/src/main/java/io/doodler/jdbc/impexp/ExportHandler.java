package io.doodler.jdbc.impexp;

import java.util.List;
import java.util.Map;

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

    void exportData(String catalogName, String schemaName, String tableName,
                    TableMetaData tableMetaData, List<Map<String, Object>> dataList, boolean idReused) throws Exception;
}