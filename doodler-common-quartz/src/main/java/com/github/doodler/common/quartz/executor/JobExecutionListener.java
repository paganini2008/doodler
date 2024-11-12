package com.github.doodler.common.quartz.executor;

import org.springframework.lang.Nullable;

/**
 * @Description: JobExecutionListener
 * @Author: Fred Feng
 * @Date: 24/08/2023
 * @Version 1.0.0
 */
public interface JobExecutionListener {

	void onStart(RpcJobBean rpcJobBean);

	void onEnd(RpcJobBean rpcJobBean, String returnValue, @Nullable Throwable error);
}