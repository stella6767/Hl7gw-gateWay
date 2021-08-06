package net.lunalabs.hl7gw.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class CMRespDto {
	
	private String resultCode;
	private String resultMsg;
	private String trId;
	private List<PR100RespDto>  patientInfos;

}
