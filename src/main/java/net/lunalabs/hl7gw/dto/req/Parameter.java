package net.lunalabs.hl7gw.dto.req;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

//@JsonIgnoreProperties(ignoreUnknown = true)
@Data //jackson 호출하기 위해서는 getter 필요
public class Parameter<T> {

	private T mv;
	private T rr;
	private RVS rvs;
	private T spo2;
	private T tv;	
	
}
