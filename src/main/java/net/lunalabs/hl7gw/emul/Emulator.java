package net.lunalabs.hl7gw.emul;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.lunalabs.hl7gw.utills.Common;

/**
 * 
 * @FileName  : Emulator.java
 * @Project   : HL7GwEmul
 * @Date      : 2021. 06. 25. 
 * @작성자      : winix
 * @설명       : [HL7GW 설명]
 * 				1. Socket 을 Listen
 * 				2. 요청 수신 후 thread 분기 
 */
//@Controller


@RequiredArgsConstructor
@RestController
public class Emulator {

	
	private static final Logger logger = LoggerFactory.getLogger(Emulator.class);
	
	private final GwEmulThread gwEmulThread;

	
	public void start(){
		
		try {
			
			ServerSocketChannel serverSocketChannel = null;
			SocketChannel socketChannel = null;
			
			
			SocketChannel socketChannel2 = null;  //HL7 Test Panel에 보낼 프로토콜
			socketChannel2 = SocketChannel.open();
			
			
			serverSocketChannel = ServerSocketChannel.open();
			
			
			serverSocketChannel.socket().bind(new InetSocketAddress (5050));
			
						
			
			try {
				socketChannel2.connect(new InetSocketAddress("localhost", 5051));
				logger.debug("socketChannel connected to port 5051");
				socketChannel2.configureBlocking(true);// Non-Blocking I/O	
				
			} catch (Exception e2) {
				logger.debug("connected refused!!!");
				//e2.printStackTrace();
				socketChannel2.close();
			}
			
			
			
			
			boolean bLoop = true;
				
			
			while (bLoop) {
				try {
					socketChannel = serverSocketChannel.accept();
					socketChannel.configureBlocking(true);

					//System.out.println("[ESMLC Listen[" + "] Socket Accept EsmlcIfWorkThread Start");
					logger.info("[ESMLC Listen[" + "] 5050 Socket Accept EsmlcIfWorkThread Start");					
					gwEmulThread.socketWork(socketChannel, socketChannel2);					
					
				} catch (Exception e) {
					e.printStackTrace();
					try{
						Thread.sleep(5000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}

				try{
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(Exception e1) {
			e1.printStackTrace();
		}
		
	}
	
}
