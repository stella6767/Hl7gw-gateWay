package net.lunalabs.hl7gw.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import net.lunalabs.hl7gw.dto.req.CMParam;
import net.lunalabs.hl7gw.dto.req.MS100ReqDto;
import net.lunalabs.hl7gw.dto.req.PR100ReqDto;
import net.lunalabs.hl7gw.dto.req.Parameter;
import net.lunalabs.hl7gw.utills.Common;


@RequiredArgsConstructor
@EnableAsync
@Service
public class HL7Service {

	private static final Logger logger = LoggerFactory.getLogger(HL7Service.class);
	
	private final CSSocketService csSocketService;
	

	ObjectMapper mapper = new ObjectMapper();
	StringBuffer sb = new StringBuffer(); //여기다 선언하는 게 맞나싶다.. 계속 append 되어지는 거 아닌가..

	public void parseToPR100Req(String jsonReqData) throws JsonMappingException, JsonProcessingException {

		sb.delete(0, sb.length());
		
		logger.debug("PR100Req HL7 parsing start");
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

			sb.append("PID||" + reqDto.getSearchWord() + "|Patient_NHS_ID|NULL||NULL|NULL|M|||||||||||\r\n" + "");

			break;

		case "name":

			sb.append("PID|||Patient_NHS_ID|NULL|" + reqDto.getSearchWord() + "|NULL|NULL|M|||||||||||");

			break;

		default:
			break;
		}
		
		logger.debug("PR100 파싱결과: " + sb.toString());
		
		
		//csSocketService.hl7ProtocolSendThread(sb.toString(), csSocketService.socketChannel2);
		
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
		sb.append("OBR||NULL|NULL|NULL|||"+ Common.parseLocalDateTime() + "||||||||||||||||||\r\n");
		

		List<?> cmParams = reqDto.getParameter().getCMParams();
		
		int i = 1;
		
		for (Object object : cmParams) {
				
			
			if(object != null) {
				
				String valueType = ((CMParam<T>)object).getType(); 	
				T value = ((CMParam<T>)object).getValue();		
				String unit = ((CMParam<T>)object).getUnit();
			
				switch (valueType) {
				
				case "NM":
					
					sb.append("OBX|"+ i +"|NM|CUBESCAN^SERIALNUMBER||" + value + "|"+unit+"||||||||"+ Common.parseLocalDateTime() + "|\r\n");
					
					break;
					
				case "NA":

					List<String> values = (List<String>) value;
					String parseValues = Common.parseToBigDecimalList(values);		
					sb.append("OBX|"+i+"|NA|CUBESCAN^SERIALNUMBER||" + parseValues + "|" + unit + "||||||||"+ Common.parseLocalDateTime() + "|\r\n");
					//logger.debug("values!!!!: " + values);
					
					break;
					
				default:
					break;
					
				}			

				i++;
				
			}								
		}
		

		logger.debug("MS100 파싱결과: " + sb.toString());
		
		
		//csSocketService.hl7ProtocolSendThread(sb.toString(), csSocketService.socketChannel2);
		
		
		try {
			CompletableFuture<SocketChannel> completableFuture = csSocketService.csSocketStart();
			SocketChannel channel = completableFuture.get(); //일단은 그냥 blocking 시켜서 보내자. 후에 thencombine으로 교체
			System.out.println(channel);
			
			csSocketService.hl7ProtocolSendThread(sb.toString(), channel);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	




}
