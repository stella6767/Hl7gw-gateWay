package net.lunalabs.hl7gw.emul;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

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
	
	
	
	public static void socketSends(String jsonData) {
		ByteBuffer writeBuf = ByteBuffer.allocate(1024);
		SocketChannel socketChannel = null; //이거는 나중에 생성 따로 분리

		boolean bConnect = true;
		while (bConnect) {

			try {
				// SocketChannel open
				System.out.println("SocketChannel open-1");
				socketChannel = SocketChannel.open();
				socketChannel.connect(new InetSocketAddress("localhost", 5051));
				System.out.println("SocketChannel open-2");
				socketChannel.configureBlocking(true);// Non-Blocking I/O

				writeBuf.clear();
				writeBuf.put(jsonData.getBytes("UTF-8"));
				writeBuf.flip();
				while (writeBuf.hasRemaining()) {
					
					System.out.println("SocketChannel open-3");
					socketChannel.write(writeBuf);
				}

				bConnect = false;
				//socketChannel.close(); //AsynchronousCloseException 이 발생하지 않기 위해서 
				System.out.println("[## ##][#3 Socket Connect");

			} catch (IOException e) {
				// e.printStackTrace();
				System.out.println("[####][#4 Not Connected!!! IO Exception Occured");
			}
		} // while
		System.out.println("[## ##][#5 Socket Connect");
		

	}
	
	
	
}
	
	

