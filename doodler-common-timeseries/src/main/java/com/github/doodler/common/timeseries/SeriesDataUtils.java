package com.github.doodler.common.timeseries;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Function;

import com.github.doodler.common.utils.DateUtils;
import com.github.doodler.common.utils.MapUtils;

import lombok.experimental.UtilityClass;

/**
 * 
 * @Description: SeriesDataUtils
 * @Author: Fred Feng
 * @Date: 16/11/2024
 * @Version 1.0.0
 */
@UtilityClass
public class SeriesDataUtils {

    public Map<String, Map<String, Object>> descendingMap(Date endTime,
                                                          int span,
                                                          int size,
                                                          TimeZone timeZone,
                                                          DateTimeFormatter df,
                                                          Function<Long, Map<String, Object>> f) {
        Map<String, Map<String, Object>> data = new LinkedHashMap<String, Map<String, Object>>();
        LocalDateTime ldt = DateUtils.toLocalDateTime(endTime, timeZone);
        ZoneOffset zos = OffsetDateTime.now(timeZone != null ? timeZone.toZoneId() : ZoneId.systemDefault()).getOffset();
        for (int i = 0; i < size; i++) {
            data.put(ldt.format(df), f.apply(ldt.toInstant(zos).toEpochMilli()));
            ldt = ldt.plusSeconds(-1 * span);
        }
        return MapUtils.reverse(data);
    }

}
