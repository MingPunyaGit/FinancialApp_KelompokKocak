package org.example.finapp.services;

import org.example.finapp.models.User;

import java.util.HashMap;
import java.util.Map;

public class AuthService {
    private static final Map<String, User> users = new HashMap<>();
    private User currentUser;

    static {
        // Data user dummy (bisa diganti dengan database)
        users.put("admin", new User("admin", "admin123", "Administrator"));
        users.put("user1", new User("user1", "password1", "John Doe"));
    }

    public boolean login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            return true;
        }
        return false;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}