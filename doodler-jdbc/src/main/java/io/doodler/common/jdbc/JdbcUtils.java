package io.doodler.common.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;

import com.google.common.base.CaseFormat;

import io.doodler.common.utils.CaseInsensitiveMap;
import io.doodler.common.utils.ConvertUtils;
import lombok.experimental.UtilityClass;

/**
 * @Description: JdbcUtils
 * @Author: Fred Feng
 * @Date: 24/03/2023
 * @Version 1.0.0
 */
@UtilityClass
public class JdbcUtils {

	public static Connection getConnection(String url, String user, String password) throws SQLException {
		if (StringUtils.isBlank(user) && StringUtils.isBlank(password)) {
			return DriverManager.getConnection(url);
		}
		return DriverManager.getConnection(url, user, password);
	}

	public int update(Connection connection, String sql) throws SQLException {
		Statement stm = null;
		try {
			stm = connection.createStatement();
			return stm.executeUpdate(sql);
		} finally {
			closeQuietly(stm);
		}
	}

	public int update(Connection connection, String sql, Object[] args) throws SQLException {
		return update(connection, sql, setValues(args));
	}

	public int update(Connection connection, String sql, PreparedStatementCallback callback) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(sql);
			if (callback != null) {
				callback.setValues(ps);
			}
			return ps.executeUpdate();
		} finally {
			closeQuietly(ps);
		}
	}

	public int[] batchUpdate(Connection connection, String sql, List<Object[]> argsList) throws SQLException {
		return batchUpdate(connection, sql, setValues(argsList));
	}

	public int[] batchUpdate(Connection connection, String sql, PreparedStatementCallback callback) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(sql);
			if (callback != null) {
				callback.setValues(ps);
			}
			return ps.executeBatch();
		} finally {
			closeQuietly(ps);
		}
	}
	
	public List<Map<String, Object>> fetchAll(Connection connection, String sql) throws SQLException {
		List<Map<String, Object>> list = new ArrayList<>();
		Statement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = ps.executeQuery(sql);
			if (rs != null) {
				while (rs.next()) {
					list.add(toMap(rs, false));
				}
			}
			return list;
		} finally {
			closeQuietly(rs);
			closeQuietly(ps);
		}
	}

	public <T> List<T> fetchAll(Connection connection, String sql,Class<T> resultClass) throws SQLException {
		Statement sm = null;
		ResultSet rs = null;
		List<T> list = new ArrayList<>();
		try {
			sm = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = sm.executeQuery(sql);
			if (rs != null) {
				while (rs.next()) {
					list.add(toObject(rs, resultClass));
				}
			}
			return list;
		} finally {
			closeQuietly(rs);
			closeQuietly(sm);
		}
	}

	public List<Map<String, Object>> fetchAll(Connection connection, String sql, Object[] args) throws SQLException {
		return fetchAll(connection, sql, setValues(args));
	}

	public List<Map<String, Object>> fetchAll(Connection connection, String sql, PreparedStatementCallback callback) throws SQLException {
		List<Map<String, Object>> list = new ArrayList<>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(sql);
			if (callback != null) {
				callback.setValues(ps);
			}
			rs = ps.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					list.add(toMap(rs, false));
				}
			}
			return list;
		} finally {
			closeQuietly(rs);
			closeQuietly(ps);
		}
	}

	public <T> List<T> fetchAll(Connection connection, String sql, Object[] args, Class<T> resultClass) throws SQLException {
		return fetchAll(connection, sql, setValues(args), resultClass);
	}

	public <T> List<T> fetchAll(Connection connection, String sql, PreparedStatementCallback callback, Class<T> resultClass)
			throws SQLException {
		List<T> list = new ArrayList<>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(sql);
			if (callback != null) {
				callback.setValues(ps);
			}
			rs = ps.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					list.add(toObject(rs, resultClass));
				}
			}
			return list;
		} finally {
			closeQuietly(rs);
			closeQuietly(ps);
		}
	}

	public static <T> T fetchOne(Connection connection, String sql, Class<T> requiredType) throws SQLException {
		Map<String, Object> one = fetchOne(connection, sql);
		if (one == null || one.isEmpty()) {
			return null;
		}
		return ConvertUtils.convert(one.values().toArray()[0], requiredType);
	}

	public static Map<String, Object> fetchOne(Connection connection, String sql) throws SQLException {
		Statement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.createStatement();
			rs = ps.executeQuery(sql);
			if (rs != null && rs.next()) {
				return toMap(rs, false);
			}
			return null;
		} finally {
			closeQuietly(rs);
			closeQuietly(ps);
		}
	}

	public static <T> T fetchOne(Connection connection, String sql, Object[] args, Class<T> requiredType) throws SQLException {
		return fetchOne(connection, sql, setValues(args), requiredType);
	}

	public static <T> T fetchOne(Connection connection, String sql, PreparedStatementCallback callback, Class<T> requiredType)
			throws SQLException {
		Map<String, Object> one = fetchOne(connection, sql, callback);
		if (one == null || one.isEmpty()) {
			return null;
		}
		return ConvertUtils.convert(one.values().toArray()[0], requiredType);
	}

	public static Map<String, Object> fetchOne(Connection connection, String sql, Object[] args) throws SQLException {
		return fetchOne(connection, sql, setValues(args));
	}

	public static Map<String, Object> fetchOne(Connection connection, String sql, PreparedStatementCallback callback) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(sql);
			if (callback != null) {
				callback.setValues(ps);
			}
			rs = ps.executeQuery();
			if (rs != null && rs.next()) {
				return toMap(rs, false);
			}
			return null;
		} finally {
			closeQuietly(rs);
			closeQuietly(ps);
		}
	}

	public Cursor<Map<String, Object>> getCursor(Connection connection, String sql, Object[] args) throws SQLException {
		return getCursor(connection, sql, setValues(args));
	}

	public Cursor<Map<String, Object>> getCursor(Connection connection, String sql, PreparedStatementCallback callback)
			throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		DisposableObservable ob = new DisposableObservable();
		try {
			ps = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			if (callback != null) {
				callback.setValues(ps);
			}
			rs = ps.executeQuery();
			return new MapCursor(rs, ob, true);
		} finally {
			closeLazily(ob, rs, ps, null);
		}
	}

	public <T> Cursor<T> getCursor(Connection connection, String sql, Object[] args, Class<T> resultClass) throws SQLException {
		return getCursor(connection, sql, setValues(args), resultClass);
	}

	public <T> Cursor<T> getCursor(Connection connection, String sql, PreparedStatementCallback callback, Class<T> resultClass)
			throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		DisposableObservable ob = new DisposableObservable();
		try {
			ps = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			if (callback != null) {
				callback.setValues(ps);
			}
			rs = ps.executeQuery();
			return new ObjectCursor<T>(rs, ob, resultClass);
		} finally {
			closeLazily(ob, rs, ps, null);
		}
	}

	private PreparedStatementCallback setValues(Object[] args) {
		return ps -> {
			setValues(ps, args);
		};
	}

	private PreparedStatementCallback setValues(List<Object[]> argsList) {
		return ps -> {
			for (Object[] args : argsList) {
				if (args != null && args.length > 0) {
					setValues(ps, args);
					ps.addBatch();
				}
			}
		};
	}

	public static void setValues(PreparedStatement ps, Object[] args) throws SQLException {
		if (args != null && args.length > 0) {
			int parameterIndex = 1;
			for (Object arg : args) {
				ps.setObject(parameterIndex++, arg);
			}
		}
	}

	public Map<String, Object> toMap(ResultSet rs, boolean caseSensitive) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		Map<String, Object> delegate = new LinkedHashMap<>(columnCount);
		Map<String, Object> info = caseSensitive ? delegate : new CaseInsensitiveMap<Object>(delegate);
		for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
			String columnLabel = rsmd.getColumnLabel(columnIndex);
			Object value = rs.getObject(columnIndex);
			info.put(columnLabel, value);
		}
		return info;
	}

	public <T> T toObject(ResultSet rs, Class<T> resultClass) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		T object;
		try {
			object = ConstructorUtils.invokeConstructor(resultClass);
		} catch (Exception e) {
			throw new DataAccessException(e.getMessage(), e);
		}
		String columnLabel, propertyName;
		for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
			columnLabel = rsmd.getColumnLabel(columnIndex);
			Object value = rs.getObject(columnIndex);
			propertyName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, columnLabel.toLowerCase());
			try {
				BeanUtils.setProperty(object, propertyName, value);
			} catch (Exception e) {
				throw new DataAccessException(e.getMessage(), e);
			}
		}
		return object;
	}
	
	public Cursor<Map<String, Object>> getCatalogInfos(Connection connection) throws SQLException {
		return getCatalogInfos(connection.getMetaData());
	}

	public Cursor<Map<String, Object>> getCatalogInfos(DatabaseMetaData databaseMetaData) throws SQLException {
		ResultSet rs = null;
		DisposableObservable ob = new DisposableObservable();
		try {
			rs = databaseMetaData.getCatalogs();
			return new MapCursor(rs, ob, false);
		} finally {
			closeLazily(ob, rs, null, null);
		}
	}

	public Cursor<Map<String, Object>> getSchemaInfos(Connection connection, String catalog) throws SQLException {
		return getSchemaInfos(connection.getMetaData(), catalog);
	}

	public Cursor<Map<String, Object>> getSchemaInfos(DatabaseMetaData databaseMetaData, String catalog) throws SQLException {
		ResultSet rs = null;
		DisposableObservable ob = new DisposableObservable();
		try {
			rs = databaseMetaData.getSchemas(catalog, "%");
			return new MapCursor(rs, ob, false);
		} finally {
			closeLazily(ob, rs, null, null);
		}
	}

	public Cursor<Map<String, Object>> getTableInfos(Connection connection, String catalog, String schema) throws SQLException {
		return getTableInfos(connection.getMetaData(), catalog, schema);
	}

	public Cursor<Map<String, Object>> getTableInfos(DatabaseMetaData databaseMetaData, String catalog, String schema) throws SQLException {
		ResultSet rs = null;
		DisposableObservable ob = new DisposableObservable();
		try {
			rs = databaseMetaData.getTables(catalog, schema, "%", new String[] { "TABLE" });
			return new MapCursor(rs, ob, false);
		} finally {
			closeLazily(ob, rs, null, null);
		}
	}

	public Cursor<Map<String, Object>> getColumnInfos(Connection connection, String catalog, String schema, String tableName)
			throws SQLException {
		return getColumnInfos(connection.getMetaData(), catalog, schema, tableName);
	}

	public Cursor<Map<String, Object>> getColumnInfos(DatabaseMetaData databaseMetaData, String catalog, String schema, String tableName)
			throws SQLException {
		ResultSet rs = null;
		DisposableObservable ob = new DisposableObservable();
		try {
			rs = databaseMetaData.getColumns(catalog, schema, tableName, null);
			return new MapCursor(rs, ob, false);
		} finally {
			closeLazily(ob, rs, null, null);
		}
	}

	public Cursor<Map<String, Object>> getPrimaryKeyInfos(Connection connection, String catalog, String schema, String tableName)
			throws SQLException {
		return getPrimaryKeyInfos(connection.getMetaData(), catalog, schema, tableName);
	}

	public Cursor<Map<String, Object>> getPrimaryKeyInfos(DatabaseMetaData databaseMetaData, String catalog, String schema,
			String tableName) throws SQLException {
		ResultSet rs = null;
		DisposableObservable ob = new DisposableObservable();
		try {
			rs = databaseMetaData.getPrimaryKeys(catalog, schema, tableName);
			return new MapCursor(rs, ob, false);
		} finally {
			closeLazily(ob, rs, null, null);
		}
	}

	public Cursor<Map<String, Object>> getImportedKeyInfos(Connection connection, String catalog, String schema, String tableName)
			throws SQLException {
		return getImportedKeyInfos(connection.getMetaData(), catalog, schema, tableName);
	}

	public Cursor<Map<String, Object>> getImportedKeyInfos(DatabaseMetaData databaseMetaData, String catalog, String schema,
			String tableName) throws SQLException {
		ResultSet rs = null;
		DisposableObservable ob = new DisposableObservable();
		try {
			rs = databaseMetaData.getImportedKeys(catalog, schema, tableName);
			return new MapCursor(rs, ob, false);
		} finally {
			closeLazily(ob, rs, null, null);
		}
	}

	public Cursor<Map<String, Object>> getIndexInfos(Connection connection, String catalog, String schema, String tableName)
			throws SQLException {
		return getIndexInfos(connection.getMetaData(), catalog, schema, tableName);
	}

	public Cursor<Map<String, Object>> getIndexInfos(DatabaseMetaData databaseMetaData, String catalog, String schema, String tableName)
			throws SQLException {
		ResultSet rs = null;
		DisposableObservable ob = new DisposableObservable();
		try {
			rs = databaseMetaData.getIndexInfo(catalog, schema, tableName, false, false);
			return new MapCursor(rs, ob, false);
		} finally {
			closeLazily(ob, rs, null, null);
		}
	}

	private static void closeLazily(Observable observable, final ResultSet rs, final Statement sm, final Connection connection) {
		observable.addObserver((ob, arg) -> {
			closeQuietly(rs);
			closeQuietly(sm);
			closeQuietly(connection);
		});
	}

	public void close(Connection connection) throws SQLException {
		if (connection != null) {
			connection.close();
		}
	}

	public void close(ResultSet rs) throws SQLException {
		if (rs != null) {
			rs.close();
		}
	}

	public void close(Statement stmt) throws SQLException {
		if (stmt != null) {
			stmt.close();
		}
	}

	public void closeQuietly(Connection connection) {
		try {
			close(connection);
		} catch (SQLException e) {
		}
	}

	public void closeQuietly(ResultSet rs) {
		try {
			close(rs);
		} catch (SQLException e) {
		}
	}

	public void closeQuietly(Statement stmt) {
		try {
			close(stmt);
		} catch (SQLException e) {
		}
	}

	public void commit(Connection connection) throws SQLException {
		if (connection != null) {
			connection.commit();
		}
	}

	public void commitQuietly(Connection connection) {
		try {
			commit(connection);
		} catch (SQLException e) {
		}
	}

	public static void setPath(Connection connection, String catalog, String schema) throws SQLException {
		if (StringUtils.isNotBlank(catalog)) {
			if (!StringUtils.equals(connection.getCatalog(), catalog)) {
				connection.setCatalog(catalog);
			}
		}
		if (StringUtils.isNotBlank(schema)) {
			if (!StringUtils.equals(connection.getSchema(), schema)) {
				connection.setSchema(schema);
			}
		}
	}

	/**
	 * @Description: MapCursor
	 * @Author: Fred Feng
	 * @Date: 24/03/2023
	 * @Version 1.0.0
	 */
	private static class MapCursor implements Cursor<Map<String, Object>> {

		private final ResultSet rs;
		private final DisposableObservable ob;
		private final boolean caseSensitive;
		private final AtomicBoolean opened;

		MapCursor(ResultSet rs, DisposableObservable ob, boolean caseSensitive) {
			this.rs = rs;
			this.ob = ob;
			this.caseSensitive = caseSensitive;
			this.opened = new AtomicBoolean(true);
		}

		public boolean isOpened() {
			return opened.get();
		}

		@Override
		public boolean hasNext() {
			try {
				opened.set(rs.next());
				return opened.get();
			} catch (SQLException e) {
				opened.set(false);
				throw new IllegalStateException(e.getMessage(), e);
			} finally {
				if (!isOpened()) {
					ob.notifyObservers();
				}
			}
		}

		@Override
		public Map<String, Object> next() {
			try {
				return toMap(rs, caseSensitive);
			} catch (SQLException e) {
				opened.set(false);
				throw new IllegalStateException(e.getMessage(), e);
			} finally {
				if (!isOpened()) {
					ob.notifyObservers();
				}
			}
		}
	}

	/**
	 * @Description: ObjectCursor
	 * @Author: Fred Feng
	 * @Date: 24/03/2023
	 * @Version 1.0.0
	 */
	private static class ObjectCursor<T> implements Cursor<T> {

		private final ResultSet rs;
		private final DisposableObservable ob;
		private final Class<T> resultClass;
		private final AtomicBoolean opened;

		ObjectCursor(ResultSet rs, DisposableObservable ob, Class<T> resultClass) {
			this.rs = rs;
			this.ob = ob;
			this.resultClass = resultClass;
			this.opened = new AtomicBoolean(true);
		}

		public boolean isOpened() {
			return opened.get();
		}

		@Override
		public boolean hasNext() {
			try {
				opened.set(rs.next());
				return opened.get();
			} catch (SQLException e) {
				opened.set(false);
				throw new IllegalStateException(e.getMessage(), e);
			} finally {
				if (!isOpened()) {
					ob.notifyObservers();
				}
			}
		}

		@Override
		public T next() {
			try {
				return toObject(rs, resultClass);
			} catch (SQLException e) {
				opened.set(false);
				throw new IllegalStateException(e.getMessage(), e);
			} finally {
				if (!isOpened()) {
					ob.notifyObservers();
				}
			}
		}
	}

	/**
	 * @Description: DisposableObservable
	 * @Author: Fred Feng
	 * @Date: 24/03/2023
	 * @Version 1.0.0
	 */
	private static class DisposableObservable extends Observable {

		@Override
		public void notifyObservers(Object arg) {
			super.setChanged();
			super.notifyObservers(arg);
			clearChanged();
			deleteObservers();
		}
	}
}