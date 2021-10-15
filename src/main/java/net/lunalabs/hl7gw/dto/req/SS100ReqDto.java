package net.lunalabs.hl7gw.dto.req;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SS100ReqDto {
	
	private String deviceId;
	private String opCode;
	private String pid;
	private String sid;
	private String startTime;
	private String endTime;

	private String trId;

}
