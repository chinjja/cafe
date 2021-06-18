package com.chinjja.issue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IssueApplication {

	public static void main(String[] args) {
		SpringApplication.run(IssueApplication.class, args);
	}

}
