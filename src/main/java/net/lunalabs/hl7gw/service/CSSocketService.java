package net.lunalabs.hl7gw.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import net.lunalabs.hl7gw.utills.Common;

@EnableAsync
@RequiredArgsConstructor
@Service
public class CSSocketService {

	
	private static final Logger logger = LoggerFactory.getLogger(CSSocketService.class);

	
	public SocketChannel socketChannel2 = null; //일단은 public으로
	private boolean bLoop = true;

	
	@Async
	public CompletableFuture<SocketChannel> csSocketStart() throws IOException {
				
		// HL7 Test Panel에 보낼 프로토콜
		socketChannel2 = SocketChannel.open();
		
		logger.debug("central로 보내는 socket channel");

		try {
			socketChannel2.connect(new InetSocketAddress("localhost", 5051));
			logger.debug("socketChannel connected to port 5051");
			socketChannel2.configureBlocking(true);// Non-Blocking I/O

		} catch (Exception e2) {
			logger.debug("connected refused!!!");
			// e2.printStackTrace();
			//socketChannel2.close(); //이걸 닫으면 안되는데..
		}
		
		return CompletableFuture.completedFuture(socketChannel2); //다른 대안 탐색중..
		
	}
	
	
	
	
	@Async
	public void hl7ProtocolSendThread(String HL7Data, SocketChannel socketChannel) {
		
		//소켓을 계속 열었다 닫았다 할까/
		
		ByteBuffer writeBuf = ByteBuffer.allocate(1024);
		ByteBuffer readBuf = ByteBuffer.allocate(10240);

		logger.debug("다른 스레드라서 동기화가 안 되나? : "  + HL7Data);
        Charset charset = Charset.forName("UTF-8");

        
        
		boolean bConnect = true;
		while (bConnect) {

	        int byteCount = 0;
			byte[] readByteArr;
	        
			String hl7Response = "";
			
			try {
				// SocketChannel open
				logger.debug("HL7 protocol 전송");

				//writeBuf.clear();
				writeBuf.put(HL7Data.getBytes("UTF-8"));
				writeBuf.flip();
				while (writeBuf.hasRemaining()) {
					
					logger.debug("SocketChannel open-3");
					socketChannel.write(writeBuf);
						

				}
				
				
//				String data = charset.decode((readBuf)).toString();
//				System.out.println("Received Data : " + data);
				
				int bytesRead = socketChannel.read(readBuf); //read into buffer. 일단은 버퍼 초과 신경쓰지 않고
				while (bytesRead != -1) {//만약 소켓채널을 통해 buffer에 데이터를 받아왔으면

					readBuf.flip();  //make buffer ready for read
					//10240로 정의한 buffer의 크기를 실제 데이터의 크기로 flip() 함

				  while(readBuf.hasRemaining()){
				     //System.out.print((char) readBuf.get()); // read 1 byte at a time
				      			     
				     hl7Response = hl7Response + String.valueOf(((char) readBuf.get()));
				  }
				  
				  
				  //logger.debug("읽기 끝 " + bytesRead);
				  //logger.debug("hl7Response data1: "+hl7Response);
				  //readBuf.clear(); //make buffer ready for writing
				  bytesRead = socketChannel.read(readBuf);
				  
				  
				  if(!readBuf.hasRemaining()) {
					  
					  logger.debug("응답 안 함??");
					  break;
				  }
				  
				}									
//				BufferedReader reader = //읽기만 하는용도
//						new BufferedReader(new InputStreamReader(socketChannel.) )

				
//				byte[] bytes = new byte[readBuf.position()];
//				readBuf.flip();        
//				readBuf.get(bytes);
//				String s = new String(bytes);
//				System.out.println(s);
				

				
				//hl7Response = readBuffer(readBuf);

				logger.debug("-------------- 응답 환자측정 hl7Response ----------------");
				logger.debug(hl7Response);
				
				
				
//				byteCount = socketChannel.read(readBuf);
				
//				while (byteCount > 0) {
//
//					readBuf.flip(); // 입력된 데이터를 읽기 위해 read-mode로 바꿈, positon이 데이터의 시작인 0으로 이동
//
//					readByteArr = new byte[readBuf.remaining()]; // 현재 위치에서 limit까지 읽어드릴 수 있는 데이터의 개수를 리턴
//
//					// 일단 확인
//					 logger.debug("limit까지의 값: " + readBuf.remaining());
//					 logger.debug("capacity: " + readBuf.capacity());
//					 logger.debug("position: " + readBuf.position());
//
//					readBuf.get(readByteArr); // 데이터 읽기
//
//					hl7Response = hl7Response + new String(readByteArr, Charset.forName("UTF-8"));
//					logger.debug("응답 결과1: " + hl7Response);
//
//					
//				}
//				
				
				//logger.debug("응답 결과2: " + hl7Response);
				

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
	
	
	
	
	public String readBuffer(ByteBuffer readBuf) {
		if (readBuf.hasArray()) {
		    return new String(readBuf.array(),
		    		readBuf.arrayOffset() + readBuf.position(),
		    		readBuf.remaining());
		} else {
		    final byte[] b = new byte[readBuf.remaining()];
		    readBuf.duplicate().get(b);
		    return new String(b);
		}
		
	}
	

	
	
}
