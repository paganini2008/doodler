package com.github.doodler.common.utils;

import lombok.experimental.UtilityClass;

/**
 * 
 * @Description: IpUtils
 * @Author: Fred Feng
 * @Date: 24/12/2024
 * @Version 1.0.0
 */
@UtilityClass
public class IpUtils {

    public long ipToLong(String ipAddress) {
        String[] ip = ipAddress.split("\\.");
        return (Long.parseLong(ip[0]) << 24) + (Long.parseLong(ip[1]) << 16)
                + (Long.parseLong(ip[2]) << 8) + Long.parseLong(ip[3]);
    }

    public String longToIp(long value) {
        StringBuffer str = new StringBuffer("");
        str.append(String.valueOf((value >>> 24)));
        str.append(".");
        str.append(String.valueOf((value & 0x00FFFFFF) >>> 16));
        str.append(".");
        str.append(String.valueOf((value & 0x0000FFFF) >>> 8));
        str.append(".");
        str.append(String.valueOf((value & 0x000000FF)));
        return str.toString();
    }

    public static void main(String[] args) {
        System.out.println(ipToLong("127.0.0.1"));
    }

}
