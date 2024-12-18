package com.github.doodler.common.jpa;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 
 * @Description: FieldList
 * @Author: Fred Feng
 * @Date: 07/10/2024
 * @Version 1.0.0
 */
public class FieldList extends ArrayList<Field<?>> {

    private static final long serialVersionUID = 1L;

    public FieldList() {
    }

    public FieldList(Field<?>... fields) {
        addAll(Arrays.asList(fields));
    }

    public FieldList addField(String attributeName) {
        add(Property.forName(attributeName));
        return this;
    }

    public FieldList addFields(String[] attributeNames) {
        if (attributeNames != null) {
            for (String attributeName : attributeNames) {
                add(Property.forName(attributeName));
            }
        }
        return this;
    }

    public FieldList addField(String alias, String attributeName) {
        add(Property.forName(alias, attributeName));
        return this;
    }

    public FieldList addFields(String alias, String[] attributeNames) {
        if (attributeNames != null) {
            for (String attributeName : attributeNames) {
                add(Property.forName(alias, attributeName));
            }
        }
        return this;
    }

    public FieldList addField(String attributeName, Class<?> requiredType) {
        add(Property.forName(attributeName, requiredType));
        return this;
    }

    public FieldList addField(String alias, String attributeName, Class<?> requiredType) {
        add(Property.forName(alias, attributeName, requiredType));
        return this;
    }

}
