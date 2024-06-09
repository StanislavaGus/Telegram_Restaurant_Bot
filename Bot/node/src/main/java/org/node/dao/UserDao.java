package org.node.dao;

import org.node.entity.User;
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

    public Mono<User> findUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = $1";
        return r2dbcEntityTemplate.getDatabaseClient()
                .sql(sql)
                .bind("$1", username)
                .map(row -> {
                    User user = new User();
                    user.setId(row.get("id", Long.class));
                    user.setUsername(row.get("username", String.class));
                    user.setPassword(row.get("password", String.class));
                    user.setEmail(row.get("email", String.class));
                    return user;
                })
                .one();
    }

    public Flux<String> getAllUserNames() {
        String sql = "SELECT username FROM users";
        return r2dbcEntityTemplate.getDatabaseClient()
                .sql(sql)
                .map(row -> row.get("username", String.class))
                .all();
    }

    public Mono<Void> saveUserPreference(Long userId, String preference) {
        String sql = "INSERT INTO preferences (user_id, preference) VALUES ($1, $2)";
        return r2dbcEntityTemplate.getDatabaseClient()
                .sql(sql)
                .bind("$1", userId)
                .bind("$2", preference)
                .then();
    }

    public Flux<String> findPreferencesByUserId(Long userId) {
        String sql = "SELECT preference FROM preferences WHERE user_id = $1";
        return r2dbcEntityTemplate.getDatabaseClient()
                .sql(sql)
                .bind("$1", userId)
                .map(row -> row.get("preference", String.class))
                .all();
    }

    public Mono<Void> deleteUserPreference(Long userId, String preference) {
        String sql = "DELETE FROM preferences WHERE user_id = $1 AND preference = $2";
        return r2dbcEntityTemplate.getDatabaseClient()
                .sql(sql)
                .bind("$1", userId)
                .bind("$2", preference)
                .then();
    }
}
