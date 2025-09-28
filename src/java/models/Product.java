package models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Model class for Products table
 * Represents coffee products available for sale
 */
public class Product {
    private int productID;
    private String productName;
    private String description;
    private int categoryID;
    private BigDecimal price;
    private int supplierID;
    private boolean isActive;
    private LocalDateTime createdAt;
    
    // Default constructor
    public Product() {
    }
    
    // Constructor with parameters (without ID and createdAt)
    public Product(String productName, String description, int categoryID, BigDecimal price, 
                  int supplierID, boolean isActive) {
        this.productName = productName;
        this.description = description;
        this.categoryID = categoryID;
        this.price = price;
        this.supplierID = supplierID;
        this.isActive = isActive;
    }
    
    // Constructor with all parameters
    public Product(int productID, String productName, String description, int categoryID, 
                  BigDecimal price, int supplierID, boolean isActive, LocalDateTime createdAt) {
        this.productID = productID;
        this.productName = productName;
        this.description = description;
        this.categoryID = categoryID;
        this.price = price;
        this.supplierID = supplierID;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(int supplierID) {
        this.supplierID = supplierID;
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
        return "Product{" +
                "productID=" + productID +
                ", productName='" + productName + '\'' +
                ", description='" + description + '\'' +
                ", categoryID=" + categoryID +
                ", price=" + price +
                ", supplierID=" + supplierID +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                '}';
    }
}