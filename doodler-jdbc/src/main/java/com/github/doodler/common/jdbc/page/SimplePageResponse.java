package com.github.doodler.common.jdbc.page;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import lombok.SneakyThrows;

/**
 * @Description: SimplePageResponse in Generic paging tools
 * @Author: Fred Feng
 * @Date: 08/01/2020
 * @Version 1.0.0
 */
public class SimplePageResponse<T> implements PageResponse<T>, Serializable {

    private static final long serialVersionUID = 3421565187066969539L;

    private final int pageNumber;
    private final int totalPages;
    private final long totalRecords;
    private final PageRequest pageRequest;
    private final PageReader<T> pageReader;

    @SneakyThrows
    SimplePageResponse(PageRequest pageRequest, PageReader<T> pageReader) {
        this.pageNumber = pageRequest.getPageNumber();
        this.totalRecords = pageReader.rowCount();
        this.totalPages = (int) ((totalRecords + pageRequest.getPageSize() - 1) / pageRequest.getPageSize());
        this.pageRequest = pageRequest;
        this.pageReader = pageReader;
    }

    public boolean isEmpty() {
        return totalRecords == 0;
    }

    public boolean isLastPage() {
        return pageNumber == getTotalPages();
    }

    public boolean isFirstPage() {
        return pageNumber == 1;
    }

    public boolean hasNextPage() {
        return pageNumber < getTotalPages();
    }

    public boolean hasPreviousPage() {
        return pageNumber > 1;
    }

    public Iterator<EachPage<T>> iterator() {
        return new PageIterator<T>(this);
    }

    static class PageIterator<T> implements Iterator<EachPage<T>> {

        private final PageResponse<T> pageResponse;
        private PageResponse<T> current;
        private EachPage<T> page;

        PageIterator(PageResponse<T> pageResponse) {
            this.pageResponse = pageResponse;
        }

        @SneakyThrows
        public boolean hasNext() {
            if (current == null) {
                current = pageResponse;
            } else if (current.hasNextPage()) {
                current = current.nextPage();
            } else {
                current = null;
            }
            List<T> content = current != null ? current.getContent() : null;
            if (CollectionUtils.isNotEmpty(content)) {
                page = new ReadonlyPage<>(content, current);
            } else {
                page = null;
            }
            return page != null;
        }

        public EachPage<T> next() {
            return page;
        }
    }

    public int getTotalPages() {
        return totalPages;
    }

    public long getTotalRecords() {
        return totalRecords;
    }

    public int getOffset() {
        return pageRequest.getOffset();
    }

    public int getPageSize() {
        return pageRequest.getPageSize();
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public PageResponse<T> setPage(int pageNumber) {
        return new SimplePageResponse<T>(pageRequest.set(pageNumber), pageReader);
    }

    public PageResponse<T> lastPage() {
        int lastPage = getTotalPages();
        return isLastPage() ? this : setPage(lastPage);
    }

    public PageResponse<T> firstPage() {
        return isFirstPage() ? this : new SimplePageResponse<T>(pageRequest.first(), pageReader);
    }

    public PageResponse<T> nextPage() {
        return hasNextPage() ? new SimplePageResponse<T>(pageRequest.next(), pageReader) : this;
    }

    public PageResponse<T> previousPage() {
        return hasPreviousPage() ? new SimplePageResponse<T>(pageRequest.previous(), pageReader) : this;
    }

    public List<T> getContent() throws SQLException {
        return pageReader.list(pageRequest.getPageNumber(), pageRequest.getPageSize());
    }
}