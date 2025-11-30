package com.example.tunipromos.models;

import com.google.firebase.Timestamp;

public class Promotion {
    private String id;
    private String title;
    private String description;
    private String imageUrl;
    private String category;
    private String merchantId;
    private String merchantName;
    private int discountPercentage;
    private double originalPrice;
    private double discountedPrice;
    private String location;
    private boolean isActive;
    private int viewCount;
    private int favoriteCount;
    private Timestamp startDate;
    private Timestamp endDate;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Constructeur vide requis pour Firebase
    public Promotion() {}

    // Constructeur simple (pour compatibilité avec votre code existant)
    public Promotion(String id, String title, String description, String imageUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.isActive = true;
        this.viewCount = 0;
        this.favoriteCount = 0;
    }

    // Constructeur complet
    public Promotion(String id, String title, String description, String imageUrl,
                     String category, String merchantName, int discountPercentage,
                     String location) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.category = category;
        this.merchantName = merchantName;
        this.discountPercentage = discountPercentage;
        this.location = location;
        this.isActive = true;
        this.viewCount = 0;
        this.favoriteCount = 0;
        this.createdAt = Timestamp.now();
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public String getCategory() { return category; }
    public String getMerchantId() { return merchantId; }
    public String getMerchantName() { return merchantName; }
    public int getDiscountPercentage() { return discountPercentage; }
    public double getOriginalPrice() { return originalPrice; }
    public double getDiscountedPrice() { return discountedPrice; }
    public String getLocation() { return location; }
    public boolean isActive() { return isActive; }
    public int getViewCount() { return viewCount; }
    public int getFavoriteCount() { return favoriteCount; }
    public Timestamp getStartDate() { return startDate; }
    public Timestamp getEndDate() { return endDate; }
    public Timestamp getCreatedAt() { return createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setCategory(String category) { this.category = category; }
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }
    public void setMerchantName(String merchantName) { this.merchantName = merchantName; }
    public void setDiscountPercentage(int discountPercentage) { this.discountPercentage = discountPercentage; }
    public void setOriginalPrice(double originalPrice) { this.originalPrice = originalPrice; }
    public void setDiscountedPrice(double discountedPrice) { this.discountedPrice = discountedPrice; }
    public void setLocation(String location) { this.location = location; }
    public void setActive(boolean active) { isActive = active; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }
    public void setFavoriteCount(int favoriteCount) { this.favoriteCount = favoriteCount; }
    public void setStartDate(Timestamp startDate) { this.startDate = startDate; }
    public void setEndDate(Timestamp endDate) { this.endDate = endDate; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    // Méthodes utiles
    public void incrementViewCount() {
        this.viewCount++;
    }

    public void incrementFavoriteCount() {
        this.favoriteCount++;
    }

    public boolean isExpired() {
        if (endDate == null) return false;
        return Timestamp.now().compareTo(endDate) > 0;
    }
}