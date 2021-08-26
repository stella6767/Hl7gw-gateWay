package net.lunalabs.hl7gw.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.lunalabs.hl7gw.dto.resp.PR100RespDto;


@JsonInclude(JsonInclude.Include.NON_NULL) //ObjectMapper로 serialize시 null값 제외하기
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CMRespDto {
	
	private String resultCode;
	private String resultMsg;
	private String trId;
	private List<PR100RespDto>  patientInfos;

}
