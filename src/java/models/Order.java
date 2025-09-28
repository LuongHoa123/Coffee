package models;

import java.time.LocalDateTime;

/**
 * Model class for Orders table
 * Represents customer orders for coffee products
 */
public class Order {
    private int orderID;
    private int shopID;
    private int createdBy;
    private int statusID;
    private LocalDateTime createdAt;
    
    // Default constructor
    public Order() {
    }
    
    // Constructor with parameters (without ID and createdAt)
    public Order(int shopID, int createdBy, int statusID) {
        this.shopID = shopID;
        this.createdBy = createdBy;
        this.statusID = statusID;
    }
    
    // Constructor with all parameters
    public Order(int orderID, int shopID, int createdBy, int statusID, LocalDateTime createdAt) {
        this.orderID = orderID;
        this.shopID = shopID;
        this.createdBy = createdBy;
        this.statusID = statusID;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getShopID() {
        return shopID;
    }

    public void setShopID(int shopID) {
        this.shopID = shopID;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public int getStatusID() {
        return statusID;
    }

    public void setStatusID(int statusID) {
        this.statusID = statusID;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderID=" + orderID +
                ", shopID=" + shopID +
                ", createdBy=" + createdBy +
                ", statusID=" + statusID +
                ", createdAt=" + createdAt +
                '}';
    }
}