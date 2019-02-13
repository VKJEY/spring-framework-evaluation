package com.example.job.data.persistence.jobs;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.example.job.data.persistence.model.People;

@Component
public class PeopleDataImporterJob {
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	public DataSource dataSource;
	
	@Bean
	public Job importPeopleData(@Qualifier("oneStep") Step step) {
		return jobBuilderFactory.get("peopleDataImporter")
				.incrementer(new RunIdIncrementer())
				.start(step)
				.build();
	}
	
	@Bean
    public Step oneStep(ItemReader<People> reader, 
    		ItemProcessor<People, People> processor, 
    		ItemWriter<People> writer) {
        return stepBuilderFactory.get("oneStep")
            .<People, People> chunk(10)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .build();
    }
	
	@Bean
	public FlatFileItemReader<People> reader() {
		return new FlatFileItemReaderBuilder<People>()
				.name("personItemReader")
				.resource(new ClassPathResource("sample-data.csv"))
				.delimited()
				.names(new String[] {"firstName", "lastName", "city"})
				.fieldSetMapper(new BeanWrapperFieldSetMapper<People>() {{
					setTargetType(People.class);
				}}).build();
	}
	
	@Bean
    public JdbcBatchItemWriter<People> writer() {
        return new JdbcBatchItemWriterBuilder<People>()
            .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
            .sql("INSERT INTO people_1(first_name, last_name, full_name, city) VALUES (:firstName, :lastName, :fullName, :city)")
            .dataSource(dataSource)
            .build();
    }
	
	@Bean
	public ItemProcessor<People, People> processor() {
		return new ItemProcessor<People, People>() {

			@Override
			public People process(People people) throws Exception {
				people.setFullName(people.getFirstName() + ", " + people.getLastName());
				return people;
			}
		};
	}

}
