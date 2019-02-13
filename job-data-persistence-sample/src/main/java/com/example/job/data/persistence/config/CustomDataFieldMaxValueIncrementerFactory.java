package com.example.job.data.persistence.config;

import org.springframework.batch.item.database.support.DataFieldMaxValueIncrementerFactory;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.H2SequenceMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.HanaSequenceMaxValueIncrementer;

import javax.sql.DataSource;

public class CustomDataFieldMaxValueIncrementerFactory implements DataFieldMaxValueIncrementerFactory {

	private final DataSource dataSource;

	private String[] supportedTypes = { "H2", "HDB" };

	public CustomDataFieldMaxValueIncrementerFactory(final DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public DataFieldMaxValueIncrementer getIncrementer(String databaseType, String incrementerName) {
		if (databaseType == "H2") {
			return new H2SequenceMaxValueIncrementer(dataSource, incrementerName);
		} else if (databaseType == "HDB") {
			return new HanaSequenceMaxValueIncrementer(dataSource, incrementerName);
		}
		throw new IllegalArgumentException("databaseType argument was not on the approved list");
	}

	@Override
	public boolean isSupportedIncrementerType(String databaseType) {
		if ((databaseType == "H2") || (databaseType == "HDB")) {
			return true;
		}
		return false;
	}

	@Override
	public String[] getSupportedIncrementerTypes() {
		return supportedTypes;
	}
}
