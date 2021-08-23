package net.lunalabs.hl7gw;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

public class TestParsing {

	
	@Test
	public void parseToBigDecimal() {
		
		String text = "83703921.63870761";
		
		BigDecimal decimal = new BigDecimal(8.370392163870761E7);
		BigDecimal decimal2 = new BigDecimal(text);
		
		System.out.println(decimal);
		System.out.println(decimal2);
		
	}
	
	
	
	@Test
	public void parseToBigDecimalList() {
		
		List<String> values = new ArrayList<String>();
		
		values.add("83766671.93251821");
		values.add("83703921.63870761");
		values.add("83779506.46560705");

		
		List<BigDecimal> bigDecimalList = values.stream()
		        .map(BigDecimal::new)
		        .collect(Collectors.toList());

//		System.out.println(bigDecimalList);
		
		
		String joinedString = StringUtils.join(bigDecimalList, "^");
		System.out.println(joinedString);
		
	}
	
	
	@Test
	public void getFiledName() {
		
		//String propertyName = PropertyUtils.getPropertyName(A.class, A::getSalary);

		
		
	}
	
	
	
}
