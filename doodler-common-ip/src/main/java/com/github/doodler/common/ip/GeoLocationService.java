package com.github.doodler.common.ip;

/**
 * 
 * @Description: GeoLocationService
 * @Author: Fred Feng
 * @Date: 24/12/2024
 * @Version 1.0.0
 */
public interface GeoLocationService {

    GeoLocationVo getGeoLocation(String ipAddress);

}
