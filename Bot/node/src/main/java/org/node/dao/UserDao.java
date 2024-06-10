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

    public Mono<Void> saveUserAllergy(Long userId, String allergy) {
        String sql = "INSERT INTO allergies (user_id, allergy) VALUES ($1, $2)";
        return r2dbcEntityTemplate.getDatabaseClient()
                .sql(sql)
                .bind("$1", userId)
                .bind("$2", allergy)
                .then();
    }

    public Flux<String> findAllergiesByUserId(Long userId) {
        String sql = "SELECT allergy FROM allergies WHERE user_id = $1";
        return r2dbcEntityTemplate.getDatabaseClient()
                .sql(sql)
                .bind("$1", userId)
                .map(row -> row.get("allergy", String.class))
                .all();
    }

    public Mono<Void> deleteUserAllergy(Long userId, String allergy) {
        String sql = "DELETE FROM allergies WHERE user_id = $1 AND allergy = $2";
        return r2dbcEntityTemplate.getDatabaseClient()
                .sql(sql)
                .bind("$1", userId)
                .bind("$2", allergy)
                .then();
    }

    public Mono<Void> saveVisit(Long userId, String restaurantId) {
        String sql = "INSERT INTO visits (user_id, restaurant_id, visited) VALUES ($1, $2, false)";
        return r2dbcEntityTemplate.getDatabaseClient()
                .sql(sql)
                .bind("$1", userId)
                .bind("$2", restaurantId)
                .then();
    }

    public Flux<String> findVisitsByUserId(Long userId) {
        String sql = "SELECT restaurant_id FROM visits WHERE user_id = $1";
        return r2dbcEntityTemplate.getDatabaseClient()
                .sql(sql)
                .bind("$1", userId)
                .map(row -> row.get("restaurant_id", String.class))
                .all();
    }

    public Mono<Void> updateVisitStatus(Long userId, String restaurantId, boolean visited) {
        String sql = "UPDATE visits SET visited = $1 WHERE user_id = $2 AND restaurant_id = $3";
        return r2dbcEntityTemplate.getDatabaseClient()
                .sql(sql)
                .bind("$1", visited)
                .bind("$2", userId)
                .bind("$3", restaurantId)
                .then();
    }

    public Mono<Void> deleteVisit(Long userId, String restaurantId) {
        String sql = "DELETE FROM visits WHERE user_id = $1 AND restaurant_id = $2";
        return r2dbcEntityTemplate.getDatabaseClient()
                .sql(sql)
                .bind("$1", userId)
                .bind("$2", restaurantId)
                .then();
    }

}