
package net.lunalabs.hl7gw.service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import net.lunalabs.hl7gw.config.ConcurrentConfig;
import net.lunalabs.hl7gw.dto.CMRespDto;
import net.lunalabs.hl7gw.dto.resp.PR100RespDto;
import net.lunalabs.hl7gw.utills.Common;

@RequiredArgsConstructor
@Service
public class CSSocketService {

	private static final Logger logger = LoggerFactory.getLogger(CSSocketService.class);
	private final Common common;
	private final ConcurrentConfig concurrentConfig;

	public SocketChannel socketChannel2 = null; // 일단은 public으로
	ObjectMapper mapper = new ObjectMapper();
	Random random = new Random();

//globalVar.globalSocket.put("schn", schn);
	@Async
	public void csSocketStart() throws IOException {
		// HL7 Test Panel에 보낼 프로토콜

		boolean bLoop = true;

		while (bLoop) {

			socketChannel2 = SocketChannel.open();

			logger.debug("central로 보내는 socket channel: " + common.ip + " , " + common.csPort);

			try {
				socketChannel2.connect(new InetSocketAddress(common.ip, common.csPort));

				logger.debug("socketChannel connected to port " + common.csPort);
				socketChannel2.configureBlocking(false);// Non-Blocking I/O
				readSocketData2(socketChannel2);


			} catch (Exception e2) {
				logger.debug("connected refused!!!");
				e2.printStackTrace();
			}

		}

		// return socketChannel2; // 다른 대안 탐색중..
	}



	public void readSocketData2(SocketChannel schn) throws IOException {

		concurrentConfig.globalSocketMap.put("cs", schn);

		logger.debug("CS-socket 담김: " + schn);

		boolean isRunning = true; // 일단 추가, socketWork 중지할지 안 중지할지

		while (isRunning && schn.isConnected()) {
			
			ByteBuffer readBuf = ByteBuffer.allocate(300); // 버퍼 메모리 공간확보
			int bytesRead = schn.read(readBuf);
			
			String result = "";
			
			while (bytesRead != -1) {// 만약 소켓채널을 통해 buffer에 데이터를 받아왔으면

                readBuf.flip(); // make buffer ready for read
                // 10240로 정의한 buffer의 크기를 실제 데이터의 크기로 flip() 함

                while (readBuf.hasRemaining()) {
                   //System.out.print((char) readBuf.get()); // read 1 byte at a time
                	//logger.debug("readBuf.hasRemaining1():  " + readBuf.hasRemaining() );
                   result = result + String.valueOf(((char) readBuf.get()));
                   //logger.debug("result1: " + result);
                }
                //logger.debug("가"+result);
                readBuf.clear(); //make buffer ready for writing
                //readBuf.compact();
                bytesRead = schn.read(readBuf);
                //logger.debug("byteRead size: " + bytesRead);
                //logger.debug("resultLength: " + result.length());
                
                if(bytesRead == 0 && result.length() > 0 ) {
                	
                	logger.debug("totalResult: " + result);
                	parsingHl7toJson(result);
                	
                	result="";
                	break;
                }
                
            	//logger.debug("readBuf.hasRemaining2():  " + readBuf.hasRemaining() );
                //이걸 어떻게 처리해야 되지.
             }

		}// 연결돼있다면 무한루프
		
		
		logger.debug("소켓 닫기");
		schn.close(); // 소켓 닫기
	}
	
	
	
