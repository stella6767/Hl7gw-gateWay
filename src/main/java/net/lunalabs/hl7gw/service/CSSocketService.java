
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

import org.apache.commons.lang3.StringUtils;
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

				//readSocketData(socketChannel2);

			} catch (Exception e2) {
				logger.debug("csSocket connected refused!!!");
				//e2.printStackTrace();
			}

		}

		// return socketChannel2; // 다른 대안 탐색중..
	}


	  
//	    public void readSocketData(SocketChannel schn) throws IOException {
//
//	        
//			concurrentConfig.globalSocketMap.put("cs", schn);
//
//			logger.debug("CS-socket 담김: " + schn);
//
//	        boolean isRunning = true; // 일단 추가, socketWork 중지할지 안 중지할지
//
//	        while (isRunning && schn.isConnected()) {
//
//	            try {
//	                long lThId = Thread.currentThread().getId();
//	                int byteCount = 0;
//	                byte[] readByteArr;
//
//	                // ByteBuffer readBuf = ByteBuffer.allocate(10); //버퍼 메모리 공간확보
//	                ByteBuffer readBuf = ByteBuffer.allocate(500);
//
//	                logger.debug("첫번째  while문");
//
//	                // 무한 루프
//	                String result = ""; // 요기서 초기화
//
//	                while (byteCount >= 0) {
//
//	                    try {
//
//	                        byteCount = schn.read(readBuf); // 소켓채널에서 한번에 초과되는 버퍼사이즈의 데이터가 들어오면..
//
//	                        //log.info("[gwEmulThread #100] TID[" + "] byteCount :  " + byteCount);
//	                        // logger.debug("isRunning why: " + isRunning);
//	                    } catch (Exception e) {
//	                        // e.printStackTrace();
//	                        logger.info("갑자기 클라이언트 소켓이 닫혔을 시");
//	                        schn.close();
//	                        isRunning = false;
//	                        break;
//	                    }
//	                    
//	                    
//	                    // 버퍼에 값이 있다면 계속 버퍼에서 값을 읽어 result 를 완성한다.
//	                    while (byteCount > 0) {
//
//
//	                        readBuf.flip(); // 입력된 데이터를 읽기 위해 read-mode로 바꿈, positon이 데이터의 시작인 0으로 이동
//	                        readByteArr = new byte[readBuf.remaining()]; // 현재 위치에서 limit까지 읽어드릴 수 있는 데이터의 개수를 리턴
//	                        readBuf.get(readByteArr); // 데이터 읽기
//
//	                        result = result + new String(readByteArr, Charset.forName("UTF-8"));
//
//	                        try {
//	                            byteCount = schn.read(readBuf);
//	                            //log.info("[gwEmulThread #210] TID[" + result + "] byteCount :  " + byteCount);
//	                        } catch (Exception e) {
//	                            e.printStackTrace();
//	                            // break;
//	                        }
//
//	                        boolean bEtxEnd = true; // 아래 while문을 실행할지 안할지
//
//	                        while (!result.equals("") && bEtxEnd) {
//	                            
//	                        	logger.info("#ETX#단위로 루프 돌기 전 result: " + result);
//	                        	
//								Integer countMSH = StringUtils.countMatches(result, "MSH");
//
//								int lastIndex = (result.lastIndexOf("|"));
//															
//								int indMSH = (result.lastIndexOf("MSH"));
//
//								logger.debug("indEtx: " + indMSH + " result.length:  " + result.length() + "  countMSH: " + countMSH);							
//								logger.debug("lastIndex: " + lastIndex);
//								
//								
//	                        	Thread.sleep(1000);
//								
//								
//	                        	//string1.split("(?=-)");
//	                        	
//	            				if ( (indMSH == 0 || indMSH== result.length()) && countMSH == 1) { 
//
//									
//									parsingHl7toJson(result);
//									
//
//									result = "";
//									bEtxEnd = false;
//									readBuf.clear();
//								} else if (result.length() != indMSH && countMSH > 1) { // case5
//
//									String[] resultArray = result.split("(?=MSH)");
//
//									logger.debug("case5 길이: " + resultArray.length);
//
//										for (int i = 0; i < resultArray.length - 1; i++) {
//											
//											logger.debug("정규표현식활용: " + resultArray[i]);
//											parsingHl7toJson(result);
//											
//										}
//
//										// 예를 들어 #ETX# #STX#{sdfsfdsdf data가 있을시 #STX#로 이어지는 데이터를 저장
//										result = resultArray[resultArray.length - 1];
//										// 다시 버퍼를 읽음
//										readBuf.clear();
//										break;
//						
//								} 
//	                        
//	                        }
//
//	                    } // #ETX# 단위로 루프
//	                } // byteCount > 0
//
//	                logger.debug("소켓 닫기");
//	                schn.close(); // 소켓 닫기
//
//	            } catch (Exception e) {
//	                e.printStackTrace();
//	                continue;
//	            }
//	        }
//	    }
	
	
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
                	MSHClsfy(result);
                	
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
	
	
	public void MSHClsfy(String data) {
		
		logger.debug("응답받은 버퍼: " + data);
		
		String[] resultArray = data.split("(?=MSH)");

		logger.debug("case5 길이: " + resultArray.length);

			for (int i = 0; i < resultArray.length; i++) {
				
				logger.debug("정규표현식활용: " + resultArray[i]);
				try {
					parsingHl7toJson(resultArray[i]);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
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
	
	
	
	//@Scheduled(initialDelay = 5000, fixedRate = 1000)
	//@Async
	public void 전송테스트() {
		String data = "MSH|^~\\&|BILABGW|NULL|RECEIVER|RECEIVER_FACILITY |2021-09-02 14:27:06|CPM0000|ORU^R01|767c2024-23ff-43a8-a165-e47bb1e3a2fe|P|2.8\r\n"
				+ "PID||1|Patient_NHS_ID|NULL|patient1|NULL|NULL||||||||||||\r\n"
				+ "OBR||patient100_20211021_171700|NULL|NULL|||"+ Common.getNowTime(1) +"|" +Common.getNowTime(2)+"|||||||||||||||||\r\n"
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
	
	
	//@Scheduled(initialDelay = 5000, fixedRate = 1000)
	//@Async
	public void 전송테스트2() {
		String data = "MSH|^~\\&|BILABGW|NULL|RECEIVER|RECEIVER_FACILITY |2021-09-02 14:27:06|CPM0001|ORU^R01|767c2024-23ff-43a8-a165-e47bb1e3a2fe|P|2.8\r\n"
				+ "PID||2|Patient_NHS_ID|NULL|patient2|NULL|NULL||||||||||||\r\n"
				+ "OBR||patient2_20211021_171700|NULL|NULL|||"+ Common.getNowTime(1) +"|" +Common.getNowTime(2)+"|||||||||||||||||\r\n"
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
	
	
	//@Scheduled(initialDelay = 5000, fixedRate = 100)
	//@Async
	public void 전송테스트3() {
		String data = "MSH|^~\\&|BILABGW|NULL|RECEIVER|RECEIVER_FACILITY |2021-09-02 14:27:06|CPM0002|ORU^R01|767c2024-23ff-43a8-a165-e47bb1e3a2fe|P|2.8\r\n"
				+ "PID||2|Patient_NHS_ID|NULL|patient2|NULL|NULL||||||||||||\r\n"
				+ "OBR||patient100_20211021_171700|NULL|NULL|||"+ Common.getNowTime(1) +"|" +Common.getNowTime(2)+"|||||||||||||||||\r\n"
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
	
	
	//@Scheduled(initialDelay = 5000, fixedRate = 100)
	//@Async
	public void 전송테스트4() {
		String data = "MSH|^~\\&|BILABGW|NULL|RECEIVER|RECEIVER_FACILITY |2021-09-02 14:27:06|CPM0003|ORU^R01|767c2024-23ff-43a8-a165-e47bb1e3a2fe|P|2.8\r\n"
				+ "PID||2|Patient_NHS_ID|NULL|patient2|NULL|NULL||||||||||||\r\n"
				+ "OBR||patient100_20211021_171700|NULL|NULL|||"+ Common.getNowTime(1) +"|" +Common.getNowTime(2)+"|||||||||||||||||\r\n"
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
	
	
	//@Scheduled(initialDelay = 5000, fixedRate = 500)
	@Async
	public void 전송테스트5() {
		String data = "MSH|^~\\&|BILABGW|NULL|RECEIVER|RECEIVER_FACILITY |2021-09-02 14:27:06|CPM0004|ORU^R01|767c2024-23ff-43a8-a165-e47bb1e3a2fe|P|2.8\r\n"
				+ "PID||2|Patient_NHS_ID|NULL|patient2|NULL|NULL||||||||||||\r\n"
				+ "OBR||patient100_20211021_171700|NULL|NULL|||"+ Common.getNowTime(1) +"|" +Common.getNowTime(2)+"|||||||||||||||||\r\n"
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
	
	
	
	//@Scheduled(initialDelay = 5000, fixedRate = 500)
	@Async
	public void 전송테스트6() {
		String data = "MSH|^~\\&|BILABGW|NULL|RECEIVER|RECEIVER_FACILITY |2021-09-02 14:27:06|CPM0005|ORU^R01|767c2024-23ff-43a8-a165-e47bb1e3a2fe|P|2.8\r\n"
				+ "PID||2|Patient_NHS_ID|NULL|patient2|NULL|NULL||||||||||||\r\n"
				+ "OBR||patient100_20211021_171700|NULL|NULL|||"+ Common.getNowTime(1) +"|" +Common.getNowTime(2)+"|||||||||||||||||\r\n"
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
	
	
	//@Scheduled(initialDelay = 5000, fixedRate = 1000)
	@Async
	public void 전송테스트7() {
		String data = "MSH|^~\\&|BILABGW|NULL|RECEIVER|RECEIVER_FACILITY |2021-09-02 14:27:06|CPM0006|ORU^R01|767c2024-23ff-43a8-a165-e47bb1e3a2fe|P|2.8\r\n"
				+ "PID||2|Patient_NHS_ID|NULL|patient2|NULL|NULL||||||||||||\r\n"
				+ "OBR||patient100_20211021_171700|NULL|NULL|||"+ Common.getNowTime(1) +"|" +Common.getNowTime(2)+"|||||||||||||||||\r\n"
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
	
	
	//@Scheduled(initialDelay = 5000, fixedRate = 100)
	@Async
	public void 전송테스트8() {
		String data = "MSH|^~\\&|BILABGW|NULL|RECEIVER|RECEIVER_FACILITY |2021-09-02 14:27:06|CPM0007|ORU^R01|767c2024-23ff-43a8-a165-e47bb1e3a2fe|P|2.8\r\n"
				+ "PID||2|Patient_NHS_ID|NULL|patient2|NULL|NULL||||||||||||\r\n"
				+ "OBR||patient100_20211021_171700|NULL|NULL|||"+ Common.getNowTime(1) +"|" +Common.getNowTime(2)+"|||||||||||||||||\r\n"
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
	
	
	
	//@Scheduled(initialDelay = 5000, fixedRate = 100)
	@Async
	public void 전송테스트9() {
		String data = "MSH|^~\\&|BILABGW|NULL|RECEIVER|RECEIVER_FACILITY |2021-09-02 14:27:06|CPM0008|ORU^R01|767c2024-23ff-43a8-a165-e47bb1e3a2fe|P|2.8\r\n"
				+ "PID||2|Patient_NHS_ID|NULL|patient2|NULL|NULL||||||||||||\r\n"
				+ "OBR||patient100_20211021_171700|NULL|NULL|||"+ Common.getNowTime(1) +"|" +Common.getNowTime(2)+"|||||||||||||||||\r\n"
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
	
	
	//@Scheduled(initialDelay = 5000, fixedRate = 100)
	@Async
	public void 전송테스트10() {
		String data = "MSH|^~\\&|BILABGW|NULL|RECEIVER|RECEIVER_FACILITY |2021-09-02 14:27:06|CPM0009|ORU^R01|767c2024-23ff-43a8-a165-e47bb1e3a2fe|P|2.8\r\n"
				+ "PID||2|Patient_NHS_ID|NULL|patient2|NULL|NULL||||||||||||\r\n"
				+ "OBR||patient100_20211021_171700|NULL|NULL|||"+ Common.getNowTime(1) +"|" +Common.getNowTime(2)+"|||||||||||||||||\r\n"
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
	
	
	//@Scheduled(initialDelay = 5000, fixedRate = 100)
	@Async
	public void 전송테스트11() {
		String data = "MSH|^~\\&|BILABGW|NULL|RECEIVER|RECEIVER_FACILITY |2021-09-02 14:27:06|CPM0010|ORU^R01|767c2024-23ff-43a8-a165-e47bb1e3a2fe|P|2.8\r\n"
				+ "PID||2|Patient_NHS_ID|NULL|patient2|NULL|NULL||||||||||||\r\n"
				+ "OBR||patient100_20211021_171700|NULL|NULL|||"+ Common.getNowTime(1) +"|" +Common.getNowTime(2)+"|||||||||||||||||\r\n"
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
	
	
	//@Scheduled(initialDelay = 5000, fixedRate = 100)
	@Async
	public void 전송테스트12() {
		String data = "MSH|^~\\&|BILABGW|NULL|RECEIVER|RECEIVER_FACILITY |2021-09-02 14:27:06|CPM0011|ORU^R01|767c2024-23ff-43a8-a165-e47bb1e3a2fe|P|2.8\r\n"
				+ "PID||2|Patient_NHS_ID|NULL|patient2|NULL|NULL||||||||||||\r\n"
				+ "OBR||patient100_20211021_171700|NULL|NULL|||"+ Common.getNowTime(1) +"|" +Common.getNowTime(2)+"|||||||||||||||||\r\n"
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
	
	
	//@Scheduled(initialDelay = 5000, fixedRate = 100)
	@Async
	public void 전송테스트13() {
		String data = "MSH|^~\\&|BILABGW|NULL|RECEIVER|RECEIVER_FACILITY |2021-09-02 14:27:06|CPM0012|ORU^R01|767c2024-23ff-43a8-a165-e47bb1e3a2fe|P|2.8\r\n"
				+ "PID||2|Patient_NHS_ID|NULL|patient2|NULL|NULL||||||||||||\r\n"
				+ "OBR||patient100_20211021_171700|NULL|NULL|||"+ Common.getNowTime(1) +"|" +Common.getNowTime(2)+"|||||||||||||||||\r\n"
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
	
	
	//@Scheduled(initialDelay = 5000, fixedRate = 100)
	@Async
	public void 전송테스트14() {
		String data = "MSH|^~\\&|BILABGW|NULL|RECEIVER|RECEIVER_FACILITY |2021-09-02 14:27:06|CPM0013|ORU^R01|767c2024-23ff-43a8-a165-e47bb1e3a2fe|P|2.8\r\n"
				+ "PID||2|Patient_NHS_ID|NULL|patient2|NULL|NULL||||||||||||\r\n"
				+ "OBR||patient100_20211021_171700|NULL|NULL|||"+ Common.getNowTime(1) +"|" +Common.getNowTime(2)+"|||||||||||||||||\r\n"
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
	
	
	//@Scheduled(initialDelay = 5000, fixedRate = 100)
	@Async
	public void 전송테스트15() {
		String data = "MSH|^~\\&|BILABGW|NULL|RECEIVER|RECEIVER_FACILITY |2021-09-02 14:27:06|CPM0014|ORU^R01|767c2024-23ff-43a8-a165-e47bb1e3a2fe|P|2.8\r\n"
				+ "PID||2|Patient_NHS_ID|NULL|patient2|NULL|NULL||||||||||||\r\n"
				+ "OBR||patient100_20211021_171700|NULL|NULL|||"+ Common.getNowTime(1) +"|" +Common.getNowTime(2)+"|||||||||||||||||\r\n"
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
	
	
	//@Scheduled(initialDelay = 5000, fixedRate = 100)
	@Async
	public void 전송테스트16() {
		String data = "MSH|^~\\&|BILABGW|NULL|RECEIVER|RECEIVER_FACILITY |2021-09-02 14:27:06|CPM0015|ORU^R01|767c2024-23ff-43a8-a165-e47bb1e3a2fe|P|2.8\r\n"
				+ "PID||2|Patient_NHS_ID|NULL|patient2|NULL|NULL||||||||||||\r\n"
				+ "OBR||patient100_20211021_171700|NULL|NULL|||"+ Common.getNowTime(1) +"|" +Common.getNowTime(2)+"|||||||||||||||||\r\n"
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
	

}
