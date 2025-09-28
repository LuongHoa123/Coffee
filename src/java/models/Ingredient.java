package models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Model class for Ingredients table
 * Represents ingredients/raw materials used in coffee production
 */
public class Ingredient {
    private int ingredientID;
    private String name;
    private int unitID;
    private String unitName; // For display purposes
    private BigDecimal stockQuantity;
    private int supplierID;
    private String supplierName; // For display purposes
    private boolean isActive;
    private LocalDateTime createdAt;
    
    // Default constructor
    public Ingredient() {
    }
    
    // Constructor with parameters (without ID and createdAt)
    public Ingredient(String name, int unitID, BigDecimal stockQuantity, int supplierID, boolean isActive) {
        this.name = name;
        this.unitID = unitID;
        this.stockQuantity = stockQuantity;
        this.supplierID = supplierID;
        this.isActive = isActive;
    }
    
    // Constructor with all parameters
    public Ingredient(int ingredientID, String name, int unitID, BigDecimal stockQuantity, 
                     int supplierID, boolean isActive, LocalDateTime createdAt) {
        this.ingredientID = ingredientID;
        this.name = name;
        this.unitID = unitID;
        this.stockQuantity = stockQuantity;
        this.supplierID = supplierID;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }
    
    // Constructor for display with unit name and supplier name
    public Ingredient(int ingredientID, String name, String unitName, double stockQuantity, 
                     String supplierName, boolean isActive, LocalDateTime createdAt) {
        this.ingredientID = ingredientID;
        this.name = name;
        this.unitName = unitName;
        this.stockQuantity = new BigDecimal(stockQuantity);
        this.supplierName = supplierName;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getIngredientID() {
        return ingredientID;
    }

    public void setIngredientID(int ingredientID) {
        this.ingredientID = ingredientID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUnitID() {
        return unitID;
    }

    public void setUnitID(int unitID) {
        this.unitID = unitID;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public BigDecimal getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(BigDecimal stockQuantity) {
        this.stockQuantity = stockQuantity;
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
        return "Ingredient{" +
                "ingredientID=" + ingredientID +
                ", name='" + name + '\'' +
                ", unitID=" + unitID +
                ", stockQuantity=" + stockQuantity +
                ", supplierID=" + supplierID +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                '}';
    }
}