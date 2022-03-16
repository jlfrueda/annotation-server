package org.clinbioinfosspa.mmp.server.repositories;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.util.Properties;

public class RepositoryFactory implements AutoCloseable {

    private static final String RESOURCE_FILE = "database.properties";

    private final HikariDataSource dataSource;

    public RepositoryFactory() throws IOException {
        var properties = getProperties();
        var dbUri = properties.getProperty("postgres.uri");
        var dbUsername = properties.getProperty("postgres.username");
        var dbPassword = properties.getProperty("postgres.password");
        var driverClassname = properties.getProperty("postgres.driver.classname");
        var config = new HikariConfig();
        config.setDriverClassName(driverClassname);
        config.setJdbcUrl(dbUri);
        config.setUsername(dbUsername);
        config.setPassword(dbPassword);
        config.addDataSourceProperty("cachePrepStmts" , "true");
        config.addDataSourceProperty("prepStmtCacheSize" , "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit" , "2048");
        dataSource = new HikariDataSource(config);
    }

    public Repository create() throws Exception {
        return new Repository(dataSource.getConnection());
    }

    @Override
    public void close() throws Exception {
        dataSource.close();
    }

    private Properties getProperties() throws IOException {
        var loader = Thread.currentThread().getContextClassLoader();
        var properties = new Properties();
        try (var stream = loader.getResourceAsStream(RESOURCE_FILE)) {
            properties.load(stream);
        }
        return properties;
    }
}
