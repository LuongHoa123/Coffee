package dao;

import dal.DBContext;
import models.User;
import java.sql.*;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * HR Dashboard Data Access Object
 * Handles database operations for HR dashboard
 */
public class HrDAO {
    
    private DBContext dbContext;
    
    public HrDAO() {
        this.dbContext = new DBContext();
    }
    
    /**
     * Get total number of users
     */
    public int getTotalUsersCount() {
        String sql = "SELECT COUNT(*) FROM Users";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * SIMPLE METHOD: Get ALL users without any filters for testing
     */
   
    /**
     * Get number of active users
     */
    public int getActiveUsersCount() {
        String sql = "SELECT COUNT(*) FROM Users WHERE IsActive = 1";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Get number of new users this month
     */
    public int getNewUsersThisMonth() {
        String sql = "SELECT COUNT(*) FROM Users WHERE MONTH(CreatedAt) = MONTH(CURDATE()) " +
                    "AND YEAR(CreatedAt) = YEAR(CURDATE())";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Get user distribution by role
     */
    public Map<String, Integer> getUsersByRole() {
        Map<String, Integer> roleDistribution = new HashMap<>();
        String sql = "SELECT s.Value as role, COUNT(*) as count " +
                    "FROM Users u " +
                    "JOIN Setting s ON u.RoleID = s.SettingID " +
                    "WHERE s.Type = 'Role' " +
                    "GROUP BY s.Value";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String role = rs.getString("role");
                int count = rs.getInt("count");
                roleDistribution.put(role != null ? role : "Unknown", count);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return roleDistribution;
    }
    
    /**
     * Get recent users
     */
    public List<User> getRecentUsers(int limit) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.UserID, u.Email, u.FullName, s.Value as role, u.CreatedAt, u.IsActive " +
                    "FROM Users u " +
                    "JOIN Setting s ON u.RoleID = s.SettingID " +
                    "WHERE s.Type = 'Role' " +
                    "ORDER BY u.CreatedAt DESC LIMIT ?";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("UserID"));
                user.setEmail(rs.getString("Email"));
                user.setFullName(rs.getString("FullName"));
                user.setRole(rs.getString("role"));
                user.setCreatedDate(rs.getTimestamp("CreatedAt"));
                user.setStatus(rs.getInt("IsActive") == 1 ? "ACTIVE" : "INACTIVE");
                
                users.add(user);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return users;
    }
    
    /**
     * Get user growth data for charts
     */
    public List<Map<String, Object>> getUserGrowthData(String period) {
        List<Map<String, Object>> growthData = new ArrayList<>();
        String sql;
        
        switch (period.toLowerCase()) {
            case "3months":
                sql = "SELECT DATE_FORMAT(CreatedAt, '%Y-%m') as period, COUNT(*) as count " +
                     "FROM Users WHERE CreatedAt >= DATE_SUB(CURDATE(), INTERVAL 3 MONTH) " +
                     "GROUP BY DATE_FORMAT(CreatedAt, '%Y-%m') ORDER BY period";
                break;
            case "1year":
                sql = "SELECT DATE_FORMAT(CreatedAt, '%Y-%m') as period, COUNT(*) as count " +
                     "FROM Users WHERE CreatedAt >= DATE_SUB(CURDATE(), INTERVAL 1 YEAR) " +
                     "GROUP BY DATE_FORMAT(CreatedAt, '%Y-%m') ORDER BY period";
                break;
            default: // 6months
                sql = "SELECT DATE_FORMAT(CreatedAt, '%Y-%m') as period, COUNT(*) as count " +
                     "FROM Users WHERE CreatedAt >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH) " +
                     "GROUP BY DATE_FORMAT(CreatedAt, '%Y-%m') ORDER BY period";
        }
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> dataPoint = new HashMap<>();
                dataPoint.put("period", rs.getString("period"));
                dataPoint.put("count", rs.getInt("count"));
                growthData.add(dataPoint);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return growthData;
    }
    
    /**
     * Get users logged in today
     */
    public int getUsersLoggedInToday() {
        String sql = "SELECT COUNT(DISTINCT user_id) FROM user_sessions " +
                    "WHERE DATE(last_activity) = CURDATE()";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            // If user_sessions table doesn't exist, return 0
            // This is optional functionality
        }
        
        return 0;
    }
    
