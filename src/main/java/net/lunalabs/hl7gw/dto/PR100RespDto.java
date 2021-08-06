package net.lunalabs.hl7gw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class PR100RespDto {

	private String patientId;
	private String firstName;
	private String lastName;
	private String gender;
	private int age;
		
	
}
