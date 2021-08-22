package net.lunalabs.hl7gw.dto.req;

import lombok.Data;

@Data
public class CMParam<T> {
	
	private String type;
	private String unit;
	private T value;
	
}
