package net.lunalabs.hl7gw;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import net.lunalabs.hl7gw.emul.Emulator;

@SpringBootApplication
@EnableScheduling
public class Hl7gwApplication {

	public static void main(String[] args) {
		SpringApplication.run(Hl7gwApplication.class, args);
	}
	
	@Bean
	public CommandLineRunner emulatorStart(Emulator emulator) {
		return (args) -> {
			// 데이터 초기화 하기			
			emulator.start();			
		};
	}

}
