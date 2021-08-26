package net.lunalabs.hl7gw.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;


@Configuration
@EnableAsync
public class SchedulingConfig implements SchedulingConfigurer  {  //implements SchedulingConfigurer 
	
	

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		// TODO Auto-generated method stub
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();

        threadPoolTaskScheduler.setPoolSize(10);
        threadPoolTaskScheduler.setThreadNamePrefix("my-scheduled-task-pool-");
        threadPoolTaskScheduler.initialize();

        taskRegistrar.setTaskScheduler(threadPoolTaskScheduler);       
	}
	
	
	

}
