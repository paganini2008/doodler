package com.github.doodler.common.jdbc.impexp;

import java.io.File;

/**
 * @Description: ScriptExporter
 * @Author: Fred Feng
 * @Date: 31/03/2023
 * @Version 1.0.0
 */
public final class ScriptExporter {

	public ScriptExporter(File root, boolean separate) {
		this.exporter = new Exporter(new ScriptExportHandler(root, separate));
	}

	private final Exporter exporter;

	public Exporter.ExportConfiguration getConfiguration() {
		return exporter.getConfiguration();
	}

	public void setMetaDataOperations(MetaDataOperations metaDataOperations) {
		exporter.setMetaDataOperations(metaDataOperations);
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