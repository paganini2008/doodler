package com.github.doodler.common.page;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * @Description: DefaultPageContent
 * @Author: Fred Feng
 * @Date: 08/10/2024
 * @Version 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DefaultPageContent<T> implements PageContent<T> {

    private List<T> content;
    private Object nextToken;

}
