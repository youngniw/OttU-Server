package com.tave8.ottu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OttuApplication {

	public static void main(String[] args) {
		SpringApplication.run(OttuApplication.class, args);
	}

}
