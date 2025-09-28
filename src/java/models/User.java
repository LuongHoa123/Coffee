package models;

import java.time.LocalDateTime;

/**
 * Model class for Users table
 * Represents user accounts with authentication and role information
 */
public class User {
    private int userID;
    private String fullName;
    private String email;
    private String passwordHash;
    private int roleID;
    private boolean isActive;
    private LocalDateTime createdAt;
    
    // Additional fields for HR Dashboard
    private String phone;
    private String address;
    private String role; // Role name (HR, Admin, etc.)
    private int status; // Status as integer (0 = inactive, 1 = active)
    private LocalDateTime createdDate;
    
    // Default constructor
    public User() {
    }
    
    // Constructor with parameters (without ID and createdAt)
    public User(String fullName, String email, String passwordHash, int roleID, boolean isActive) {
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.roleID = roleID;
        this.isActive = isActive;
    }
    
    // Constructor with all parameters
    public User(int userID, String fullName, String email, String passwordHash, int roleID, 
               boolean isActive, LocalDateTime createdAt) {
        this.userID = userID;
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.roleID = roleID;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public int getRoleID() {
        return roleID;
    }

    public void setRoleID(int roleID) {
        this.roleID = roleID;
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

    // Additional getters and setters for HR Dashboard fields
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    
    // Additional methods for HR Dashboard compatibility
    public int getUserId() {
        return this.userID;
    }
    
    public void setUserId(int userId) {
        this.userID = userId;
    }
    
    public String getUsername() {
        return this.email; // Using email as username for compatibility
    }
    
    public void setUsername(String username) {
        this.email = username;
    }
    
    // Method to set createdDate from Timestamp
    public void setCreatedDate(java.sql.Timestamp timestamp) {
        if (timestamp != null) {
            this.createdDate = timestamp.toLocalDateTime();
        }
    }
    
    // Method to set status from String
    public void setStatus(String statusStr) {
        if (statusStr != null) {
            switch (statusStr.toUpperCase()) {
                case "ACTIVE":
                case "1":
                    this.status = 1;
                    this.isActive = true;
                    break;
                case "INACTIVE":
                case "0":
                    this.status = 0;
                    this.isActive = false;
                    break;
                default:
                    this.status = 0;
                    this.isActive = false;
            }
        }
    }

    // Utility methods
    public String getFormattedCreatedDate() {
        if (createdDate != null) {
            return createdDate.toString().replace('T', ' ').substring(0, 16); // YYYY-MM-DD HH:mm
        }
        return "N/A";
    }
    
    public String getFormattedCreatedAt() {
        if (createdAt != null) {
            return createdAt.toString().replace('T', ' ').substring(0, 16); // YYYY-MM-DD HH:mm
        }
        return "N/A";
    }

    @Override
    public String toString() {
        return "User{" +
                "userID=" + userID +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", passwordHash='" + "[PROTECTED]" + '\'' +
                ", roleID=" + roleID +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                '}';
    }
}