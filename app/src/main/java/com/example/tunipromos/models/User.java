package com.example.tunipromos.models;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String uid;
    private String email;
    private String name;
    private List<String> categories;
    private boolean notificationsEnabled;

    public User() {
        this.categories = new ArrayList<>();
        this.notificationsEnabled = true;
    }

    public User(String uid, String email, String name) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.categories = new ArrayList<>();
        this.notificationsEnabled = true;
    }

    // Getters et Setters
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<String> getCategories() { return categories; }
    public void setCategories(List<String> categories) {
        this.categories = categories != null ? categories : new ArrayList<>();
    }

    public boolean isNotificationsEnabled() { return notificationsEnabled; }
    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }
}