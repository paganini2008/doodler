package io.doodler.common.quartz.scheduler;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.doodler.common.ApiResult;
import io.doodler.common.quartz.executor.RpcJobBean;

/**
 * @Description: JobEndController
 * @Author: Fred Feng
 * @Date: 24/08/2023
 * @Version 1.0.0
 */
@RequestMapping("/job")
@RestController
public class JobEndController {

    @Autowired()
    private JobLogService jobLogService;
    
    @Autowired
    private Counters counters;

    @PostMapping("/end")
    public ApiResult<String> endJob(@RequestBody RpcJobBean rpcJobBean) {
    	Counter counter = counters.getCounter(rpcJobBean.getJobSignature().getJobGroup());
    	counter.decrementRunningCount();
    	counter.incrementCount();
    	if(ArrayUtils.isNotEmpty(rpcJobBean.getErrors())) {
    		counter.incrementErrorCount();
    	}
        jobLogService.endJob(rpcJobBean.getGuid(), rpcJobBean.getJobSignature(), rpcJobBean.getJobExecutor(), rpcJobBean.getResponseBody(), rpcJobBean.getErrors(), false);
        return ApiResult.ok();
    }
}