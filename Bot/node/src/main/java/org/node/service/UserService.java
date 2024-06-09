package org.node.service;

import org.node.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public Mono<Void> addUser(String username, String password, String email) {
        String encodedPassword = passwordEncoder.encode(password);
        return userDao.saveUser(username, encodedPassword, email);
    }

    public Flux<String> getAllUsers() {
        return userDao.getAllUserNames();
    }

    public Mono<Boolean> authenticate(String username, String password) {
        return userDao.findUserByUsername(username)
                .map(storedPassword -> passwordEncoder.matches(password, storedPassword));
    }
}