    /**
     * Get users logged in this week
     */
    public int getUsersLoggedInThisWeek() {
        String sql = "SELECT COUNT(DISTINCT user_id) FROM user_sessions " +
                    "WHERE WEEK(last_activity) = WEEK(CURDATE()) " +
                    "AND YEAR(last_activity) = YEAR(CURDATE())";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            // If user_sessions table doesn't exist, return 0
        }
        
        return 0;
    }
    
    /**
     * Get users logged in this month
     */
    public int getUsersLoggedInThisMonth() {
        String sql = "SELECT COUNT(DISTINCT user_id) FROM user_sessions " +
                    "WHERE MONTH(last_activity) = MONTH(CURDATE()) " +
                    "AND YEAR(last_activity) = YEAR(CURDATE())";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            // If user_sessions table doesn't exist, return 0
        }
        
        return 0;
    }
    
   
   
    
    
    // ============================================
    // BUSINESS STATISTICS FOR COFFEE SHOP MANAGEMENT
    // ============================================
    
    /**
     * Get total number of shops
     */
    public int getTotalShopsCount() {
        String sql = "SELECT COUNT(*) FROM Shops";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Get number of active shops
     */
    public int getActiveShopsCount() {
        String sql = "SELECT COUNT(*) FROM Shops WHERE IsActive = 1";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Get total orders today
     */
    public int getTotalOrdersToday() {
        String sql = "SELECT COUNT(*) FROM Orders WHERE DATE(CreatedAt) = CURDATE()";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Get completed orders today
     */
    public int getCompletedOrdersToday() {
        String sql = "SELECT COUNT(*) FROM Orders o " +
                    "JOIN Setting s ON o.StatusID = s.SettingID " +
                    "WHERE s.Type = 'OrderStatus' AND s.Value = 'Completed' " +
                    "AND DATE(o.CreatedAt) = CURDATE()";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Get total number of ingredients
     */
    public int getTotalIngredientsCount() {
        String sql = "SELECT COUNT(*) FROM Ingredients WHERE IsActive = 1";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Get number of low stock ingredients (stock < 10)
     */
    public int getLowStockIngredientsCount() {
        String sql = "SELECT COUNT(*) FROM Ingredients WHERE IsActive = 1 AND StockQuantity < 10";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Get staff distribution by role (different from general user roles)
     */
    public Map<String, Integer> getStaffByRole() {
        Map<String, Integer> staffDistribution = new HashMap<>();
        String sql = "SELECT s.Value as role, COUNT(*) as count " +
                    "FROM Users u " +
                    "JOIN Setting s ON u.RoleID = s.SettingID " +
                    "WHERE s.Type = 'Role' AND u.IsActive = 1 " +
                    "AND s.Value IN ('Barista', 'Inventory', 'HR', 'Admin') " +
                    "GROUP BY s.Value";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String role = rs.getString("role");
                int count = rs.getInt("count");
                staffDistribution.put(role != null ? role : "Unknown", count);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return staffDistribution;
    }
    
    /**
     * Get user by ID with role information
     */
    public User getUserById(int userId) {
        String sql = "SELECT u.UserID, u.Email, u.FullName, u.CreatedAt, u.IsActive, " +
                    "s.Value as role, s.SettingID as roleId " +
                    "FROM Users u " +
                    "JOIN Setting s ON u.RoleID = s.SettingID " +
                    "WHERE u.UserID = ? AND s.Type = 'Role'";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setUserID(rs.getInt("UserID")); // Use setUserID
                user.setEmail(rs.getString("Email"));
                user.setFullName(rs.getString("FullName"));
                user.setCreatedDate(rs.getTimestamp("CreatedAt"));
                user.setStatus(rs.getInt("IsActive") == 1 ? "ACTIVE" : "INACTIVE");
                user.setRole(rs.getString("role"));
                user.setRoleID(rs.getInt("roleId"));
                
                return user;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Update user status (active/inactive)
     */
    public boolean updateUserStatus(int userId, boolean isActive) {
        String sql = "UPDATE Users SET IsActive = ? WHERE UserID = ?";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setBoolean(1, isActive);
            ps.setInt(2, userId);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
}