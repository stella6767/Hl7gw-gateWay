package net.lunalabs.hl7gw.config;

import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConcurrentConfig {

	public ConcurrentHashMap<String, SocketChannel> globalQtsocketMap = new ConcurrentHashMap<>();
	
	
//	@Bean 
//	public ConcurrentHashMap<String, SocketChannel> justReturnMap() {
//	
//		return globalQtsocketMap; 
//	
//	}

	
}
