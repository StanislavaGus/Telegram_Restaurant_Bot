package org.node.repository;

import org.node.entity.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;


public interface UserRepository extends ReactiveCrudRepository<User, Long> {
    Mono<User> findByUsername(String username);// можно потом использовать в getUserIdByUsername UserService, но нужно изменить и UserDAO
}
