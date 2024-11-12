package com.github.doodler.common.utils;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import lombok.experimental.UtilityClass;

/**
 * @Description: Env
 * @Author: Fred Feng
 * @Date: 24/08/2023
 * @Version 1.0.0
 */
@UtilityClass
public class Env {

	public int getPid() {
		try {
			RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
			return Integer.parseInt(runtimeMXBean.getName().split("@")[0]);
		} catch (Exception e) {
			return 0;
		}
	}
}