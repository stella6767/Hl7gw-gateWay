package net.lunalabs.hl7gw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class PR100RespDto {

	private int patientId;
	private String name;
	private String gender;
	private int height;
	private int weight;
		
	
}
