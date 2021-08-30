package net.lunalabs.hl7gw.service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import net.lunalabs.hl7gw.config.ConcurrentConfig;
import net.lunalabs.hl7gw.dto.CMRespDto;
import net.lunalabs.hl7gw.dto.resp.PR100RespDto;
import net.lunalabs.hl7gw.utills.Common;

@EnableAsync
@RequiredArgsConstructor
@Service
public class CSSocketService {

	private static final Logger logger = LoggerFactory.getLogger(CSSocketService.class);

	public SocketChannel socketChannel2 = null; // 일단은 public으로
	private boolean bLoop = true;
	ObjectMapper mapper = new ObjectMapper();

	private final ConcurrentConfig concurrentConfig;

	@Async
	public CompletableFuture<SocketChannel> csSocketStart() throws IOException {

		logger.debug("다른 스레드에서 di check: " + concurrentConfig.toString());

		// HL7 Test Panel에 보낼 프로토콜
		socketChannel2 = SocketChannel.open();

		logger.debug("central로 보내는 socket channel");

		try {
			// socketChannel2.connect(new InetSocketAddress("172.16.81.180", 5051));
			socketChannel2.connect(new InetSocketAddress("localhost", 5051));
			logger.debug("socketChannel connected to port 5051");
			socketChannel2.configureBlocking(true);// Non-Blocking I/O

		} catch (Exception e2) {
			logger.debug("connected refused!!!");
			// e2.printStackTrace();
			// socketChannel2.close(); //이걸 닫으면 안되는데..
		}

		return CompletableFuture.completedFuture(socketChannel2); // 다른 대안 탐색중..

	}



	@Async
	public void hl7ProtocolSendThread(String HL7Data, SocketChannel socketChannel) {

		// 소켓을 계속 열었다 닫았다 할까/

		ByteBuffer writeBuf = ByteBuffer.allocate(1024);
		ByteBuffer readBuf = ByteBuffer.allocate(10240);

		logger.debug("다른 스레드라서 동기화가 안 되나? : " + HL7Data);
		Charset charset = Charset.forName("UTF-8");

		boolean bConnect = true;
		while (bConnect) {

			int byteCount = 0;
			byte[] readByteArr;

			String hl7Response = "";

			try {
				// SocketChannel open
				logger.debug("HL7 protocol 전송");

				// writeBuf.clear();
				writeBuf.put(HL7Data.getBytes("UTF-8"));
				writeBuf.flip();
				while (writeBuf.hasRemaining()) {

					logger.debug("SocketChannel open-3");
					socketChannel.write(writeBuf);
				}

				
				if(!HL7Data.contains("SS100")) {
					
					int bytesRead = socketChannel.read(readBuf); // read into buffer. 일단은 버퍼 초과 신경쓰지 않고
					while (bytesRead != -1) {// 만약 소켓채널을 통해 buffer에 데이터를 받아왔으면

						readBuf.flip(); // make buffer ready for read
						// 10240로 정의한 buffer의 크기를 실제 데이터의 크기로 flip() 함

						while (readBuf.hasRemaining()) {
							// System.out.print((char) readBuf.get()); // read 1 byte at a time

							hl7Response = hl7Response + String.valueOf(((char) readBuf.get()));
						}

						// logger.debug("읽기 끝 " + bytesRead);
						// logger.debug("hl7Response data1: "+hl7Response);
						// readBuf.clear(); //make buffer ready for writing
						bytesRead = socketChannel.read(readBuf);

						if (!readBuf.hasRemaining()) {

							logger.debug("응답 안 함??");
							break;
						}

					}

					logger.debug("-------------- 응답 hl7Response ----------------");
					logger.debug(hl7Response);

					parsingHl7toJson(hl7Response, writeBuf);
														
				}
				
				

				bConnect = false;
				// socketChannel.close(); //AsynchronousCloseException 이 발생하지 않기 위해서
				logger.debug("[## ##][#3 Socket Connect");

			} catch (IOException e) {
				// e.printStackTrace();
				logger.debug("[####][#4 Not Connected!!! IO Exception Occured");
			}
		} // while
		logger.debug("[## ##][#5 Socket send complete");

	}

	public void parsingHl7toJson(String HL7Data, ByteBuffer wriBuf) throws IOException {

		logger.debug("patient 측정 response => json 파싱준비");
		
		boolean a = concurrentConfig.globalQtsocketMap == null ? true : false;
		logger.debug("확인: " + a);

		SocketChannel channel = concurrentConfig.globalQtsocketMap.get("mySchn");

		String[] splitEnterArray = HL7Data.split("[\\r\\n]+"); // 개행문자 기준으로 1차 파싱
		String[] mshArray = splitEnterArray[0].split("[|]");
		String trId = mshArray[9];
		logger.debug("trid: " + trId);

		if (splitEnterArray.length == 1) {
			CMRespDto cmRespDto = new CMRespDto();

			cmRespDto.setResultCode("100");
			cmRespDto.setResultMsg("Success");
			cmRespDto.setTrId(trId);
			String jsonData = mapper.writeValueAsString(cmRespDto);

			try {
				Common.sendJsonToQT(jsonData, channel);
			} catch (IOException | InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {

			List<PR100RespDto> dtos = new ArrayList<>();

			for (int i = 1; i < splitEnterArray.length; i++) { // 1부터 OBX param 시작

				PR100RespDto dto = null;

				String[] splitSecondArray = splitEnterArray[i].split("[|]");

				for (int j = 0; j < splitSecondArray.length; j++) {
					// logger.debug("| 기준으로 2차 파싱: " + splitSecondArray[j]);

					dto = PR100RespDto.builder().firstName(splitSecondArray[5]).lastName(splitSecondArray[6])
							.patientId(Integer.parseInt(splitSecondArray[2])).age(Integer.parseInt(splitSecondArray[3]))
							.height(Double.parseDouble(splitSecondArray[4]))
							.weight(Double.parseDouble(splitSecondArray[7]))
							.gender(Integer.parseInt(splitSecondArray[8])).comment(splitSecondArray[9])
							.lastSession(splitSecondArray[10]).build();
				}

				dtos.add(dto);

			}

			logger.debug("QT에 응답할 환자 objectList: " + dtos);

			CMRespDto cmRespDto = new CMRespDto();

			cmRespDto.setPatientInfos(dtos);
			cmRespDto.setResultCode("100");
			cmRespDto.setResultMsg("Success");
			cmRespDto.setTrId(trId);
			String jsonData = mapper.writeValueAsString(cmRespDto);

			// SocketChannel channel = ConcurrentConfig.globalQtsocketMap.get("mySchn");

			// 서버가 가동될 때부터 서버가 종료되는 시점까지의 범위를 Application Scope라고 부릅니다.

			try {
				Common.sendJsonToQT(jsonData, channel);
			} catch (IOException | InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
