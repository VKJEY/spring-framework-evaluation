package com.example.job.data.persistence.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobInstanceAlreadyExistsException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobExecutionController {
	
	@Autowired
	JobOperator jobOperator;
	
	@Autowired
	JobLauncher jobLauncher;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private Job pipeLineExecutorJob;
	
	@GetMapping("/pipeline/{id}/execute")
	public String executePipeline(@PathVariable String id) {

		logger.debug("Preparing to run the Job");
		
		
		JobParameters jobParameters = new JobParametersBuilder()
				.addLong("time", System.currentTimeMillis())
				.addString("pipeLineId", id)
				.toJobParameters();
		try {
			System.out.println(pipeLineExecutorJob.hashCode());
				jobLauncher.run(pipeLineExecutorJob, jobParameters);
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			e.printStackTrace();
		}
		return "Job Started";
	}
	
	@GetMapping("/pipeline/{id}/executeCustom")
	public String executePipelineByCustomJobOperator(@PathVariable String id) { 
		try {
			jobOperator.start("peopleDataImporter", id);
		} catch (NoSuchJobException | JobInstanceAlreadyExistsException | JobParametersInvalidException e) {
			e.printStackTrace();
		}
		return "Job Started";
	}

}
