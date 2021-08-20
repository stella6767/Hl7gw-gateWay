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
	
	
	public static void socketSends(String jsonData, SocketChannel socketChannel) {
		ByteBuffer writeBuf = ByteBuffer.allocate(1024);

		boolean bConnect = true;
		while (bConnect) {

			try {
				// SocketChannel open
				System.out.println("HL7 protocol 전송");

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
	
	

