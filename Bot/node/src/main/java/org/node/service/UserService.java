package org.node.service;

import org.node.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    private final UserDao userDao;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public Mono<Void> addUser(String username, String password, String email) {
        return userDao.saveUser(username, password, email);
    }

    public Flux<String> getAllUsers() {
        return userDao.getAllUserNames();
    }
}
