package com.scuop.routeservice.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.scuop.routeservice.util.ThreadLocalUtil;

import cn.dev33.satoken.stp.StpUtil;

@Configuration
public class TaskThreadPoolConfig {
    public class ContextDecorator implements TaskDecorator {

        @Bean("taskExecutor")
        public Executor taskExecutor() {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(10);
            executor.setMaxPoolSize(20);
            executor.setQueueCapacity(200);
            executor.setKeepAliveSeconds(60);
            executor.setThreadNamePrefix("taskExecutor-");
            executor.setWaitForTasksToCompleteOnShutdown(true);
            executor.setAwaitTerminationSeconds(60);
            // 增加 TaskDecorator 属性的配置
            executor.setTaskDecorator(new ContextDecorator());
            executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
            executor.initialize();
            return executor;
        }

        @Override
        public Runnable decorate(Runnable runnable) {
            RequestAttributes context = RequestContextHolder.currentRequestAttributes();
            String tokenName = StpUtil.getTokenName();
            String token = ((ServletRequestAttributes) context).getRequest().getHeader("token");
            String cookie = ((ServletRequestAttributes) context).getRequest().getHeader("Cookie");
            return () -> {
                try {
                    ThreadLocalUtil.set(tokenName, token);
                    ThreadLocalUtil.set("Cookie", cookie);
                    runnable.run();
                } finally {
                    RequestContextHolder.resetRequestAttributes();
                }
            };
        }
    }
}
