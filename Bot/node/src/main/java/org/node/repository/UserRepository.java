package org.node.repository;

import org.node.entity.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface UserRepository extends ReactiveCrudRepository<User, Long> {
    Mono<User> findByUsername(String username);

    Mono<User> findByEmail(String email);
    @Query("SELECT username FROM users")
    Flux<String> findAllUsernames();
}
