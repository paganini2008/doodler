package com.github.doodler.common.jdbc.impexp;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StopWatch;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description: ServerMetaData
 * @Author: Fred Feng
 * @Date: 25/03/2023
 * @Version 1.0.0
 */
@Slf4j
public class ServerMetaData implements MetaData {

	private final String username;
	private final String password;
	private final DatabaseMetaData metaData;
	private final MetaDataOperations metaDataOperations;
	private final Dialect dialect;

	public ServerMetaData(String username, String password, DatabaseMetaData metaData, MetaDataOperations metaDataOperations,
			Dialect dialect) {
		this.username = username;
		this.password = password;
		this.metaData = metaData;
		this.metaDataOperations = metaDataOperations;
		this.dialect = dialect;
	}

	private final List<CatalogMetaData> catalogMetaDatas = new ArrayList<>();
	private final StopWatch stopWatch = new StopWatch();

	public Optional<CatalogMetaData> findCatalogMetaData(String catalog) {
		return catalogMetaDatas.stream().filter(md -> md.getCatalogName().equals(catalog)).findFirst();
	}

	public Optional<SchemaMetaData> findSchemaMetaData(String catalog, String schema) {
		Optional<CatalogMetaData> opt = findCatalogMetaData(catalog);
		if (opt.isPresent()) {
			return opt.get().findSchemaMetaData(schema);
		}
		return Optional.empty();
	}

	public Optional<TableMetaData> findTableMetaData(String catalog, String schema, String table) {
		Optional<SchemaMetaData> opt = findSchemaMetaData(catalog, schema);
		if (opt.isPresent()) {
			return opt.get().getTableMetaDatas().stream().filter(md -> md.getTableName().equals(table)).findFirst();
		}
		return Optional.empty();
	}

	@Override
	public void accept(MetaDataVisitor visitor) throws SQLException {
		long startTime = System.currentTimeMillis();
		if(log.isInfoEnabled()) {
			log.info("Begin to process ...");
		}
		visitor.visit(this);

		Exporter.ExportConfiguration configuration = visitor.getConfiguration();
		
		List<Map<String, Object>> infoList = metaDataOperations.getCatalogInfos(metaData);
		for (Map<String, Object> catalogInfo : infoList) {
			String catalogName = (String) catalogInfo.get("TABLE_CAT");
			if (ArrayUtils.isEmpty(configuration.getIncludedCatalogNames())
					|| ArrayUtils.contains(configuration.getIncludedCatalogNames(), catalogName)) {
				catalogMetaDatas.add(new CatalogMetaData(catalogName, catalogInfo, this));
			}
		}

		for (CatalogMetaData catalogMetaData : catalogMetaDatas) {
			stopWatch.start(catalogMetaData.getCatalogName());
			catalogMetaData.accept(visitor);
			stopWatch.stop();
		}
		if(log.isInfoEnabled()) {
			log.info("End to process. Total time: {} (ms)\n", (System.currentTimeMillis()-startTime));
			log.info(stopWatch.prettyPrint());
		}
	}

	@Override
	public String[] getStatements() throws SQLException {
		String username = getUsername();
		String password = getPassword();
		if (StringUtils.isNotBlank(username)) {
			List<String> sqls = new ArrayList<>();
			String statement = getDialect().getCreateUserStatement(username, password);
			sqls.add(statement);

			String[] after = getDialect().getStatementAfterUserCreated(username);
			if (ArrayUtils.isNotEmpty(after)) {
				sqls.addAll(Arrays.asList(after));
			}
			return sqls.toArray(new String[0]);
		}
		return null;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public DatabaseMetaData getMetaData() {
		return metaData;
	}

	@Override
	public Dialect getDialect() {
		return dialect;
	}

	@Override
	public Map<String, Object> getDetail() {
		return null;
	}

	public MetaDataOperations getMetaDataOperations() {
		return metaDataOperations;
	}

	public List<CatalogMetaData> getCatalogMetaDatas() {
		return catalogMetaDatas;
	}
}