	//@Scheduled(initialDelay = 5000, fixedRate = 500)
	public void 전송테스트() {
		String data = "MSH|^~\\&|BILABGW|NULL|RECEIVER|RECEIVER_FACILITY |2021-09-02 14:27:06|CPM0000|ORU^R01|767c2024-23ff-43a8-a165-e47bb1e3a2fe|P|2.8\r\n"
				+ "PID||1|Patient_NHS_ID|NULL|patient1|NULL|NULL||||||||||||\r\n"
				+ "OBR||10_kangmin|NULL|NULL|||"+ Common.getNowTime(1) +"|" +Common.getNowTime(2)+"|||||||||||||||||\r\n"
				+ "OBX|1|NM|mv||123|L/min||||||||2021-09-02 14:27:06|\r\n"
				+ "OBX|2|NM|rr||17|bpm||||||||2021-09-02 14:27:06|\r\n"
				+ "OBX|3|NA|rvs||-5.30485E+6^-2.40058E+7|mL||||||||2021-09-02 14:27:06|\r\n"
				+ "OBX|4|NM|spo2||123|%||||||||2021-09-02 14:27:06|\r\n"
				+ "OBX|5|NM|tv||198|mL||||||||2021-09-02 14:27:06|\r\n"
				+ "";
		
		try {
			writeSocket(data);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	@Scheduled(initialDelay = 5000, fixedRate = 100)
	public void 전송테스트2() {
		String data = "MSH|^~\\&|BILABGW|NULL|RECEIVER|RECEIVER_FACILITY |2021-09-02 14:27:06|CPM0001|ORU^R01|767c2024-23ff-43a8-a165-e47bb1e3a2fe|P|2.8\r\n"
				+ "PID||2|Patient_NHS_ID|NULL|patient2|NULL|NULL||||||||||||\r\n"
				+ "OBR||10_kangmin|NULL|NULL|||"+ Common.getNowTime(1) +"|" +Common.getNowTime(2)+"|||||||||||||||||\r\n"
				+ "OBX|1|NM|mv||123|L/min||||||||2021-09-02 14:27:06|\r\n"
				+ "OBX|2|NM|rr||17|bpm||||||||2021-09-02 14:27:06|\r\n"
				+ "OBX|3|NA|rvs||"+(random.nextInt(100)+1) + "^" +(random.nextInt(100)+1)+"|mL||||||||2021-09-02 14:27:06|\r\n"
				+ "OBX|4|NM|spo2||123|%||||||||2021-09-02 14:27:06|\r\n"
				+ "OBX|5|NM|tv||198|mL||||||||2021-09-02 14:27:06|\r\n"
				+ "";
		
		try {
			writeSocket(data);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	

	public void writeSocket(String Hl7parsingData) throws JsonProcessingException {

		logger.debug("CS에게 HL7 protocol 전송: " + Hl7parsingData);
		ByteBuffer writBuf = ByteBuffer.allocate(10240);
		
		SocketChannel schn = concurrentConfig.globalSocketMap.get("cs");
		
		 writBuf.flip();		
		 writBuf = Common.str_to_bb(Hl7parsingData);

		if (schn.isConnected()) {
			logger.debug("cssocket channel이 정상적으로 연결되었습니다.");
			while (writBuf.hasRemaining()) {
				logger.debug("SocketChannel open-3");
			try {
					schn.write(writBuf);
				} catch (IOException e) {
					logger.debug("close exception?  " + e.getMessage());
					e.printStackTrace();
				}
			}

		} else if (!schn.isConnected()) {
			logger.debug("cssocket channel이 연결이 끊어졌습니다.");
		}

		
		logger.debug("writeBuffer 초기화");
		//writBuf.rewind();
		
		writBuf.clear();
		
		
	}

	public void parsingHl7toJson(String HL7Data) throws IOException {

		logger.debug("응답 response => json 파싱준비");

//		boolean a = concurrentConfig.globalQtsocketMap == null ? true : false;			
//		logger.debug("확인: " + a);

		logger.debug("csServer로부터 응답받은 데이터: " + HL7Data);

		SocketChannel channel = concurrentConfig.globalSocketMap.get("qt");

		String[] splitEnterArray = HL7Data.split("[\\r\\n]+"); // 개행문자 기준으로 1차 파싱
		String[] mshArray = splitEnterArray[0].split("[|]");
		String trId = mshArray[9];
		logger.debug("trid: " + trId);

		if (splitEnterArray.length == 1) { /// MS100
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

		} else { //PR100

			List<PR100RespDto> dtos = new ArrayList<>();

			for (int i = 1; i < splitEnterArray.length; i++) { // 1부터 OBX param 시작

				PR100RespDto dto = null;

				String[] splitSecondArray = splitEnterArray[i].split("[|]");

				for (int j = 0; j < splitSecondArray.length; j++) {
					logger.debug("| 기준으로 2차 파싱: " + splitSecondArray[j]);

					// 일단 여기까지 빌드해서 올릴테니 라즈베리파이에서 다시 pull 받아서 빌드하시고 로그를 같이 볼 수 있을까요. -> 넵

					dto = PR100RespDto.builder().firstName(splitSecondArray[5]).lastName(splitSecondArray[6])
							.patientUserId(splitSecondArray[2]).age(Integer.parseInt(splitSecondArray[3]))
							.height(Double.parseDouble(splitSecondArray[4]))
							.weight(Double.parseDouble(splitSecondArray[7]))
							.gender(Integer.parseInt(splitSecondArray[8])).comment(splitSecondArray[9])
							.lastSession(splitSecondArray[10]).pid(Integer.parseInt(splitSecondArray[11])).build();

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
