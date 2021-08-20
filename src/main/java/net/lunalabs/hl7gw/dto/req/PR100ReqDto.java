package net.lunalabs.hl7gw.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
