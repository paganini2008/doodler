package com.github.doodler.common.jdbc.page;

import java.util.List;

/**
 * @Description: EachPage
 * @Author: Fred Feng
 * @Date: 08/01/2020
 * @Version 1.0.0
 */
public interface EachPage<T> {

	boolean isEmpty();

	boolean isLastPage();

	boolean isFirstPage();

	boolean hasNextPage();

	boolean hasPreviousPage();

	int getTotalPages();

	long getTotalRecords();

	int getOffset();

	int getPageSize();

	int getPageNumber();

	List<T> getContent();
}