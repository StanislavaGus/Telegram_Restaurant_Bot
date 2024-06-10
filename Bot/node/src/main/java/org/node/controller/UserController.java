package org.node.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.node.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.node.service.FoursquareService;

@RestController
public class UserController {

    private final UserService userService;
    private final FoursquareService foursquareService;

    @Autowired
    public UserController(UserService userService, FoursquareService foursquareService) {
        this.userService = userService;
        this.foursquareService = foursquareService;
    }

    @PostMapping("/addUser")
    public Mono<Void> addUser(@RequestParam String username, @RequestParam String password, @RequestParam String email) {
        return userService.addUser(username, password, email);
    }

    @GetMapping("/allUsers")
    public Flux<String> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/login")
    public Mono<Boolean> login(@RequestParam String username, @RequestParam String password) {
        return userService.authenticate(username, password);
    }

    @GetMapping("/searchRestaurants")
    public Mono<JsonNode> searchRestaurants(@RequestParam String location,
                                            @RequestParam(required = false) String cuisine,
                                            @RequestParam(required = false) String keywords) {
        return foursquareService.searchRestaurants(location, cuisine, keywords);
    }
}
