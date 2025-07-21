package com.multi.database.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

@Slf4j
public class DynamicDataSourceRouter extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        String currentDatabase = DatabaseContextHolder.getCurrentDatabase();
        log.debug("Routing to database: {}", currentDatabase);
        return currentDatabase;
    }
}
