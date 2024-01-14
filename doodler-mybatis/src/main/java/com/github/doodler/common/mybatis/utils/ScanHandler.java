package com.github.doodler.common.mybatis.utils;

import com.github.doodler.common.jdbc.page.EachPage;

/**
 * @Description: ScanHandler
 * @Author: Fred Feng
 * @Date: 22/01/2020
 * @Version 1.0.0
 */
@FunctionalInterface
public interface ScanHandler<T> {

	void onEachPage(EachPage<T> page);
}