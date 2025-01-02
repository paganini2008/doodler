package com.github.doodler.common.timeseries;

import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * 
 * @Description: TimeSeriesConstants
 * @Author: Fred Feng
 * @Date: 17/11/2024
 * @Version 1.0.0
 */
public interface TimeSeriesConstants {

    TimeZone DEFAULT_TIMEZONE = TimeZone.getDefault();

    DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

}
