package com.resumeAi.resume;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.resumeAi.resume.Entity")

public class ResumeUploadAiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResumeUploadAiApplication.class, args);
	}

}
