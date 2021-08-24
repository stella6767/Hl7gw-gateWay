package net.lunalabs.hl7gw.dto.req;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PR100ReqDto {
	
	private String deviceId;
	private String opCode;
	private String trId;
	private String searchType;
	private String searchWord;

}
