package net.lunalabs.hl7gw.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

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

import net.lunalabs.hl7gw.dto.req.MS100ReqDto;
import net.lunalabs.hl7gw.dto.req.PR100ReqDto;
import net.lunalabs.hl7gw.utills.Common;

@EnableAsync
@Service
public class HL7Service {

	private static final Logger logger = LoggerFactory.getLogger(HL7Service.class);

	ObjectMapper mapper = new ObjectMapper();
	StringBuffer sb = new StringBuffer(); //여기다 선언하는 게 맞나싶다.. 계속 append 되어지는 거 아닌가..

	public void parseToPR100Req(String jsonReqData) throws JsonMappingException, JsonProcessingException {

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
		
	}
	
	public void parseToMS100Req(String jsonReqData) throws JsonMappingException, JsonProcessingException {

		logger.debug("측정데이터 HL7 parsing start");
		MS100ReqDto<?> reqDto = mapper.readValue(jsonReqData, MS100ReqDto.class);
		logger.debug("convert to java object: " + reqDto);
		
		
		// protocol enter(개행) 문자는 필요한가.
		sb.append("MSH|^~\\&|BILABGW|NULL|RECEIVER|RECEIVER_FACILITY |" + Common.parseLocalDateTime() + "||ORU^R01|" + reqDto.getTrId() +"|P|2.8\r\n"
				+ "");
		
		sb.append("PID||" + reqDto.getPatientId() + "|Patient_NHS_ID|NULL|PatientName|NULL|NULL|M|||||||||||");  //M은 gender?
		sb.append("OBR||NULL|NULL|NULL|||"+ Common.parseLocalDateTime() + "||||||||||||||||||");
		sb.append("OBX|1|NM|CUBESCAN^SERIALNUMBER||576|ml||||||||"+ Common.parseLocalDateTime() + "|");

		//logger.debug("PR100 파싱결과: " + sb.toString());
		
	}
	

	public void classfyOpcode(String jsonReqData) throws ParseException {

		logger.debug("HL7 parsing ready");

		StringBuffer sb = new StringBuffer();
		// sb.append(jsonData);

		JSONParser parser = new JSONParser();
		JSONObject obj;

		obj = (JSONObject) parser.parse(jsonReqData);

		String strOpCode = (String) obj.get("opCode");
		String trId = (String) obj.get("trId");
		String searchType = (String) obj.get("searchType");
		String searchWord = (String) obj.get("searchWord");

		// Map<String, Object> resHmap = new HashMap<>();

	}

	public void convertToHL7(String jsonData) {

//		switch (strOpCode) {
//
//		case "HC100":
//			//convertToHL7(jsonReqData);
//
//			break;
//
//		case "PR100":
//			convertToHL7(jsonReqData);
//
//			break;
//
//		case "FT100":
//			convertToHL7(jsonReqData);
//
//			break;
//
//		case "MS100":
//			convertToHL7(jsonReqData);
//
//
//			break;
//			
//		default: break;
//
//		}

	}

}
