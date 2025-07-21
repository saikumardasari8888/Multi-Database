package com.multi.database.configuration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DatabaseContextHolder {

    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();

    public static void setCurrentDatabase(String databaseName) {
        log.debug("Setting database context to: {}", databaseName);
        CONTEXT_HOLDER.set(databaseName);
    }

    public static String getCurrentDatabase() {
        return CONTEXT_HOLDER.get();
    }

    public static void clear() {
        log.debug("Clearing database context");
        CONTEXT_HOLDER.remove();
    }
}