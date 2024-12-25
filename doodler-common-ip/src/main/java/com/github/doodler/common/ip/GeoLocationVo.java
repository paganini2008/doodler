package com.github.doodler.common.ip;

import java.math.BigDecimal;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 
 * @Description: GeoLocationVo
 * @Author: Fred Feng
 * @Date: 24/12/2024
 * @Version 1.0.0
 */
@ApiModel(description = "Geo Location Info")
@Data
public class GeoLocationVo {

    public static final GeoLocationVo EMPTY = new GeoLocationVo();

    private String status;
    private String country;
    private String countryCode;
    private String region;
    private String regionName;
    private String city;
    private String zip;
    private BigDecimal lat;
    private BigDecimal lon;
    private String timezone;
    private String isp;
    private String org;
    private String as;
    private String query;

}
