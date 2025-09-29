package com.bytogether.commservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CommServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommServiceApplication.class, args);
	}

}
