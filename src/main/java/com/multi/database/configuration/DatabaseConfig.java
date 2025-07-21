package com.multi.database.configuration;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseConfig {
    private List<DatabaseInfo> databases;
    private ConnectionPoolConfig connectionPool;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DatabaseInfo {
        private String name;
        private String url;
        private String username;
        private String password;
        private boolean primary;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConnectionPoolConfig {
        private int maximumPoolSize;
        private int minimumIdle;
        private long connectionTimeout;
        private long idleTimeout;
        private long maxLifetime;
    }
}
