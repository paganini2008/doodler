package com.github.doodler.common.mybatis.utils;

import com.github.doodler.common.page.EachPage;

/**
 * @Description: ScanHandler
 * @Author: Fred Feng
 * @Date: 22/03/2023
 * @Version 1.0.0
 */
@FunctionalInterface
public interface ScanHandler<T> {

	void onEachPage(EachPage<T> page);
}