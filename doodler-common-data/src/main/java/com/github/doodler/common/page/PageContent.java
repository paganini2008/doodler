package com.github.doodler.common.page;

import java.util.List;

/**
 * 
 * @Description: PageContent
 * @Author: Fred Feng
 * @Date: 08/10/2024
 * @Version 1.0.0
 */
public interface PageContent<T> {

    List<T> getContent();

    default Object getNextToken() {
        return null;
    }

}
