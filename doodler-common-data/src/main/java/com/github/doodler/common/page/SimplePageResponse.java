package com.github.doodler.common.page;

import java.io.Serializable;
import java.util.Iterator;
import org.apache.commons.collections4.CollectionUtils;
import lombok.SneakyThrows;

/**
 * @Description: SimplePageResponse in Generic paging tools
 * @Author: Fred Feng
 * @Date: 08/03/2023
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
        this.totalPages =
                (int) ((totalRecords + pageRequest.getPageSize() - 1) / pageRequest.getPageSize());
        this.pageRequest = pageRequest;
        this.pageReader = pageReader;
    }

    private PageContent<T> pageContent;

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
            PageContent<T> content = current != null ? current.getContent() : null;
            if (content != null && CollectionUtils.isNotEmpty(content.getContent())) {
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
        return hasPreviousPage() ? new SimplePageResponse<T>(pageRequest.previous(), pageReader)
                : this;
    }

    @Override
    public PageContent<T> getContent() throws Exception {
        Object nextToken = this.pageContent != null ? this.pageContent.getNextToken() : null;
        PageContent<T> pageContent = pageReader.list(pageRequest.getPageNumber(),
                pageRequest.getOffset(), pageRequest.getPageSize(), nextToken);
        this.pageContent = pageContent;
        return pageContent;
    }

}
