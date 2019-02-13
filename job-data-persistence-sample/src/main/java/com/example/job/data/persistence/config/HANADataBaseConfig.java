package com.example.job.data.persistence.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("!test")
@Component
public class HANADataBaseConfig {
	@Value("${hana.driverclass}")
	private String hanaDriverName;
	
	@Value("${hana.url}")
	private String hanaUrl;
	
	@Value("${hana.username}")
	private String hanaUserName;
	
	@Value("${hana.password}")
	private String hanaPassword;

	public String getHanaDriverName() {
		return hanaDriverName;
	}

	public String getHanaUrl() {
		return hanaUrl;
	}

	public String getHanaUserName() {
		return hanaUserName;
	}

	public String getHanaPassword() {
		return hanaPassword;
	}
}
