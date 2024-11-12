package com.github.doodler.common.jdbc.page;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @Description: PageReader
 * @Author: Fred Feng
 * @Date: 08/10/2024
 * @Version 1.0.0
 */
public interface PageReader<T> extends Countable {

    default List<T> list(int offset, int limit) throws SQLException {
        return list(1, offset, limit);
    }

    default List<T> list(int pageNumber, int offset, int limit) throws SQLException {
        PageContent<T> pageContent = list(pageNumber, offset, limit, null);
        return pageContent != null ? pageContent.getContent() : Collections.emptyList();
    }

    PageContent<T> list(int pageNumber, int offset, int limit, Object nextToken) throws SQLException;

    default PageResponse<T> list(PageRequest pageRequest) {
        return new SimplePageResponse<T>(pageRequest, this);
    }
}