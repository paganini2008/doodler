package com.github.doodler.common.jdbc.impexp;

/**
 * @Description: TiedMetaData
 * @Author: Fred Feng
 * @Date: 17/05/2023
 * @Version 1.0.0
 */
public interface TiedMetaData extends MetaData {

    String getCatalogName();

    default String getSchemaName() {
        return null;
    }

    default String getTableName() {
        return null;
    }

    <T extends TiedMetaData> T unwrap(Class<T> clz);
}