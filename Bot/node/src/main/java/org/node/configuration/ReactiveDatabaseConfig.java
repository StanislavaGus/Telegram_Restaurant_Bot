package org.node.configuration;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import static io.r2dbc.spi.ConnectionFactoryOptions.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableR2dbcRepositories(basePackages = "org.node.repository")
public class ReactiveDatabaseConfig extends AbstractR2dbcConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(ReactiveDatabaseConfig.class);

    @Value("${spring.r2dbc.url}")
    private String url;

    @Value("${spring.r2dbc.username}")
    private String username;

    @Value("${spring.r2dbc.password}")
    private String password;

    @Override
    @Bean
    public ConnectionFactory connectionFactory() {
        logger.debug("Creating ConnectionFactory with URL: {}, Username: {}", url, username);
        return ConnectionFactories.get(ConnectionFactoryOptions.builder()
                .option(DRIVER, "postgresql")
                .option(PROTOCOL, "r2dbc")
                .option(HOST, "localhost")
                .option(PORT, 5432)
                .option(DATABASE, "postgres")
                .option(USER, username)
                .option(PASSWORD, password)
                .build());
    }
}