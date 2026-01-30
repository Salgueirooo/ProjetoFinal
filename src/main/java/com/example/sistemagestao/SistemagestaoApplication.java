package com.example.sistemagestao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SistemagestaoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SistemagestaoApplication.class, args);
	}

}
