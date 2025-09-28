package models;

import java.time.LocalDateTime;

/**
 * Model class for Shops table
 * Represents coffee shop locations/branches
 */
public class Shop {
    private int shopID;
    private String shopName;
    private String address;
    private String phone;
    private boolean isActive;
    private LocalDateTime createdAt;
    
    // Default constructor
    public Shop() {
    }
    
    // Constructor with parameters (without ID and createdAt)
    public Shop(String shopName, String address, String phone, boolean isActive) {
        this.shopName = shopName;
        this.address = address;
        this.phone = phone;
        this.isActive = isActive;
    }
    
    // Constructor with all parameters
    public Shop(int shopID, String shopName, String address, String phone, boolean isActive, LocalDateTime createdAt) {
        this.shopID = shopID;
        this.shopName = shopName;
        this.address = address;
        this.phone = phone;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getShopID() {
        return shopID;
    }

    public void setShopID(int shopID) {
        this.shopID = shopID;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Shop{" +
                "shopID=" + shopID +
                ", shopName='" + shopName + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                '}';
    }
}