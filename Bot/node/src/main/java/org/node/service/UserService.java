package org.node.service;

import org.node.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    public Mono<Void> addUser(String username, String password, String email) {
        String encodedPassword = passwordEncoder.encode(password);
        return userDao.saveUser(username, encodedPassword, email);
    }

    public Mono<Boolean> authenticate(String username, String password) {
        return userDao.findUserByUsername(username)
                .flatMap(storedPassword -> {
                    if (passwordEncoder.matches(password, storedPassword)) {
                        return Mono.just(true);
                    } else {
                        return Mono.just(false);
                    }
                });
    }

    public Flux<String> getAllUsers() {
        return userDao.getAllUserNames();
    }
}
