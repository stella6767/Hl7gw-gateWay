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
	
	
	public static void socketSend(String jsonData) {
				
		SocketChannel socketChannel = null;

		ByteBuffer writeBuf = ByteBuffer.allocate(10240);

		boolean bConnect = true;
		
		
		while (bConnect) { //이걸 무한루프로 돌려버릴까

			try {
				// SocketChannel open
				System.out.println("SocketChannel open-1");
				socketChannel = SocketChannel.open();
				
				
				
				socketChannel.connect(new InetSocketAddress("localhost", 5051));
				
				System.out.println("SocketChannel open-2");
				socketChannel.configureBlocking(true);// Non-Blocking I/O

				
				writeBuf.clear();
				//writeBuf.put(jsonData.getBytes("UTF-8"));
				
				Charset charset = Charset.forName("UTF-8");
				writeBuf = charset.encode(jsonData);
				
				writeBuf.flip();
				while (writeBuf.hasRemaining()) {
					socketChannel.write(writeBuf); //send data
				}

				bConnect = false;

				socketChannel.close();

				System.out.println("[## ##][#3 Socket Connect");

			} catch (IOException e) {
				// e.printStackTrace();
				System.out.println("[####][#4 Not Connected!!! IO Exception Occured");
			}
		} // while
		System.out.println("[## ##][#5 Socket Connect");

	}
	
}
