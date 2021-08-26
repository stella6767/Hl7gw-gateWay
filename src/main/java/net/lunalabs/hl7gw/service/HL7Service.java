package net.lunalabs.hl7gw.service;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import net.lunalabs.hl7gw.config.ConcurrentConfig;
import net.lunalabs.hl7gw.dto.req.CMParam;
import net.lunalabs.hl7gw.dto.req.MS100ReqDto;
import net.lunalabs.hl7gw.dto.req.PR100ReqDto;
import net.lunalabs.hl7gw.utills.Common;


@RequiredArgsConstructor
@EnableAsync
@Service
public class HL7Service {

	private static final Logger logger = LoggerFactory.getLogger(HL7Service.class);
	
	private final CSSocketService csSocketService;
	private final ConcurrentConfig concurrentConfig;


	ObjectMapper mapper = new ObjectMapper();

	
	StringBuffer sb = new StringBuffer(); //여기다 선언하는 게 맞나싶다.. 계속 append 되어지는 거 아닌가..

	public void parseToPR100Req(String jsonReqData) throws JsonMappingException, JsonProcessingException {

		sb.delete(0, sb.length()); //초기화
		

		
		logger.debug("PR100Req HL7 parsing start");
		
		logger.debug(jsonReqData);
		
		PR100ReqDto reqDto = mapper.readValue(jsonReqData, PR100ReqDto.class);
		logger.debug("convert to java object: " + reqDto);

		String searchType = reqDto.getSearchType();

		// protocol enter 문자는 필요한가.
		sb.append("MSH|^~\\&|BILABGW|NULL|RECEIVER|RECEIVER_FACILITY |" + Common.parseLocalDateTime() + "||RQI^I02|" + reqDto.getTrId() +"|P|2.8\r\n" + "");

		if (searchType == null) {
			logger.debug("searchType이 없습니다..");
		}

		// 나중에 스위치문으로 searchtype 분기
		switch (searchType) {
		case "patientId":

			sb.append("PID||" + reqDto.getSearchWord() + "|Patient_NHS_ID|NULL||NULL|NULL||||||||||||\r\n" + "");

			break;

		case "name":

			sb.append("PID|||Patient_NHS_ID|NULL|" + reqDto.getSearchWord() + "|NULL|NULL|M|||||||||||");

			break;

		default:
			break;
		}
		
		logger.debug("PR100 파싱결과: " + sb.toString());
		
		
		
		try {
			sendToCSsocket(sb.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	
	public <T> void parseToSS100Req(String jsonReqData) throws JsonMappingException, JsonProcessingException {

		
		
		
		
	}
	
	
	
	public <T> void parseToMS100Req(String jsonReqData) throws JsonMappingException, JsonProcessingException {

		sb.delete(0, sb.length());
		
		logger.debug("측정데이터 HL7 parsing start");
		MS100ReqDto<?> reqDto = mapper.readValue(jsonReqData, MS100ReqDto.class);
		logger.debug("convert to java object: " + reqDto);
		
		
		// protocol enter(개행) 문자는 필요한가.
		sb.append("MSH|^~\\&|BILABGW|NULL|RECEIVER|RECEIVER_FACILITY |" + Common.parseLocalDateTime() + "||ORU^R01|" + reqDto.getTrId() +"|P|2.8\r\n"
				+ "");
		
		sb.append("PID||" + reqDto.getPatientId() + "|Patient_NHS_ID|NULL||NULL|NULL||||||||||||\r\n");  //M은 gender?
		sb.append("OBR||"+ reqDto.getSessionId()+"|NULL|NULL|||"+ reqDto.getTimestampStart() + "|"+ reqDto.getTimestampEnd() +"|||||||||||||||||\r\n");
		

		//reqDto.getParameter().getClass().getDeclaredField(jsonReqData)
				
		List<String> filedNames = Common.getValueType(reqDto.getParameter());		
		List<?> cmParams = reqDto.getParameter().getCMParams();
				
		
		
		int i = 1;
		
		for (Object object : cmParams) {
				
			
			if(object != null) {
				
				String valueType = ((CMParam<T>)object).getType(); 	
				T value = ((CMParam<T>)object).getValue();		
				String unit = ((CMParam<T>)object).getUnit();
			
				logger.debug("sp.더하기");
				
				switch (valueType) {
				
				case "NM":
					
					sb.append("OBX|"+ i +"|NM|"+    filedNames.get(i-1)   +"||" + value + "|"+unit+"||||||||"+ Common.parseLocalDateTime() + "|\r\n");
					
					break;
					
				case "NA":

					List<String> values = (List<String>) value;
					String parseValues = Common.parseToBigDecimalList(values);		
					sb.append("OBX|"+i+"|NA|"+filedNames.get(i-1) +"||" + parseValues + "|" + unit + "||||||||"+ Common.parseLocalDateTime() + "|\r\n");
					//logger.debug("values!!!!: " + values);
					
					break;
					
				default:
					break;
					
				}			

				i++;
				
			}								
		}
		

		logger.debug("MS100 파싱결과: " + sb.toString());
				
		
		try {
			sendToCSsocket(sb.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
	}
	

	
	public void sendToCSsocket(String Hl7parsingData) throws IOException, InterruptedException, ExecutionException {
		
		
			CompletableFuture<SocketChannel> completableFuture = csSocketService.csSocketStart();
			SocketChannel channel = completableFuture.get(); //일단은 그냥 blocking 시켜서 보내자. 후에 thencombine으로 교체
			System.out.println(channel);
			
			if(channel.isConnected()) {
				logger.debug("cssocket channel이 정상적으로 연결되었습니다.");
				csSocketService.hl7ProtocolSendThread(Hl7parsingData, channel);
			}else if(!channel.isConnected()) {
				logger.debug("cssocket channel이 연결이 끊어졌습니다.");
			}
		
	}



}
