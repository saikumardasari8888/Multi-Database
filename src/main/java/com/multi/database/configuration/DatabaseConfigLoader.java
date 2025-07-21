package com.multi.database.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
public class DatabaseConfigLoader {

    private static final String CONFIG_FILE = "databases-config.json";
    private final ObjectMapper objectMapper;

    public DatabaseConfigLoader() {
        this.objectMapper = new ObjectMapper();
    }

    public DatabaseConfig loadDatabaseConfig() {
        try {
            ClassPathResource resource = new ClassPathResource(CONFIG_FILE);
            try (InputStream inputStream = resource.getInputStream()) {
                DatabaseConfig config = objectMapper.readValue(inputStream, DatabaseConfig.class);
                log.info("Successfully loaded configuration for {} databases",
                        config.getDatabases().size());
                return config;
            }
        } catch (IOException e) {
            log.error("Failed to load database configuration from {}", CONFIG_FILE, e);
            throw new RuntimeException("Could not load database configuration", e);
        }
    }
}
