package com.github.doodler.common.timeseries;

import java.time.Instant;
import java.util.Map;

/**
 * 
 * @Description: OverflowDataManager
 * @Author: Fred Feng
 * @Date: 02/01/2025
 * @Version 1.0.0
 */
public interface OverflowDataManager {

    Map<Instant, Object> retrive(String category, String dimension);

    void clean(String category);

    void clean(String category, String dimension);

}
