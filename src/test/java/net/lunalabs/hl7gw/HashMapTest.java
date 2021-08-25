package net.lunalabs.hl7gw;

import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.extern.slf4j.Slf4j;
import net.lunalabs.hl7gw.config.ConcurrentConfig;

//@SpringBootTest
//@ExtendWith(SpringExtension.class)
@Slf4j
public class HashMapTest {


	
	@Test
	public void test2() {
		
		ConcurrentHashMap<String, String> concurrentHashMap = new ConcurrentHashMap<>();
		
		concurrentHashMap.put("key", "value");
		
		String bb = concurrentHashMap.get("key");
		
		log.debug("bb: " + bb);
		
		
		String aa = concurrentHashMap.get("aaa");
		
		log.debug("aa: " + aa);
		
		
	}
	
	
	@Test
	public void test() {
				
//	  SocketChannel channel	= ConcurrentConfig.globalQtsocketMap.get("mySchn");
//		
//		
//	  log.info(channel.toString());
//	  
//		
//		if (channel.isConnected()) {
//			log.debug("qtSocket channel이 정상적으로 연결되었습니다.");
//
//
//		} else if (!channel.isConnected()) {
//			log.debug("qtSocket channel이 연결이 끊어졌습니다.");
//		}

		
	}
	
	
}
