package io.doodler.jdbc.impexp;

import io.doodler.jdbc.impexp.ImportingExportHandler.Configuration;

/**
 * @Description: ImportingExporter
 * @Author: Fred Feng
 * @Date: 31/03/2023
 * @Version 1.0.0
 */
public final class ImportingExporter {

    public ImportingExporter() {
        this.importingExportHandler = new ImportingExportHandler();
        this.exporter.setExportHandler(importingExportHandler);
    }

    private final ImportingExportHandler importingExportHandler;
    private final Exporter exporter = new Exporter();

    public Configuration getConfiguration() {
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