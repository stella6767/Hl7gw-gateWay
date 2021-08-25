package net.lunalabs.hl7gw;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ConfigTest {
	
	
	@Autowired
	DefaultListableBeanFactory df;
	
	
	@Test
	public void beansListCheck() {
		
		for(String name : df.getBeanDefinitionNames()) {
			//log.info("등록된 beans: " + df.getBean(name).getClass().getName());
			log.info("등록된 beans: " + name);
		}
		
	}
	

}
