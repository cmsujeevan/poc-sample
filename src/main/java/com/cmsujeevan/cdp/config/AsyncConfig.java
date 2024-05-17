package com.cmsujeevan.cdp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {


    @Value("${job.processor.thread.core-pool.size}")
    private int corePoolSize;

    @Value("${job.processor.thread.keep-live.seconds}")
    private int keepAliveSeconds;

    @Value("${job.processor.thread.max-pool.size}")
    private int maxPoolSize;

    @Value("${job.processor.thread.queue.capacity}")
    private int queueCapacity;

    /**
     * this is not the spring default pool, dedicated to batch job
     *
     * @return pooled executor to execute consumer tasks
     */
    @Bean("JobProcessorThread")
    public Executor getPooledTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setRejectedExecutionHandler(
                new ThreadPoolExecutor.DiscardOldestPolicy());
        executor.initialize();
        return executor;
    }

}
