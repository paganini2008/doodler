package com.github.doodler.common.timeseries;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * 
 * @Description: OverflowDataManager
 * @Author: Fred Feng
 * @Date: 02/01/2025
 * @Version 1.0.0
 */
public interface OverflowDataManager {

    Map<Instant, Object> retrieve(String category, String dimension, int N);

    void clean(String category);

    void clean(String category, String dimension);

    List<String> getDays(String category, String dimension);

}
