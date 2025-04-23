package org.node.configuration;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import static io.r2dbc.spi.ConnectionFactoryOptions.*;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.DialectResolver;
import org.springframework.data.r2dbc.dialect.PostgresDialect;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.binding.BindMarkersFactory;
import org.springframework.r2dbc.core.binding.BindMarkersFactoryResolver;


@Configuration
@EnableR2dbcRepositories(basePackages = "org.node.repository")
public class ReactiveDatabaseConfig extends AbstractR2dbcConfiguration {

    @Value("${spring.r2dbc.host:postgres-db}")
    private String host;

    @Value("${spring.r2dbc.port:5432}")
    private int port;

    @Value("${spring.r2dbc.username:userok}")
    private String username;

    @Value("${spring.r2dbc.password:12345}")
    private String password;

    @Value("${spring.r2dbc.database:postgres}")
    private String database;

    @Bean
    public ConnectionFactory connectionFactory() {
        PostgresqlConnectionConfiguration cfg = PostgresqlConnectionConfiguration.builder()
                .host(host)
                .port(port)
                .username(username)
                .password(password)
                .database(database)
                .build();
        return new PostgresqlConnectionFactory(cfg);
    }

    /**
     * Этот метод и даёт Spring Data R2DBC понять,
     * что мы работаем с PostgreSQL.
     */
    @Override
    public R2dbcDialect getDialect(ConnectionFactory connectionFactory) {
        return PostgresDialect.INSTANCE;
    }
}