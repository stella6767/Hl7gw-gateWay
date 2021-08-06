package net.lunalabs.hl7gw.emul;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

@EnableAsync
@Service("testServerReqThread")
public class TestServerReqThread {
	
	private static final Logger logger = LoggerFactory.getLogger(TestServerReqThread.class);

	
	public void reqThreadStart(String jsonData) {
				
		logger.debug("받은 jsonData: "  + jsonData);
		
		
		
		
		
	}
	
	
}
