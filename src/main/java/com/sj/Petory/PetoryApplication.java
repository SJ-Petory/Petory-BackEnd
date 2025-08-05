package com.sj.Petory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PetoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(PetoryApplication.class, args);
	}

}
