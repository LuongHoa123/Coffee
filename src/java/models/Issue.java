package models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Model class for Issues table
 * Represents issues with ingredients (damaged, expired, etc.)
 */
public class Issue {
    private int issueID;
    private int ingredientID;
    private BigDecimal quantity;
    private int statusID;
    private int createdBy;
    private int confirmedBy;
    private LocalDateTime createdAt;
    
    // Default constructor
    public Issue() {
    }
    
    // Constructor with parameters (without ID and createdAt)
    public Issue(int ingredientID, BigDecimal quantity, int statusID, int createdBy, int confirmedBy) {
        this.ingredientID = ingredientID;
        this.quantity = quantity;
        this.statusID = statusID;
        this.createdBy = createdBy;
        this.confirmedBy = confirmedBy;
    }
    
    // Constructor with all parameters
    public Issue(int issueID, int ingredientID, BigDecimal quantity, int statusID, 
                int createdBy, int confirmedBy, LocalDateTime createdAt) {
        this.issueID = issueID;
        this.ingredientID = ingredientID;
        this.quantity = quantity;
        this.statusID = statusID;
        this.createdBy = createdBy;
        this.confirmedBy = confirmedBy;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getIssueID() {
        return issueID;
    }

    public void setIssueID(int issueID) {
        this.issueID = issueID;
    }

    public int getIngredientID() {
        return ingredientID;
    }

    public void setIngredientID(int ingredientID) {
        this.ingredientID = ingredientID;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public int getStatusID() {
        return statusID;
    }

    public void setStatusID(int statusID) {
        this.statusID = statusID;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public int getConfirmedBy() {
        return confirmedBy;
    }

    public void setConfirmedBy(int confirmedBy) {
        this.confirmedBy = confirmedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Issue{" +
                "issueID=" + issueID +
                ", ingredientID=" + ingredientID +
                ", quantity=" + quantity +
                ", statusID=" + statusID +
                ", createdBy=" + createdBy +
                ", confirmedBy=" + confirmedBy +
                ", createdAt=" + createdAt +
                '}';
    }
}