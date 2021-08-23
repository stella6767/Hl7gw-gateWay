package net.lunalabs.hl7gw.dto.req;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

@FieldNameConstants
@JsonIgnoreProperties(ignoreUnknown = true)
@Data // jackson 호출하기 위해서는 getter 필요
public class Parameter<T> {

	private CMParam<T> mv;
	private CMParam<T> rr;
	private CMParam<T> rvs;
	private CMParam<T> spo2;
	private CMParam<T> tv;

	public List<String> getCMParamType() {

		List<String> valueTypes = new ArrayList<>();

		String mvType = mv.getType();
		String rrType = rr.getType();
		String rvsType = rvs.getType();
		String spo2Type = spo2.getType();
		String tvType = tv.getType();

		valueTypes.add(mvType);
		valueTypes.add(rrType);
		valueTypes.add(rvsType);
		valueTypes.add(spo2Type);
		valueTypes.add(tvType);

		return valueTypes;
	}

	
	
	public List<CMParam<T>> getCMParams(){
		
		List<CMParam<T>> cmparams = new ArrayList<>();
		
		cmparams.add(mv);
		cmparams.add(rr);
		cmparams.add(rvs);
		cmparams.add(spo2);
		cmparams.add(tv);
		
		return cmparams;		
	}
	
	
	
//	public GetParameterInfo getCMParams() {
//
//		GetParameterInfo getParameterInfo = new GetParameterInfo<>(); // 일단 만들자..
//
//		
//		getParameterInfo.cmparams.add(mv);
//		getParameterInfo.cmparams.add(rr);
//		getParameterInfo.cmparams.add(rvs);
//		getParameterInfo.cmparams.add(spo2);
//		getParameterInfo.cmparams.add(tv);
//		
//
//		Field[] fields = Parameter.class.getFields();
//
//		
//		
//		for (int i = 0; i < fields.length; i++) {
//			// print name of Fields
//			System.out.println("Name of Field:" + fields[i].getName());
//			
//			getParameterInfo.fieldName.add(fields[i].getName());
//		}
//
//		return getParameterInfo;
//	}

}
