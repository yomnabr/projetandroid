package com.example.tunipromos.models;

public class Promotion {
    private String id;
    private String title;
    private String description;
    private String imageUrl;
    private String category;
    private String merchantName;
    private int discount; // Pourcentage de réduction
    private boolean active;
    private long timestamp;

    // Constructeur vide requis par Firebase
    public Promotion() {
        this.active = true;
        this.timestamp = System.currentTimeMillis();
    }

    // Constructeur utilisé par PromotionViewModel
    public Promotion(String id, String title, String description, String imageUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.active = true;
        this.timestamp = System.currentTimeMillis();
    }

    // Constructeur simple
    public Promotion(String title, String description, String imageUrl) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.active = true;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public String getCategory() { return category; }
    public String getMerchantName() { return merchantName; }
    public int getDiscount() { return discount; }
    public boolean isActive() { return active; }
    public long getTimestamp() { return timestamp; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setCategory(String category) { this.category = category; }
    public void setMerchantName(String merchantName) { this.merchantName = merchantName; }
    public void setDiscount(int discount) { this.discount = discount; }
    public void setActive(boolean active) { this.active = active; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}