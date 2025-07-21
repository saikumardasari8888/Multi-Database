package com.multi.database.service;

import com.multi.database.configuration.DatabaseConfig;
import com.multi.database.configuration.DatabaseConfigLoader;
import com.multi.database.configuration.DatabaseContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseService {

    private final DatabaseConfigLoader configLoader;
    private final DataSource multiDataSource;

    public List<String> getAllDatabaseNames() {
        DatabaseConfig config = configLoader.loadDatabaseConfig();
        return config.getDatabases().stream()
                .map(DatabaseConfig.DatabaseInfo::getName)
                .collect(Collectors.toList());
    }

    public boolean testDatabaseConnection(String databaseName) {
        try {
            DatabaseContextHolder.setCurrentDatabase(databaseName);
            try (Connection connection = multiDataSource.getConnection()) {
                boolean isValid = connection.isValid(5); // 5 seconds timeout
                log.info("Database {} connection test: {}", databaseName, isValid ? "SUCCESS" : "FAILED");
                return isValid;
            }
        } catch (SQLException e) {
            log.error("Failed to test connection for database: {}", databaseName, e);
            return false;
        } finally {
            DatabaseContextHolder.clear();
        }
    }

    public DatabaseConfig.DatabaseInfo getDatabaseInfo(String databaseName) {
        DatabaseConfig config = configLoader.loadDatabaseConfig();
        return config.getDatabases().stream()
                .filter(db -> db.getName().equals(databaseName))
                .findFirst()
                .orElse(null);
    }

    public String getPrimaryDatabase() {
        DatabaseConfig config = configLoader.loadDatabaseConfig();
        return config.getDatabases().stream()
                .filter(DatabaseConfig.DatabaseInfo::isPrimary)
                .findFirst()
                .map(DatabaseConfig.DatabaseInfo::getName)
                .orElseThrow(() -> new IllegalStateException("Primary database not found"));
    }

}
