package com.github.doodler.common.jdbc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @Description: Cursor
 * @Author: Fred Feng
 * @Date: 24/03/2023
 * @Version 1.0.0
 */
public interface Cursor<T> extends Iterator<T> {

    default List<T> list() {
        List<T> dataList = new ArrayList<T>();
        T t;
        while (hasNext()) {
            t = next();
            if (t != null) {
                dataList.add(t);
            }
        }
        return dataList;
    }

    boolean isOpened();
}