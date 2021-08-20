package net.lunalabs.hl7gw.service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;
import net.lunalabs.hl7gw.dto.CMRespDto;
import net.lunalabs.hl7gw.dto.PR100RespDto;
import net.lunalabs.hl7gw.utills.Common;

@RequiredArgsConstructor
@Service
public class HL7Service {
	
	
	private static final Logger logger = LoggerFactory.getLogger(HL7Service.class);
	

	
	public <T> void opCodeAction(String strMessage, SocketChannel schn, 
			ByteBuffer writeBuf, long lThId, SocketChannel schn2) {
		
		logger.debug("strMessage:" + strMessage);
				
		//serverReqThread.socketSends(strMessage, schn2);
		
		
		Gson gson = new Gson();
		
		try {
			if (!strMessage.equals("")) {
				
				strMessage = strMessage.replaceAll("#STX#", "");// 거르기
				strMessage = strMessage.replaceAll("#ETX#", "");// 2차 거르기
				
				logger.debug("[gwEmulThread #300] TID[ "
						+ lThId + "] socketRead Start[" + strMessage + "]");
				 
								
				JSONParser parser = new JSONParser();
				JSONObject obj;
				
				
				
				
				obj = (JSONObject)parser.parse(strMessage);
	
				String strOpCode = (String)obj.get("opCode");
				String trId = (String)obj.get("trId");
								
				List<PR100RespDto> fakeList = new ArrayList<>();
				

		
				if(strOpCode.equals("PR100")) {
					
					logger.debug("환자 정보 응답");	
					
					//pr100RespDto = gson.fromJson(strMessage, PR100RespDto.class);
					
					
					String searchType = (String)obj.get("searchType");
					String searchWord = (String)obj.get("searchWord");

					
					logger.info("searchType: " + searchType + "   searchWord: " + searchWord);
					

					
					//아마도 DB에서 검증 후 돌려주겠지.
					//logger.debug(pr100RespDto.toString());
					
					

					switch (searchType) {
					
					case "patientId" :
						
						fakeList = Common.searchPatientID(SocketService.fakePatientsList, fakeList, searchWord);
					
					case "name" :
						
						fakeList = Common.searchName(SocketService.fakePatientsList, fakeList, searchWord);

						
						break;
					}
					
					
	
					
					
										
				}else if(strOpCode.equals("MS100")) {
								
					List<T> params = (ArrayList)obj.get("params");					
					logger.debug("params 배열 " + params);	
				}
				
				

				logger.debug("[gwEmulThread #310] TID[ "
						+ lThId + "] opCode[" + strOpCode + "]");
				
				CMRespDto cmRespDto = new CMRespDto();
				

				switch (strOpCode) {
				
				case "HC100" :
					
					cmRespDto.setResultCode("100");
					cmRespDto.setResultMsg("Success");
					cmRespDto.setTrId(trId);
					
					break;
				
				case "PR100" :
									
					cmRespDto.setPatientInfos(fakeList);
					cmRespDto.setResultCode("100");
					cmRespDto.setResultMsg("Success");
					cmRespDto.setTrId(trId);
					
					break;
					
					
				case "FT100" :
					
					cmRespDto.setResultCode("100");
					cmRespDto.setResultMsg("Success");
					cmRespDto.setTrId(trId);
					
				
				case "MS100" :
					
					cmRespDto.setResultCode("100");
					cmRespDto.setResultMsg("Success");
					cmRespDto.setTrId(trId);
					
					
					break;	
						

				}
	
				
				String jsonData = gson.toJson(cmRespDto);  //알아서 null값은 걸러냄		
				logger.debug("jsonData: " + jsonData);

		        writeBuf.flip();
		        
		        //writeBuf = common.str_to_bb(json.toString());
		        writeBuf = Common.str_to_bb(jsonData);
		        schn.write(writeBuf);
		        writeBuf.clear();

	
			}
		} catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	

}
