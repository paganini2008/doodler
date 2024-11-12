package com.github.doodler.common.id;

import java.util.List;

/**
 * 
 * @Description: IdBatchGenerator
 * @Author: Fred Feng
 * @Date: 07/11/2024
 * @Version 1.0.0
 */
public interface IdBatchGenerator {

    List<Long> createManyLongs(int n);

    List<String> createManyStrings(int n);

}
