package net.lunalabs.hl7gw.config;

import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@EnableScheduling
public class SchedulingConfig extends AsyncConfigurerSupport { // implements SchedulingConfigurer

	@Override
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);
		executor.setMaxPoolSize(20);
		executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("kang-async-");
		executor.initialize();
		return executor;
	}

//	@Bean
//	public TaskScheduler configureTasks() {
//		// TODO Auto-generated method stub
//        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
//
//        threadPoolTaskScheduler.setPoolSize(10);
//        threadPoolTaskScheduler.setThreadNamePrefix("my-scheduled-task-pool-");
//        threadPoolTaskScheduler.initialize();
//
//        return threadPoolTaskScheduler;
//	}


}
