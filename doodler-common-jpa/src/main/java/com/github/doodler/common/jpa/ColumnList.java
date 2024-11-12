package com.github.doodler.common.jpa;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 
 * @Description: ColumnList
 * @Author: Fred Feng
 * @Date: 07/10/2024
 * @Version 1.0.0
 */
public class ColumnList extends ArrayList<Column> {

    private static final long serialVersionUID = 1L;

    public ColumnList() {
    }

    public ColumnList(Column... columns) {
        addAll(Arrays.asList(columns));
    }

    public ColumnList addColumn(String attributeName) {
        add(Column.forName(attributeName));
        return this;
    }

    public ColumnList addColumns(String[] attributeNames) {
        if (attributeNames != null) {
            for (String attributeName : attributeNames) {
                add(Column.forName(attributeName));
            }
        }
        return this;
    }

    public ColumnList addColumn(String alias, String attributeName) {
        add(Column.forName(alias, attributeName));
        return this;
    }

    public ColumnList addColumns(String alias, String[] attributeNames) {
        if (attributeNames != null) {
            for (String attributeName : attributeNames) {
                add(Column.forName(alias, attributeName));
            }
        }
        return this;
    }

    public <X> ColumnList addColumns(SerializedFunction<X, ?>... functions) {
        if (functions != null) {
            for (SerializedFunction<X, ?> function : functions) {
                add(Column.forName(function, null));
            }
        }
        return this;
    }

    public ColumnList addColumn(String attributeName, Class<?> requiredType) {
        add(Column.forName(attributeName, requiredType));
        return this;
    }

    public ColumnList addColumn(String alias, String attributeName, Class<?> requiredType) {
        add(Column.forName(alias, attributeName, requiredType));
        return this;
    }

    public ColumnList addColumns(Field<?>... fields) {
        if (fields != null) {
            for (Field<?> field : fields) {
                addColumn(field, field.toString());
            }
        }
        return this;
    }

    public ColumnList addColumn(Field<?> field, String alias) {
        add(field.as(alias));
        return this;
    }

}
