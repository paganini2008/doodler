package com.github.doodler.common.jdbc.impexp;

import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import com.github.doodler.common.utils.CaseInsensitiveMap;

/**
 * @Description: DbType
 * @Author: Fred Feng
 * @Date: 25/03/2023
 * @Version 1.0.0
 */
public enum DbType {

    MYSQL("com.mysql.cj.jdbc.Driver", true, false, false) {
        @Override
        public String getUrl(String hostname, int port, String catalogName) {
            if (StringUtils.isNotBlank(catalogName)) {
                catalogName = "/" + catalogName;
            } else {
                catalogName = "";
            }
            return String.format("jdbc:mysql://%s:%d%s?serverTimezone=UTC&useSSL=false", hostname, port, catalogName);
        }
    },

    POSTGRESQL("org.postgresql.Driver", false, true, true) {
        @Override
        public String getUrl(String hostname, int port, String catalogName) {
            Assert.hasText(catalogName,
                    "Catalog name must be required while using jdbc api to operate postgresql database.");
            return String.format(
                    "jdbc:postgresql://%s:%d/%s?characterEncoding=utf8&allowMultiQueries=true&useSSL=false&stringtype=unspecified",
                    hostname, port, catalogName);
        }
    };

    private DbType(String driverClassName, boolean canSetCatalog, boolean canSetSchema, boolean schemaSupported) {
        this.driverClassName = driverClassName;
        this.canSetCatalog = canSetCatalog;
        this.canSetSchema = canSetSchema;
        this.schemaSupported = schemaSupported;
    }

    private final String driverClassName;
    private final boolean canSetCatalog;
    private final boolean canSetSchema;
    private final boolean schemaSupported;

    public String getDriverClassName() {
        return driverClassName;
    }

    public boolean isCanSetCatalog() {
        return canSetCatalog;
    }

    public boolean isCanSetSchema() {
        return canSetSchema;
    }

    public boolean isSchemaSupported() {
        return schemaSupported;
    }

    public abstract String getUrl(String hostname, int port, String catalogName);

    private static Map<String, DbType> cache = new CaseInsensitiveMap<DbType>();

    static {
        for (DbType dbType : DbType.values()) {
            cache.put(dbType.name().toLowerCase(), dbType);
        }
    }

    public static DbType forName(String name) {
        return cache.get(name);
    }
}