package net.lunalabs.hl7gw.dto.req;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class RVS<T> { //일단은 쓰지 말자.
	
	private String type;
	private String unit;
	private List<BigDecimal> value;

}
