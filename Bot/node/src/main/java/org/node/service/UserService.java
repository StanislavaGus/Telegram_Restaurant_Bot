package org.node.service;

import org.node.dao.UserDao;
import org.node.entity.User;
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
        return userDao.findUserByUsername(username)
                .flatMap(existingUser -> Mono.error(new IllegalArgumentException("User already exists")))
                .switchIfEmpty(userDao.saveUser(username, passwordEncoder.encode(password), email))
                .then();
    }

    public Mono<Boolean> authenticate(String username, String password) {
        return userDao.findUserByUsername(username)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")))
                .flatMap(user -> {
                    if (passwordEncoder.matches(password, user.getPassword())) {
                        return Mono.just(true);
                    } else {
                        return Mono.just(false);
                    }
                });
    }

    public Mono<Long> getUserIdByUsername(String username) {
        return userDao.findUserByUsername(username)
                .flatMap(user -> Mono.just(user.getId()));
    }

    public Flux<String> getAllUsers() {
        return userDao.getAllUserNames();
    }

    public Mono<Void> addUserPreference(Long userId, String preference) {
        return userDao.saveUserPreference(userId, preference);
    }

    public Flux<String> getUserPreferences(Long userId) {
        return userDao.findPreferencesByUserId(userId);
    }

    public Mono<Void> deleteUserPreference(Long userId, String preference) {
        return userDao.deleteUserPreference(userId, preference);
    }
}
