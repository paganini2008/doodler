package com.github.doodler.common.timeseries;

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import com.github.doodler.common.utils.TimeWindowUnit;

public class TestMain {

    static {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    public static void main(String[] args) {
        Date endDate = new Date();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        Map<String, Object> map = TimeWindowUnit.MINUTES.initializeMap(endDate, 5, 60, TimeZone.getTimeZone(
                "Asia/Shanghai"), dtf,
                l -> {
                    return new HashMap<>();
                });
        for (Map.Entry<String, Object> e : map.entrySet()) {
            System.out.println(e);
        }
    }

}
