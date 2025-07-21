package com.multi.database.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableJpaRepositories(
        basePackages = "com.multi.database.repo",
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "multiTransactionManager"
)
public class MultiDatabaseConfiguration {

    private final DatabaseConfigLoader configLoader;

    @Bean
    @Primary
    public DataSource multiDataSource() {
        DatabaseConfig config = configLoader.loadDatabaseConfig();

        Map<Object, Object> dataSources = new HashMap<>();
        DataSource primaryDataSource = null;

        for (DatabaseConfig.DatabaseInfo dbInfo : config.getDatabases()) {
            HikariDataSource dataSource = createDataSource(dbInfo, config.getConnectionPool());
            dataSources.put(dbInfo.getName(), dataSource);

            if (dbInfo.isPrimary()) {
                primaryDataSource = dataSource;
            }

            log.info("Configured database: {}", dbInfo.getName());
        }

        DynamicDataSourceRouter router = new DynamicDataSourceRouter();
        router.setTargetDataSources(dataSources);
        router.setDefaultTargetDataSource(primaryDataSource);
        router.afterPropertiesSet();

        return router;
    }

    private HikariDataSource createDataSource(DatabaseConfig.DatabaseInfo dbInfo,
                                              DatabaseConfig.ConnectionPoolConfig poolConfig) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(dbInfo.getUrl());
        hikariConfig.setUsername(dbInfo.getUsername());
        hikariConfig.setPassword(dbInfo.getPassword());
        hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");

        // Connection pool settings
        hikariConfig.setMaximumPoolSize(poolConfig.getMaximumPoolSize());
        hikariConfig.setMinimumIdle(poolConfig.getMinimumIdle());
        hikariConfig.setConnectionTimeout(poolConfig.getConnectionTimeout());
        hikariConfig.setIdleTimeout(poolConfig.getIdleTimeout());
        hikariConfig.setMaxLifetime(poolConfig.getMaxLifetime());

        // Additional MySQL optimizations
        hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("useSSL", "false");
        hikariConfig.addDataSourceProperty("allowPublicKeyRetrieval", "true");

        return new HikariDataSource(hikariConfig);
    }

    @Bean(name = "entityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(multiDataSource());
        em.setPackagesToScan("com.multi.database.model");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        //properties.setProperty("hibernate.hbm2ddl.auto", "validate");
        properties.setProperty("hibernate.hbm2ddl.auto", "update");
        properties.setProperty("hibernate.show_sql", "false");
        properties.setProperty("hibernate.format_sql", "true");
        em.setJpaProperties(properties);

        return em;
    }

    @Bean
    @Primary
    public PlatformTransactionManager multiTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }
}
