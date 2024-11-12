package com.github.doodler.common.quartz;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.quartz.utils.ConnectionProvider;
import com.github.doodler.common.context.ApplicationContextUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: ApplicationContextConnectionProvider
 * @Author: Fred Feng
 * @Date: 22/06/2023
 * @Version 1.0.0
 */
@Slf4j
@Setter
@Getter
public class ApplicationContextConnectionProvider implements ConnectionProvider {

	private DataSource contextDataSource;
	private String dataSourceName;

	@Override
	public Connection getConnection() throws SQLException {
		synchronized (this) {
			if (contextDataSource == null) {
				this.contextDataSource = StringUtils.isNotBlank(dataSourceName)
						? ApplicationContextUtils.getBean(dataSourceName, DataSource.class)
						: ApplicationContextUtils.getBean(DataSource.class);
				if(log.isInfoEnabled()) {
					log.info("Initialize Quartz DataSource: {}", contextDataSource.toString());
				}
			}
		}
		return contextDataSource.getConnection();
	}

	@Override
	public void shutdown() throws SQLException {
	}

	@Override
	public void initialize() throws SQLException {
	}
}