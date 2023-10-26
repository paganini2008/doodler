package io.doodler.webmvc;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Description: ThreadPoolProperties
 * @Author: Fred Feng
 * @Date: 25/01/2023
 * @Version 1.0.0
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "threadpool")
public class ThreadPoolProperties {

    private TaskExecutor executor = new TaskExecutor();
    private TaskScheduler scheduler = new TaskScheduler();

    @Getter
    @Setter
    public static class TaskExecutor {

        private int poolSize = 8;
        private int maxPoolSize = 200;
        private int queueCapacity = 1000;
        private String threadNameFormat = "crypto-webapp-threads-%d";
        private String threadGroupName = "crypto-webapp-threads-group";
    }

    @Getter
    @Setter
    public static class TaskScheduler {

        private int poolSize = 20;
        private String threadNameFormat = "crypto-webapp-tasks-%d";
        private String threadGroupName = "crypto-webapp-tasks-group";
    }
}