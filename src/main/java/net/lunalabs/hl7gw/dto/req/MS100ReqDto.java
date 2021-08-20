package net.lunalabs.hl7gw.dto.req;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MS100ReqDto<T>{
	
	private String deviceId;
	private String opCode;
	//private List<T> parameter; 인자 안의 인자 속의 배열은 못 바꿈.
	private Parameter<T> parameter;
	private String patientId;
	private String sessionId;
	private String timestampStart; //일단 문자열로 받자.
	private String timestampEnd;
	private String trId;
	
	
//	@JsonFormat(pattern = "yy.MM.dd-kk:mm:ss")
//	@JsonDeserialize(using = LocalDateTimeDeserializer.class) //mvn 추가
//	private LocalDateTime timestampStart;

//	@JsonFormat(pattern = "yy.MM.dd-kk:mm:ss")
//	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
//	private LocalDateTime timestampEnd;
}
