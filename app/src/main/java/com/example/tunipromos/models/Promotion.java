package com.example.tunipromos.models;

public class Promotion {
    private String id;
    private String title;
    private String description;
    private String imageUrl;     // pour les anciennes promos ou URL en ligne
    private String base64Image;  // 🔹 pour les images venant de la galerie
    private String category;
    private String merchantName;
    private int discount; // Pourcentage de réduction
    private boolean active;
    private long timestamp;
    private String providerId; // ID du fournisseur (User UID)

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
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getBase64Image() {
        return base64Image;
    }

    public String getCategory() {
        return category;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public String getProviderId() {
        return providerId;
    }

    public int getDiscount() {
        return discount;
    }

    public boolean isActive() {
        return active;
    }

    public long getTimestamp() {
        return timestamp;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
