package org.node.dao;

import org.springframework.beans.factory.annotation.Autowired;
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
        String sql = "INSERT INTO users (username, password, email) VALUES ($1, $2, $3)";
        return r2dbcEntityTemplate.getDatabaseClient()
                .sql(sql)
                .bind("$1", username)
                .bind("$2", password)
                .bind("$3", email)
                .then();
    }

    public Mono<String> findUserByUsername(String username) {
        String sql = "SELECT password FROM users WHERE username = $1";
        return r2dbcEntityTemplate.getDatabaseClient()
                .sql(sql)
                .bind("$1", username)
                .map(row -> row.get("password", String.class))
                .one();
    }

    public Flux<String> getAllUserNames() {
        String sql = "SELECT username FROM users";
        return r2dbcEntityTemplate.getDatabaseClient()
                .sql(sql)
                .map(row -> row.get("username", String.class))
                .all();
    }
}
