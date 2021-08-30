package net.lunalabs.hl7gw.service;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
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
import net.lunalabs.hl7gw.dto.resp.PR100RespDto;
import net.lunalabs.hl7gw.utills.Common;

@RequiredArgsConstructor
@Service
public class JsonParseService {
	
	
	private static final Logger logger = LoggerFactory.getLogger(JsonParseService.class);
	private final ConcurrentConfig concurrentConfig;
	private final FTPService ftpService;

	Gson gson = new Gson();
	
	
	private final HL7Service hl7Service;

	public <T> void opCodeAction(String strMessage, SocketChannel schn, 
			long lThId) {
		
		logger.debug("strMessage:" + strMessage);
						
		
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

				logger.debug("[gwEmulThread #310] TID[ "
						+ lThId + "] opCode[" + strOpCode + "]");
				
				CMRespDto cmRespDto = new CMRespDto();
				
				concurrentConfig.globalQtsocketMap.put("mySchn", schn);
				logger.debug("여기서 분명 socketChannel을 집어넣었을텐데?? " + concurrentConfig.globalQtsocketMap.get("mySchn"));
				

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
					String filename = (String)obj.get("filename");	
					
					
					cmRespDto.setResultCode("100");
					cmRespDto.setResultMsg("Success");
					cmRespDto.setTrId(trId);
					qtSendCheck(cmRespDto, schn);
					
					try {
						//ftpService.ftpSendToCs(filename); //비동기로 파일전송
						ftpService.ftpSendToCs2(filename);
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
		
		
		String jsonData = gson.toJson(cmRespDto);  //알아서 null값은 걸러냄	
		
		
		logger.debug("3차 스레드 put test:  " + concurrentConfig);			
//		logger.debug(" ?????!" + concurrentConfig.globalQtsocketMap.toString());			
//		logger.debug(" !!!!1!" + (concurrentConfig.globalQtsocketMap.get("")).toString());
		
//	     Attribute attribute = new Attribute();
//	        attribute.setLoggingCode(loggingCode);     Attribute attribute = new Attribute();
//	        attribute.setLoggingCode(loggingCode);	
		
		try {
			Common.sendJsonToQT(jsonData, schn);
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	


}
