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

	private Integer patientId;
	private String firstName;
	private String lastName;
	private Integer gender;
	private Integer age;
	private Double height;
	private Double weight;
	private String commnet;
}
