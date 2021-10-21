package net.lunalabs.hl7gw.service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import net.lunalabs.hl7gw.config.ConcurrentConfig;


/**
 * 
 * @FileName  : Emulator.java
 * @Project   : HL7GwEmul
 * @Date      : 2021. 08. 20. 
 * @작성자      : winix
 * @설명       : [HL7GW 설명]
 * 				1. Socket 을 Listen
 * 				2. 요청 수신 후 thread 분기 
 */
//@Controller





@RequiredArgsConstructor
@Service
public class QTSocketService {

	private static final Logger logger = LoggerFactory.getLogger(QTSocketService.class);

	private final JsonParseService jsonParseService;
	private final ConcurrentConfig concurrentConfig; 

	
	
	//Test data
	//public static List<String> fakeName = Common.generateRandomString();
	//public static List<PR100RespDto> fakePatientsList = Common.createDummyPatients();
	
	ServerSocketChannel serverSocketChannel = null;
	public SocketChannel socketChannel = null;
	

	@Async
	public void socketThread() {
		
		
		logger.debug( "3차 스레드 di test: "   + concurrentConfig.toString());

		try {

			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.socket().bind(new InetSocketAddress(5050));

			boolean bLoop = true;

			while (bLoop) {
				try {
					socketChannel = serverSocketChannel.accept();
					socketChannel.configureBlocking(true);
					concurrentConfig.globalSocketMap.put("qt", socketChannel);

					// System.out.println("[ESMLC Listen[" + "] Socket Accept EsmlcIfWorkThread
					// Start");
					logger.info("[ESMLC Listen[" + "] 5050 port Socket Accept EsmlcIfWorkThread Start");
					socketWork();

				} catch (Exception e) {
					e.printStackTrace();
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}

				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		//return CompletableFuture.completedFuture(socketChannel);


	}

	
	private void socketWork() {

		SocketChannel schn = concurrentConfig.globalSocketMap.get("qt");

		// String result = "";
		boolean isRunning = true; // 일단 추가, socketWork 중지할지 안 중지할지
			
	
		
		while (isRunning) {

			try {

				int byteCount = 0;
				byte[] readByteArr;

				long lThId = Thread.currentThread().getId();

				//ByteBuffer readBuf = ByteBuffer.allocate(10); //버퍼 메모리 공간확보
				ByteBuffer readBuf = ByteBuffer.allocate(10240);

				logger.debug("첫번째  while문");

				/*
				 * ### STX 가 없는 경우 
				 * 
				 * CASE 1 : #ETX# 가 있는 경우 - #ETX# 까지 잘라서 이전 값은 버리고 이 후 값만 버퍼에서
				 * 읽음
				 * 
				 * CASE 2 : #ETX# 가 없는 경우 - #ETX# 가 나올때까지 계속 버퍼에서 읽어서 버림 
				 * 
				 * 
				 * * ### STX 가 있는 경우 
				 * CASE 3 : #ETX# 가 없는 경우 - 계속 버퍼에서 읽어 append 함
				 * 
				 * CASE 4 : #ETX# 가 1개 있고 버퍼의 끝인 경우 - #ETX# 만 버리고 opCodeAction 호출
				 * 
				 * CASE 5 : #ETX# 가 1개 있고 버퍼의 끝이 아닌 경우 - #ETX# 단위로 opCodeAction 호출하고 나머지 부분은
				 * 저장하여 버퍼에서 계속읽어서 append 함
				 * 
				 * CASE 6 : #ETX# 가 2개 이상 있고 버퍼의 끝인 경우 - #ETX# 만 버리고 opCodeAction 호출 하고 다시 #ETX#
				 * 를 읽어 동일하게 처리
				 * 
				 * CASE 7 : #ETX# 가 2개 이상 있고 버퍼의 끝이 아닌 경우 - #ETX# 단위로 opCodeAction 호출하고 나머지 부분은
				 * 저장하여 버퍼에서 계속읽어서 append 함
				 */

				// 무한 루프

				String result = ""; // 요기서 초기화, 요까지는 다 처리하고 가야된다.

				while (byteCount >= 0) {
					long time = System.currentTimeMillis();
					String strDT;

					try {

						byteCount = schn.read(readBuf); // 소켓채널에서 한번에 초과되는 버퍼사이즈의 데이터가 들어오면..

						logger.debug("[gwEmulThread #100] TID[" + "] byteCount :  " + byteCount);
						// logger.debug("isRunning why: " + isRunning);
					} catch (Exception e) {
						// e.printStackTrace();
						logger.debug("갑자기 클라이언트 소켓이 닫혔을 시");

						schn.close();
						isRunning = false;
						break;
						// continue;
					}

					int i = 0;

					// 버퍼에 값이 있다면 계속 버퍼에서 값을 읽어 result 를 완성한다.
					// while (byteCount > 0 || isRunning) {
					while (byteCount > 0) {

						readBuf.flip(); // 입력된 데이터를 읽기 위해 read-mode로 바꿈, positon이 데이터의 시작인 0으로 이동

						readByteArr = new byte[readBuf.remaining()]; // 현재 위치에서 limit까지 읽어드릴 수 있는 데이터의 개수를 리턴

						// 일단 확인
						// logger.debug("limit까지의 값: " + readBuf.remaining());
						// logger.debug("capacity: " + readBuf.capacity());
						// logger.debug("position: " + readBuf.position());

						readBuf.get(readByteArr); // 데이터 읽기

						result = result + new String(readByteArr, Charset.forName("UTF-8")); // 어차피 여기서 계속 더하니까.

						// 공백제거
						result = result.replaceAll(" ", "");
						result = result.replaceAll("(\r|\n|\r\n|\n\r)", ""); // Enter 문자 제거

						logger.debug("[gwEmulThread #200] TID[ " + lThId + "] socketRead Start[" + result
								+ "], byteCount[" + byteCount + "], i[" + i + "]");
						i++;

						try {
							byteCount = schn.read(readBuf);
							logger.debug("[gwEmulThread #210] TID[" + result + "] byteCount :  " + byteCount);
						} catch (Exception e) {
							e.printStackTrace();
							// break;
						}

						boolean bEtxEnd = true; // 아래 while문을 실행할지 안할지

						// #ETX# 단위로 루프
						while (!result.equals("") && bEtxEnd) {

							logger.debug("#ETX#단위로 루프 돌기 전 result: " + result);

							int indEtx = (result.lastIndexOf("#ETX#")) + 5;
							logger.debug("indEtx: " + indEtx + " result.length:  " + result.length());

							// #STX#가 없는 경우
							if (!result.contains("#STX#")) {
								// case2 #ETX# 도 없는 경우
								if (!result.contains("#ETX#")) {
									logger.debug("case2");
									// result = ""; //문자열로 치환한 버퍼를 비운다.
									bEtxEnd = false;
									readBuf.clear(); // 이걸 해줘야 되네.. 100으로 감. 근데 positon이 맥스니 byteCount도 당연히 -라 아래
														// while문을 못탐
									break;

								} else {
									// logger.debug("case2 + result " + result);
									// lastIndexOf의 문제는 앞의 정상적인 단위도 다 버린다는 거
									result = result.substring(result.lastIndexOf("#ETX#") + 5);
									// result = result.substring(result.indexOf("#ETX#")+5);
									logger.debug("case1 + result: " + result);
								}
							}

							// #STX#가 있는 경우

							// case3
							if (!result.contains("#ETX#")) {

								// - 계속 버퍼에서 읽어 append 함
								logger.debug("#ETX#가 없는 result 값: " + result);
								logger.debug("case3 position: " + readBuf.position());
								readBuf.clear(); // 위와 같은 이유, position 초기화
								logger.debug("case3 position: " + readBuf.position());
								break; // 이 블록에서 빠져나온다.
							}

							// 요것만으로는 힘듬..
							logger.debug("1차 가공: " + result);

							int countETX = StringUtils.countMatches(result, "#ETX#");
							int countSTX = StringUtils.countMatches(result, "#STX#");

							// #STX# 가 맨 처음에 있지 않다면 #ETX# 까지 버림
							if (result.indexOf("#STX#") != 0) {
								// #ETX# 가 없다면 전체 버림
								logger.debug("#STX# 가 맨 처음에 있지 않다면 #ETX# 까지 버림 " + result + ", indEtx: " + indEtx);

								if (indEtx < 0) {
									result = "";
								} else { // #ETX# 까지 버림
									result = result.substring(indEtx); // 수정
									logger.debug("아예 다 버리자.. " + result);
									// 아예 다 버리네..
									readBuf.clear(); // 추가
								}
								continue; // 현재 while문의 최상단으로 건너뛰기
							}
							// result = result.replaceAll("#STX#", "");

							logger.debug("2차 가공 result: " + result);

							if (indEtx > 0) { // #ETX#가 있을 시

								// 여기서 ETX 단위로 짤라야 되나, 공통모듈화 나중에
								logger.debug("#ETX# 개수: " + countETX);

								if (result.length() == indEtx && countETX == 1) { // case4

									logger.debug("case4");
									jsonParseService.opCodeAction(result, lThId);

									logger.debug("[gwEmulThread #220] TID[ " + lThId + "] socketRead Start[" + result
											+ "], byteCount[" + byteCount + "], i[" + i + "]");

									result = "";
									bEtxEnd = false;
									readBuf.clear();
								} else if (countETX > 1 && result.length() == indEtx) { // case6

									String[] resultArray = result.split("#ETX#");

									logger.debug("case6 길이: " + resultArray.length);
									for (int a = 0; a < resultArray.length; a++) {
										logger.debug(resultArray[a]); // 마지막은 짤리는구만,
										jsonParseService.opCodeAction(resultArray[a], lThId);
									}
									result = "";
									readBuf.clear();
									bEtxEnd = false;

								} else if (result.length() != indEtx && countETX == 1) { // case5

									String[] resultArray = result.split("#ETX#");

									logger.debug("case5 길이: " + resultArray.length);

									for (int a = 0; a < resultArray.length; a++) {
										logger.debug(resultArray[a]); // 마지막은 짤리는구만,
									}

									if (!(resultArray[resultArray.length - 1].contains("#ETX#"))) {
										for (int a = 0; a < resultArray.length - 1; a++) {
											jsonParseService.opCodeAction(resultArray[a], lThId);
										}

										// 예를 들어 #ETX# #STX#{sdfsfdsdf data가 있을시 #STX#로 이어지는 데이터를 저장
										result = resultArray[resultArray.length - 1];
										// 다시 버퍼를 읽음
										readBuf.clear();
										break;
									}

								} else { // case7

									String[] resultArray = result.split("#ETX#");

									logger.debug("case7 길이: " + resultArray.length);

									logger.debug("[gwEmulThread #230] TID[ " + lThId + "] socketRead Start[" + result
											+ "], byteCount[" + byteCount + "], i[" + i + "]");

									if (!(resultArray[resultArray.length - 1].contains("#ETX#"))) {
										logger.debug("case7");
										for (int a = 0; a < resultArray.length - 1; a++) {
											jsonParseService.opCodeAction(resultArray[a], lThId);
										}

										// 예를 들어 #ETX# #STX#{sdfsfdsdf data가 있을시 #STX#로 이어지는 데이터를 저장
										result = resultArray[resultArray.length - 1];
										// 다시 버퍼를 읽음
										readBuf.clear();
										break;
									}

								}

							}

						} // #ETX# 단위로 루프

					} // byteCount > 0

					Thread.sleep(100);
					// Thread.sleep(1000); //일단 1초로
				} // 무한루프

				schn.close(); // 소켓 닫기

			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}

	}
	
	
	
	
	
	

}
