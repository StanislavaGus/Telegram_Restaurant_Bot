package org.node.controller;

import org.node.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/addUser")
    public Mono<Void> addUser(@RequestParam String username, @RequestParam String password, @RequestParam String email) {
        return userService.addUser(username, password, email);
    }

    @GetMapping("/allUsers")
    public Flux<String> getAllUsers() {
        return userService.getAllUsers();
    }
}
