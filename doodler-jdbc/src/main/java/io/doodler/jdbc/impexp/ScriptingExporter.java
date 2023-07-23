package io.doodler.jdbc.impexp;

import java.io.File;

import io.doodler.jdbc.impexp.Exporter.Configuration;

/**
 * @Description: ScriptingExporter
 * @Author: Fred Feng
 * @Date: 31/03/2023
 * @Version 1.0.0
 */
public final class ScriptingExporter {

	public ScriptingExporter(File root, boolean separate) {
		exporter.setExportHandler(new ScriptingExportHandler(root, separate));
	}

	private final Exporter exporter = new Exporter();

	public Configuration getConfiguration() {
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