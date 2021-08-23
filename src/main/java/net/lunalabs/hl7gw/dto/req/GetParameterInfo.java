package net.lunalabs.hl7gw.dto.req;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class GetParameterInfo<T> {
	
	List<CMParam<T>> cmparams = new ArrayList<CMParam<T>>();
	List<String> fieldName = new ArrayList<String>();
	
}
