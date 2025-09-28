package models;

import java.time.LocalDateTime;

/**
 * Model class for PurchaseOrders table
 * Represents purchase orders for buying ingredients from suppliers
 */
public class PurchaseOrder {
    private int poID;
    private int shopID;
    private int supplierID;
    private String supplierName; // For display purposes
    private int createdBy;
    private int statusID;
    private String status; // For display purposes
    private LocalDateTime createdAt;
    
    // Default constructor
    public PurchaseOrder() {
    }
    
    // Constructor with parameters (without ID and createdAt)
    public PurchaseOrder(int shopID, int supplierID, int createdBy, int statusID) {
        this.shopID = shopID;
        this.supplierID = supplierID;
        this.createdBy = createdBy;
        this.statusID = statusID;
    }
    
    // Constructor with all parameters
    public PurchaseOrder(int poID, int shopID, int supplierID, int createdBy, int statusID, LocalDateTime createdAt) {
        this.poID = poID;
        this.shopID = shopID;
        this.supplierID = supplierID;
        this.createdBy = createdBy;
        this.statusID = statusID;
        this.createdAt = createdAt;
    }
    
    // Constructor for display with supplier name and status
    public PurchaseOrder(int poID, int shopID, int supplierID, String supplierName, int createdBy, String status, LocalDateTime createdAt) {
        this.poID = poID;
        this.shopID = shopID;
        this.supplierID = supplierID;
        this.supplierName = supplierName;
        this.createdBy = createdBy;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getPoID() {
        return poID;
    }
    
    public int getPOID() {
        return poID;
    }

    public void setPoID(int poID) {
        this.poID = poID;
    }

    public int getShopID() {
        return shopID;
    }

    public void setShopID(int shopID) {
        this.shopID = shopID;
    }

    public int getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(int supplierID) {
        this.supplierID = supplierID;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "PurchaseOrder{" +
                "poID=" + poID +
                ", shopID=" + shopID +
                ", supplierID=" + supplierID +
                ", createdBy=" + createdBy +
                ", statusID=" + statusID +
                ", createdAt=" + createdAt +
                '}';
    }
}