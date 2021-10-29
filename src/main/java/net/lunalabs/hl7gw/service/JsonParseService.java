package net.lunalabs.hl7gw.service;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutionException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;
import net.lunalabs.hl7gw.config.ConcurrentConfig;
import net.lunalabs.hl7gw.dto.CMRespDto;
import net.lunalabs.hl7gw.utills.Common;

@RequiredArgsConstructor
@Service
public class JsonParseService {
	
	
	private static final Logger logger = LoggerFactory.getLogger(JsonParseService.class);
	private final ConcurrentConfig concurrentConfig;
	private final FTPService ftpService;

	
	
	private final HL7Service hl7Service;

	public <T> void opCodeAction(String strMessage,
			long lThId) {
		
		logger.debug("strMessage:" + strMessage);
						
		
		SocketChannel schn = concurrentConfig.globalSocketMap.get("qt");

		
		
		try {
			if (!strMessage.equals("")) {
				
				strMessage = strMessage.replaceAll("#STX#", "");// 거르기
				strMessage = strMessage.replaceAll("#ETX#", "");// 2차 거르기
				
				logger.debug("[gwEmulThread #300] TID[ "
						+ lThId + "] socketRead Start[" + strMessage + "]");
							
				//serverReqThread.socketSends(strMessage, schn2);
									
				JSONParser parser = new JSONParser();
				JSONObject obj;
		
				obj = (JSONObject)parser.parse(strMessage);
	
				String strOpCode = (String)obj.get("opCode");
				String trId = (String)obj.get("trId");											
//				String sessionId = (String)obj.get("sessionId");											

				logger.debug("[gwEmulThread #310] TID[ "
						+ lThId + "] opCode[" + strOpCode + "]");
				
				CMRespDto cmRespDto = new CMRespDto();
				
				//concurrentConfig.globalSocketMap.put("qt", schn);
				//logger.debug("여기서 분명 socketChannel을 집어넣었을텐데?? " + concurrentConfig.globalSocketMap.get("qt"));
				

				switch (strOpCode) {
				
				case "HC100" :				
					cmRespDto.setResultCode("100");
					cmRespDto.setResultMsg("Success");
					cmRespDto.setTrId(trId);	
					
					qtSendCheck(cmRespDto, schn);
					break;
				
				case "PR100" :
									
					hl7Service.parseToPR100Req(strMessage);
					break;
					
				case "MS100" :
					
					hl7Service.parseToMS100Req(strMessage);				
					break;	
					
				case "FT100" :
					//String filename = String.valueOf(obj.get("filename"));	//여기서 문제.
//					JsonReader reader = new JsonReader(new StringReader(strMessage));
//					reader.setLenient(true);
					
//					String escapeFile =StringEscapeUtils.escapeJson(strMessage);
//					FileReqDto fileReqDto = gson.fromJson(특수문자파싱문자열, FileReqDto.class);
//					//String filename = fileReqDto.getFilename();
					String parseFilepath = Common.parsingFilepath(strMessage);
				
					logger.debug("escapeFile: " + parseFilepath);

					obj = (JSONObject)parser.parse(parseFilepath);
					String filename = String.valueOf(obj.get("filename"));	//여기서 문제.			
					logger.debug("ftp uploder로 넘겨주는 filename: " + filename);
					
					try {
						//비동기로 파일전송
						ftpService.ftpSendToCs(filename);						
						cmRespDto.setResultCode("100");
						cmRespDto.setResultMsg("Success");
						cmRespDto.setTrId(trId);
						qtSendCheck(cmRespDto, schn);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					break;
					

					case "SS100" :
					
					hl7Service.parseToSS100Req(strMessage);				
					break;	
					
				}
	

				
	
			}
		} catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

		
	}
	
	
	
	public void qtSendCheck(CMRespDto cmRespDto, SocketChannel schn) throws IOException {
		Gson gson = new Gson();

		String jsonData = gson.toJson(cmRespDto);  //알아서 null값은 걸러냄	
		
		logger.debug("3차 스레드 put test:  " + concurrentConfig);			

		try {
			Common.sendJsonToQT(jsonData, schn);
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	


}
