package com.example.job.data.persistence;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing(modular=true)

public class JobDataPersistenceSampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobDataPersistenceSampleApplication.class, args);
	}

}

