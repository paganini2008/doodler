package io.doodler.jdbc.page;

import java.sql.SQLException;
import java.util.List;

/**
 * @Description: PageReader
 * @Author: Fred Feng
 * @Date: 08/03/2023
 * @Version 1.0.0
 */
public interface PageReader<T> extends Countable {

    List<T> list(int pageNumber, int maxResults) throws SQLException;

    default List<T> list(int maxResults)throws SQLException {
        return list(1, maxResults);
    }

    default PageResponse<T> list(PageRequest pageRequest) {
        return new SimplePageResponse<T>(pageRequest, this);
    }
}