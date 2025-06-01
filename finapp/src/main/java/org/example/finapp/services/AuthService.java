package org.example.finapp.services;

import org.example.finapp.models.User;
import java.util.HashMap;
import java.util.Map;

public class AuthService {
    private static final Map<String, User> users = new HashMap<>();
    private static User currentUser;

    static {
        users.put("admin", new User("admin", "admin123", "Administrator"));
        users.put("user", new User("user", "user123", "Pengguna Biasa"));
    }

    public boolean register(String username, String password, String fullName) {
        if (users.containsKey(username)) {
            return false;
        }
        users.put(username, new User(username, password, fullName));
        return true;
    }

    public boolean login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            return true;
        }
        return false;
    }

    public boolean loginFromSession(String username) {
        User user = users.get(username);
        if (user != null) {
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
}