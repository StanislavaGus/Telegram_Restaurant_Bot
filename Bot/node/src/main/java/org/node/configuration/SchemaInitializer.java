package org.node.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;

@Configuration
public class SchemaInitializer {

    private final DatabaseClient databaseClient;

    @Autowired
    public SchemaInitializer(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @PostConstruct
    public void init() {
        createUsersTable()
                .then(createVisitsTable())
                .then(createAcceptableAllergiesTable())
                .then(createAvailablePreferencesTable())
                .then(createUserAllergiesTable())
                .then(createUserPreferencesTable())
                .subscribe();
    }

    private Mono<Void> createUsersTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id SERIAL PRIMARY KEY, " +
                "username VARCHAR(150) NOT NULL, " +
                "password VARCHAR(255) NOT NULL, " +
                "email VARCHAR(150) NOT NULL)";
        return databaseClient.sql(sql).then();
    }


    private Mono<Void> createVisitsTable() {
        String sql = "CREATE TABLE IF NOT EXISTS visits (" +
                "id SERIAL PRIMARY KEY, " +
                "user_id INTEGER NOT NULL, " +
                "restaurant_id VARCHAR(255) NOT NULL, " +
                "visited BOOLEAN DEFAULT FALSE, " +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE)";
        return databaseClient.sql(sql).then();
    }

    private Mono<Void> createAcceptableAllergiesTable() {
        String sql = "CREATE TABLE IF NOT EXISTS acceptable_allergies (" +
                "id SERIAL PRIMARY KEY, " +
                "allergy VARCHAR(255) NOT NULL)";
        return databaseClient.sql(sql).then();
    }

    private Mono<Void> createAvailablePreferencesTable() {
        String sql = "CREATE TABLE IF NOT EXISTS available_preferences (" +
                "id SERIAL PRIMARY KEY, " +
                "preference VARCHAR(255) NOT NULL)";
        return databaseClient.sql(sql).then();
    }

    private Mono<Void> createUserAllergiesTable() {
        String sql = "CREATE TABLE IF NOT EXISTS user_allergies (" +
                "id SERIAL PRIMARY KEY, " +
                "user_id INTEGER NOT NULL, " +
                "allergy VARCHAR(250) NOT NULL, " +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE)";
        return databaseClient.sql(sql).then();
    }

    private Mono<Void> createUserPreferencesTable() {
        String sql = "CREATE TABLE IF NOT EXISTS user_preferences (" +
                "id SERIAL PRIMARY KEY, " +
                "user_id INTEGER NOT NULL, " +
                "preference VARCHAR(250) NOT NULL, " +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE)";
        return databaseClient.sql(sql).then();
    }
}
