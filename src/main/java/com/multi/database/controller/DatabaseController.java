package com.multi.database.controller;

import com.multi.database.configuration.DatabaseConfig;
import com.multi.database.service.DatabaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/database")
@RequiredArgsConstructor
public class DatabaseController {

    private final DatabaseService databaseService;

    @GetMapping("/list")
    public ResponseEntity<List<String>> getAllDatabases() {
        List<String> databases = databaseService.getAllDatabaseNames();
        return ResponseEntity.ok(databases);
    }

    @GetMapping("/primary")
    public ResponseEntity<String> getPrimaryDatabase() {
        String primaryDb = databaseService.getPrimaryDatabase();
        return ResponseEntity.ok(primaryDb);
    }

    @GetMapping("/info/{databaseName}")
    public ResponseEntity<DatabaseConfig.DatabaseInfo> getDatabaseInfo(
            @PathVariable String databaseName) {
        DatabaseConfig.DatabaseInfo info = databaseService.getDatabaseInfo(databaseName);
        if (info != null) {
            // Don't expose password in response
            DatabaseConfig.DatabaseInfo safeInfo = new DatabaseConfig.DatabaseInfo(
                    info.getName(), info.getUrl(), info.getUsername(), "***", info.isPrimary()
            );
            return ResponseEntity.ok(safeInfo);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/test/{databaseName}")
    public ResponseEntity<Map<String, Object>> testDatabaseConnection(
            @PathVariable String databaseName) {
        boolean isConnected = databaseService.testDatabaseConnection(databaseName);
        Map<String, Object> response = new HashMap<>();
        response.put("database", databaseName);
        response.put("connected", isConnected);
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test-all")
    public ResponseEntity<Map<String, Boolean>> testAllConnections() {
        List<String> databases = databaseService.getAllDatabaseNames();
        Map<String, Boolean> results = new HashMap<>();

        for (String db : databases) {
            results.put(db, databaseService.testDatabaseConnection(db));
        }

        return ResponseEntity.ok(results);
    }
}