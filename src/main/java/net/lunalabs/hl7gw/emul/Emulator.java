package net.lunalabs.hl7gw.emul;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import net.lunalabs.hl7gw.service.FTPService;
import net.lunalabs.hl7gw.service.SocketService;

/**
 * 
 * @FileName  : Emulator.java
 * @Project   : HL7GwEmul
 * @Date      : 2021. 06. 25. 
 * @작성자      : winix
 * @설명       : [HL7GW 설명]
 * 				1. Socket Thread 시작
 */



@RequiredArgsConstructor
@Component
public class Emulator {

	
	private static final Logger logger = LoggerFactory.getLogger(Emulator.class);
	
	private final SocketService socketService;
	private final FTPService ftpService;
	
	
	public void start() throws Exception{
		
		logger.debug("Emulator Start!");
		
		socketService.socketThread();
		ftpService.FTPTest();
		
	}
	

	
}
