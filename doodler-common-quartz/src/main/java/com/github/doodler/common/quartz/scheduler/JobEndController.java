package com.github.doodler.common.quartz.scheduler;

import com.github.doodler.common.ApiResult;
import com.github.doodler.common.quartz.executor.RpcJobBean;
import com.github.doodler.common.utils.MapUtils;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.Setter;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: JobEndController
 * @Author: Fred Feng
 * @Date: 24/08/2023
 * @Version 1.0.0
 */
@RequestMapping("/job")
@RestController
public class JobEndController implements SmartInitializingSingleton, JobSchedulingListenerAware {

    @Autowired()
    private JobLogService jobLogService;

    @Autowired
    private ApplicationContext applicationContext;

    @Setter
    private List<JobSchedulingListener> jobSchedulingListeners = new CopyOnWriteArrayList<>();

    @PostMapping("/end")
    public ApiResult<String> endJob(@RequestBody RpcJobBean rpcJobBean) {
        for (JobSchedulingListener listener : jobSchedulingListeners) {
            listener.afterScheduling(rpcJobBean.getStartTime(), rpcJobBean.getJobSignature(), rpcJobBean.getErrors());
        }
        jobLogService.endJob(rpcJobBean.getGuid(), rpcJobBean.getJobSignature(), rpcJobBean.getJobExecutor(),
                rpcJobBean.getResponseBody(), rpcJobBean.getErrors(), false);
        return ApiResult.ok();
    }

    @Override
    public void afterSingletonsInstantiated() {
        Map<String, JobSchedulingListener> beanMap = applicationContext.getBeansOfType(JobSchedulingListener.class);
        if (MapUtils.isNotEmpty(beanMap)) {
            List<JobSchedulingListener> listeners = new CopyOnWriteArrayList<>(beanMap.values());
            setJobSchedulingListeners(listeners);
        }
    }
}