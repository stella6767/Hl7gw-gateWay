package net.lunalabs.hl7gw.dto.resp;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PR100RespDto {

	private int patientId;
	private String firstName;
	private String lastName;
	private int gender;
	private int age;
	private int height;
	private int weight;
	private String lastSession;		
	private String commnet;
}