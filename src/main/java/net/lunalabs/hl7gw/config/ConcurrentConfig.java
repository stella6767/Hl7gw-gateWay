package net.lunalabs.hl7gw.config;

import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConcurrentConfig {

	public ConcurrentHashMap<String, SocketChannel> globalQtsocketMap = new ConcurrentHashMap<>(); //이 필드도 객체이므로 new로 인스턴스를 띄워줘야 된다.
	
}
