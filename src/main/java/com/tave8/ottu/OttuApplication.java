package com.tave8.ottu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class OttuApplication {

	public static void main(String[] args) {
		SpringApplication.run(OttuApplication.class, args);
	}

	@PostConstruct
	public void started() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));	//아무리 DB와 시스템의 timezone을 Asia/Seoul로 해도 서버 배포 시에는 이 코드 필수!
	}
}
