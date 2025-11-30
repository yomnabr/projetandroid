package com.example.tunipromos.models;

import com.google.firebase.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class User {
    private String uid;
    private String email;
    private String displayName;
    private String userType; // "consumer" ou "merchant"
    private String profileImageUrl;
    private UserPreferences preferences;
    private String businessName;
    private String businessAddress;
    private String businessPhone;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public User() {
        this.preferences = new UserPreferences();
    }

    public User(String uid, String email, String displayName, String userType) {
        this.uid = uid;
        this.email = email;
        this.displayName = displayName;
        this.userType = userType;
        this.preferences = new UserPreferences();
        this.createdAt = Timestamp.now();
    }

    // Getters
    public String getUid() { return uid; }
    public String getEmail() { return email; }
    public String getDisplayName() { return displayName; }
    public String getUserType() { return userType; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public UserPreferences getPreferences() { return preferences; }
    public String getBusinessName() { return businessName; }
    public String getBusinessAddress() { return businessAddress; }
    public String getBusinessPhone() { return businessPhone; }
    public Timestamp getCreatedAt() { return createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }

    // Setters
    public void setUid(String uid) { this.uid = uid; }
    public void setEmail(String email) { this.email = email; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setUserType(String userType) { this.userType = userType; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
    public void setPreferences(UserPreferences preferences) { this.preferences = preferences; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    public void setBusinessAddress(String businessAddress) { this.businessAddress = businessAddress; }
    public void setBusinessPhone(String businessPhone) { this.businessPhone = businessPhone; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    // Classe interne pour les préférences
    public static class UserPreferences {
        private List<String> categories;
        private boolean notificationsEnabled;
        private String location;
        private int notificationRadius; // en km
        private boolean emailNotifications;

        public UserPreferences() {
            this.categories = new ArrayList<>();
            this.notificationsEnabled = true;
            this.notificationRadius = 10;
            this.emailNotifications = false;
        }

        // Getters
        public List<String> getCategories() { return categories; }
        public boolean isNotificationsEnabled() { return notificationsEnabled; }
        public String getLocation() { return location; }
        public int getNotificationRadius() { return notificationRadius; }
        public boolean isEmailNotifications() { return emailNotifications; }

        // Setters
        public void setCategories(List<String> categories) { this.categories = categories; }
        public void setNotificationsEnabled(boolean notificationsEnabled) {
            this.notificationsEnabled = notificationsEnabled;
        }
        public void setLocation(String location) { this.location = location; }
        public void setNotificationRadius(int notificationRadius) {
            this.notificationRadius = notificationRadius;
        }
        public void setEmailNotifications(boolean emailNotifications) {
            this.emailNotifications = emailNotifications;
        }
    }
}