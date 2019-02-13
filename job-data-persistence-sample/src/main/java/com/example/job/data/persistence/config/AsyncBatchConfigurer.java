package com.example.job.data.persistence.config;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.ApplicationContextFactory;
import org.springframework.batch.core.configuration.support.GenericApplicationContextFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BasicBatchConfigurer;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.job.data.persistence.jobs.PeopleDataImporterJob;

@ConditionalOnMissingBean(BatchConfigurer.class)
@Configuration
@Profile("!test")

public class AsyncBatchConfigurer extends BasicBatchConfigurer {

	@Autowired
	private DataSource dataSource;
	
	@Autowired
    JobRepository jobRepository;
    
	@Autowired
    JobRegistry jobRegistry;
    
	@Autowired
    JobLauncher jobLauncher;
    
	@Autowired
    JobExplorer jobExplorer;
	
	@Autowired
	private TransactionManagerCustomizers transactionManagerCustomizers;

	@Autowired
	private BatchProperties properties;
	
	protected AsyncBatchConfigurer(BatchProperties properties, DataSource dataSource,
			TransactionManagerCustomizers transactionManagerCustomizers) {
		super(properties, dataSource, transactionManagerCustomizers);
		this.dataSource = dataSource;
	}
	
	
	@Override
	protected JobLauncher createJobLauncher() throws Exception {
		SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
		jobLauncher.setJobRepository(getJobRepository());
		jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
		jobLauncher.afterPropertiesSet();
		return jobLauncher;
	}

	@Override
	public JobRepository createJobRepository() throws Exception {
		JobRepositoryFactoryBean jobRepositoryFactoryBean = new JobRepositoryFactoryBean();
		jobRepositoryFactoryBean.setDatabaseType("HDB");
		jobRepositoryFactoryBean.setDataSource(dataSource);
		jobRepositoryFactoryBean.setTransactionManager(getTransactionManager());
		jobRepositoryFactoryBean.setIncrementerFactory(new CustomDataFieldMaxValueIncrementerFactory(dataSource));
		jobRepositoryFactoryBean.setClobType(Types.NCLOB);
		jobRepositoryFactoryBean.afterPropertiesSet();
		return jobRepositoryFactoryBean.getObject();
	}

	@Override
	public PlatformTransactionManager getTransactionManager() {
		return new DataSourceTransactionManager(dataSource);
	}
	
	@Bean
    public JobOperator jobOperator() {
        SimpleJobOperator jobOperator = new SimpleJobOperator();
        jobOperator.setJobExplorer(jobExplorer);
        jobOperator.setJobLauncher(jobLauncher);
        jobOperator.setJobRegistry(jobRegistry);
        jobOperator.setJobRepository(jobRepository);
        return jobOperator;
    }
	
	@Bean
	public ApplicationContextFactory someJob() {
		return new GenericApplicationContextFactory(PeopleDataImporterJob.class);
	}
	
	@Bean
	public Collection<BatchConfigurer> configurers() {
		return new ArrayList<BatchConfigurer>() {{
			add(new AsyncBatchConfigurer(properties, dataSource, transactionManagerCustomizers));
		}};
		
	}
}
