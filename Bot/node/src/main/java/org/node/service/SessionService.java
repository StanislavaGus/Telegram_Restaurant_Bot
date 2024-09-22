package org.node.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class SessionService {

    private final Set<Long> loggedInUsers = new HashSet<>();
    private final Map<Long, Long> chatUserMap = new HashMap<>(); // Map chatId to userId

    public boolean isUserLoggedIn(long chatId) {
        return loggedInUsers.contains(chatId);
    }

    public void logInUser(long chatId, Long userId) {
        loggedInUsers.add(chatId);
        chatUserMap.put(chatId, userId);
    }

    public void logOutUser(long chatId) {
        loggedInUsers.remove(chatId);
        chatUserMap.remove(chatId);
    }

    public Long getUserId(long chatId) {
        return chatUserMap.get(chatId);
    }
}
