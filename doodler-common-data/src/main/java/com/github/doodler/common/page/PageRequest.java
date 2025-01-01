package com.github.doodler.common.page;

/**
 * 
 * @Description: PageRequest in Generic paging tools
 * @Author: Fred Feng
 * @Date: 08/03/2023
 * @Version 1.0.0
 */
public interface PageRequest {
	
	int getPageNumber();

	int getPageSize();

	int getOffset();

	PageRequest next();

	PageRequest previous();

	PageRequest first();

	PageRequest set(int page);

	static PageRequest of(int size) {
		return of(1, size);
	}

	static PageRequest of(int page, int size) {
		return new SimplePageRequest(page, size);
	}
}
