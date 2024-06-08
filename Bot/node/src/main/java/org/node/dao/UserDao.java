package org.node.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class UserDao {

    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    @Autowired
    public UserDao(R2dbcEntityTemplate r2dbcEntityTemplate) {
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
    }

    public Mono<Void> saveUser(String username, String password, String email) {
        String sql = "INSERT INTO Users (username, password, email) VALUES (:username, :password, :email)";
        return r2dbcEntityTemplate.getDatabaseClient()
                .sql(sql)
                .bind("username", username)
                .bind("password", password)
                .bind("email", email)
                .then();
    }

    public Flux<String> getAllUserNames() {
        String sql = "SELECT username FROM Users";
        return r2dbcEntityTemplate.getDatabaseClient()
                .sql(sql)
                .map(row -> row.get("username", String.class))
                .all();
    }
}
