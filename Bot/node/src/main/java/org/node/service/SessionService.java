package org.node.service;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class SessionService {

    private final Set<Long> loggedInUsers = new HashSet<>();

    public boolean isUserLoggedIn(long chatId) {
        return loggedInUsers.contains(chatId);
    }

    public void logInUser(long chatId) {
        loggedInUsers.add(chatId);
    }

    public void logOutUser(long chatId) {
        loggedInUsers.remove(chatId);
    }
}
