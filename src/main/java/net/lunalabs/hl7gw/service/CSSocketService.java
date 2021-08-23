package net.lunalabs.hl7gw.service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@EnableAsync
@RequiredArgsConstructor
@Service
public class CSSocketService {

	
	private static final Logger logger = LoggerFactory.getLogger(CSSocketService.class);

	
	public SocketChannel socketChannel2; //일단은 public으로
	private boolean bLoop = true;

	
	@Async
	public CompletableFuture<SocketChannel> csSocketStart() throws IOException {
				
		socketChannel2 = null; // HL7 Test Panel에 보낼 프로토콜
		socketChannel2 = SocketChannel.open();
		
		logger.debug("central로 보내는 socket channel");

		try {
			socketChannel2.connect(new InetSocketAddress("localhost", 5051));
			logger.debug("socketChannel connected to port 5051");
			socketChannel2.configureBlocking(true);// Non-Blocking I/O

		} catch (Exception e2) {
			logger.debug("connected refused!!!");
			// e2.printStackTrace();
			socketChannel2.close();
		}
		
		return CompletableFuture.completedFuture(socketChannel2);
		
	}
	
	
	
	
	@Async
	public void hl7ProtocolSendThread(String HL7Data, SocketChannel socketChannel) {
		
		
		ByteBuffer writeBuf = ByteBuffer.allocate(1024);

		logger.debug("다른 스레드라서 동기화가 안 되나? : "  + HL7Data);
		
		
		boolean bConnect = true;
		while (bConnect) {

			try {
				// SocketChannel open
				logger.debug("HL7 protocol 전송");

				writeBuf.clear();
				writeBuf.put(HL7Data.getBytes("UTF-8"));
				writeBuf.flip();
				while (writeBuf.hasRemaining()) {
					
					logger.debug("SocketChannel open-3");
					socketChannel.write(writeBuf);
				}

				bConnect = false;
				//socketChannel.close(); //AsynchronousCloseException 이 발생하지 않기 위해서 
				logger.debug("[## ##][#3 Socket Connect");

			} catch (IOException e) {
				// e.printStackTrace();
				logger.debug("[####][#4 Not Connected!!! IO Exception Occured");
			}
		} // while
		logger.debug("[## ##][#5 Socket send complete");
		
	}
	
	

	
	
}
