package com.github.doodler.common.jdbc.impexp;

import com.github.doodler.common.jdbc.impexp.ImportExportHandler.ImportConfiguration;

/**
 * @Description: ImportExporter
 * @Author: Fred Feng
 * @Date: 31/03/2023
 * @Version 1.0.0
 */
public class ImportExporter {

    public ImportExporter() {
    }

    private final ImportExportHandler importingExportHandler = new ImportExportHandler();
    private final Exporter exporter = new Exporter(importingExportHandler);
    
    public Exporter.ExportConfiguration getExportConfiguration() {
        return exporter.getConfiguration();
    }
    
    public void setMetaDataOperations(MetaDataOperations metaDataOperations) {
    	exporter.setMetaDataOperations(metaDataOperations);
    }

    public ImportConfiguration getImportConfiguration() {
        return importingExportHandler.getConfiguration();
    }

    public void exportDdlAndData() throws Exception {
        exporter.exportDdlAndData();
    }

    public void exportDdl() throws Exception {
        exporter.exportDdl();
    }

    public void exportData() throws Exception {
        exporter.exportData();
    }
}