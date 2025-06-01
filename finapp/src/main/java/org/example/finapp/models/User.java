package org.example.finapp.models;

public class User {
    private int id;
    private String username;
    private String password;
    private String fullName;

    public User(int id, String username, String password, String fullName) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; } // Di aplikasi nyata, ini harus di-hash
    public String getFullName() { return fullName; }
}