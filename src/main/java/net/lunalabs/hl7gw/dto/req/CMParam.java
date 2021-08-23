package net.lunalabs.hl7gw.dto.req;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class CMParam<T> {  //RVS를 제외한 측정데이터의 공통 Parameter

	private String type;
	private String unit;
	private T value;

	
}
