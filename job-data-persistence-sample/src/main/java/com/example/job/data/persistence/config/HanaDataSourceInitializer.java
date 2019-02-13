package com.example.job.data.persistence.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
@Profile("!test")
public class HanaDataSourceInitializer {
	@Autowired
    private HANADataBaseConfig hanaDataBaseConfiguration;

    @Bean
    @Primary
    public DataSource dataSource() {
    	
    	DriverManagerDataSource dataSource = new DriverManagerDataSource();
    	dataSource.setDriverClassName(hanaDataBaseConfiguration.getHanaDriverName());
    	dataSource.setUrl(hanaDataBaseConfiguration.getHanaUrl());
    	dataSource.setUsername(hanaDataBaseConfiguration.getHanaUserName());
    	dataSource.setPassword(hanaDataBaseConfiguration.getHanaPassword());
		return dataSource;        
    }
}
