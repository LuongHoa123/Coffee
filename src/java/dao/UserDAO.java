package dao;

import dal.DBContext;
import models.User;
import java.sql.*;
import java.util.*;

/**
 * User List Data Access Object
 * Handles database operations for user list functionality
 */
public class UserDAO {
    
    private DBContext dbContext;
    
    public UserDAO() {
        this.dbContext = new DBContext();
    }
    
   
    /**
     * Get user by ID
     */
    public User getUserById(int userId) {
        String sql = "SELECT u.UserID, u.FullName, u.Email, u.PasswordHash, u.RoleID, " +
                    "u.IsActive, u.CreatedAt, u.Phone, u.Address, s.Value as RoleName " +
                    "FROM Users u " +
                    "JOIN Setting s ON u.RoleID = s.SettingID " +
                    "WHERE s.Type = 'Role' AND u.UserID = ?";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setUserID(rs.getInt("UserID"));
                user.setFullName(rs.getString("FullName"));
                user.setEmail(rs.getString("Email"));
                user.setPasswordHash(rs.getString("PasswordHash"));
                user.setRoleID(rs.getInt("RoleID"));
                user.setActive(rs.getBoolean("IsActive"));
                user.setPhone(rs.getString("Phone"));
                user.setAddress(rs.getString("Address"));
                user.setRole(rs.getString("RoleName"));
                
                // Set created date
                java.sql.Timestamp timestamp = rs.getTimestamp("CreatedAt");
                if (timestamp != null) {
                    user.setCreatedDate(timestamp.toLocalDateTime());
                }
                
                return user;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Get distinct roles
     */
    public List<String> getDistinctRoles() {
        List<String> roles = new ArrayList<>();
        String sql = "SELECT DISTINCT role FROM users WHERE role IS NOT NULL ORDER BY role";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String role = rs.getString("role");
                if (role != null && !role.trim().isEmpty()) {
                    roles.add(role);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Add default roles if none found
        if (roles.isEmpty()) {
            roles.addAll(Arrays.asList("USER", "ADMIN", "HR", "MANAGER"));
        }
        
        return roles;
    }
    
    /**
     * Search users for HR (only Inventory and Barista roles)
     */
    public List<User> searchUsersForHR(String searchTerm) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.UserID, u.FullName, u.Email, u.PasswordHash, u.RoleID, " +
                    "u.IsActive, u.CreatedAt, s.Value as RoleName " +
                    "FROM Users u " +
                    "JOIN Setting s ON u.RoleID = s.SettingID " +
                    "WHERE s.Type = 'Role' AND s.Value IN ('Inventory', 'Barista') " +
                    "AND (u.FullName LIKE ? OR u.Email LIKE ?) " +
                    "ORDER BY u.CreatedAt DESC";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchTerm + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setUserID(rs.getInt("UserID"));
                user.setFullName(rs.getString("FullName"));
                user.setEmail(rs.getString("Email"));
                user.setPasswordHash(rs.getString("PasswordHash"));
                user.setRoleID(rs.getInt("RoleID"));
                user.setActive(rs.getBoolean("IsActive"));
                user.setRole(rs.getString("RoleName"));
                
                // Set created date
                java.sql.Timestamp timestamp = rs.getTimestamp("CreatedAt");
                if (timestamp != null) {
                    user.setCreatedDate(timestamp.toLocalDateTime());
                }
                
                users.add(user);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return users;
    }

    /**
     * Advanced search users (original method)
     */
    public List<User> advancedSearchUsers(String searchTerm, String role, String status, 
                                         String dateFrom, String dateTo) {
        List<User> users = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        
        sql.append("SELECT user_id, username, email, full_name, role, created_date, status, phone, address ");
        sql.append("FROM users WHERE 1=1 ");
        
        List<Object> params = new ArrayList<>();
        
        // Add search filter
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql.append("AND (username LIKE ? OR email LIKE ? OR full_name LIKE ?) ");
            String searchPattern = "%" + searchTerm.trim() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }
        
        // Add role filter
        if (role != null && !role.trim().isEmpty() && !"all".equalsIgnoreCase(role)) {
            sql.append("AND role = ? ");
            params.add(role);
        }
        
        // Add status filter
        if (status != null && !status.trim().isEmpty() && !"all".equalsIgnoreCase(status)) {
            if ("active".equalsIgnoreCase(status)) {
                sql.append("AND (status = 'ACTIVE' OR status = 1) ");
            } else if ("inactive".equalsIgnoreCase(status)) {
                sql.append("AND (status = 'INACTIVE' OR status = 0) ");
            }
        }
        
        // Add date filters
        if (dateFrom != null && !dateFrom.trim().isEmpty()) {
            sql.append("AND created_date >= ? ");
            params.add(dateFrom);
        }
        
        if (dateTo != null && !dateTo.trim().isEmpty()) {
            sql.append("AND created_date <= ? ");
            params.add(dateTo + " 23:59:59");
        }
        
        sql.append("ORDER BY created_date DESC");
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            // Set parameters
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = createUserFromResultSet(rs);
                users.add(user);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return users;
    }
    
    /**
     * Get total users count
     */
    public int getTotalUsersCount() {
        String sql = "SELECT COUNT(*) FROM users";
        
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
     * Get active users count
     */
    public int getActiveUsersCount() {
        String sql = "SELECT COUNT(*) FROM users WHERE status = 'ACTIVE' OR status = 1";
        
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
     * Get inactive users count
     */
    public int getInactiveUsersCount() {
        String sql = "SELECT COUNT(*) FROM users WHERE status = 'INACTIVE' OR status = 0";
        
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
     * Get recent users count (within specified days)
     */
    public int getRecentUsersCount(int days) {
        String sql = "SELECT COUNT(*) FROM users WHERE created_date >= DATE_SUB(CURDATE(), INTERVAL ? DAY)";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, days);
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
     * Create User object from ResultSet
     */
    private User createUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        
        user.setUserID(rs.getInt("user_id"));
        user.setEmail(rs.getString("email"));
        user.setFullName(rs.getString("full_name"));
        user.setRole(rs.getString("role"));
        user.setPhone(rs.getString("phone"));
        user.setAddress(rs.getString("address"));
        
        // Handle created_date
        Timestamp createdDate = rs.getTimestamp("created_date");
        if (createdDate != null) {
            user.setCreatedAt(createdDate.toLocalDateTime());
        }
        
        // Handle status (could be string or int)
        Object statusObj = rs.getObject("status");
        if (statusObj != null) {
            if (statusObj instanceof String) {
                String statusStr = (String) statusObj;
                user.setActive("ACTIVE".equalsIgnoreCase(statusStr) || "1".equals(statusStr));
                user.setStatus("ACTIVE".equalsIgnoreCase(statusStr) || "1".equals(statusStr) ? 1 : 0);
            } else if (statusObj instanceof Integer) {
                int statusInt = (Integer) statusObj;
                user.setActive(statusInt == 1);
                user.setStatus(statusInt);
            }
        }
        
        return user;
    }
    
    /**
     * Get users with pagination
     */
    public List<User> getUsersWithPagination(int offset, int limit) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, username, email, full_name, role, created_date, status, phone, address " +
                    "FROM users ORDER BY user_id DESC LIMIT ? OFFSET ?";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                users.add(createUserFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return users;
    }
    
    /**
     * Get available roles for HR (only Inventory and Barista)
     */
    public List<String> getAvailableRolesForHR() {
        List<String> roles = new ArrayList<>();
        String sql = "SELECT Value FROM Setting WHERE Type = 'Role' AND Value IN ('Inventory', 'Barista') ORDER BY Value";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                roles.add(rs.getString("Value"));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return roles;
    }
    
    /**
     * Get Role ID by Role Name
     */
    public int getRoleIdByName(String roleName) {
        String sql = "SELECT SettingID FROM Setting WHERE Type = 'Role' AND Value = ?";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, roleName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("SettingID");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0; // Default role ID if not found
    }
    
    /**
     * Get total user count
     */
    public int getTotalUserCount() {
        String sql = "SELECT COUNT(*) as total FROM Users";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Search users with pagination
     */
    public List<User> searchUsersWithPagination(String searchTerm, String role, String status, 
                                               String dateFrom, String dateTo, int offset, int limit) {
        List<User> users = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        
        sql.append("SELECT user_id, username, email, full_name, role, created_date, status, phone, address ");
        sql.append("FROM users WHERE 1=1 ");
        
        List<Object> params = new ArrayList<>();
        
        // Add search filter
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql.append("AND (username LIKE ? OR email LIKE ? OR full_name LIKE ?) ");
            String searchPattern = "%" + searchTerm.trim() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }
        
        // Add role filter
        if (role != null && !role.trim().isEmpty() && !"all".equalsIgnoreCase(role)) {
            sql.append("AND role = ? ");
            params.add(role);
        }
        
        // Add status filter
        if (status != null && !status.trim().isEmpty() && !"all".equalsIgnoreCase(status)) {
            if ("active".equalsIgnoreCase(status)) {
                sql.append("AND status = 1 ");
            } else if ("inactive".equalsIgnoreCase(status)) {
                sql.append("AND status = 0 ");
            }
        }
        
        // Add date filter
        if (dateFrom != null && !dateFrom.trim().isEmpty()) {
            sql.append("AND created_date >= ? ");
            params.add(dateFrom);
        }
        
        if (dateTo != null && !dateTo.trim().isEmpty()) {
            sql.append("AND created_date <= ? ");
            params.add(dateTo + " 23:59:59");
        }
        
        sql.append("ORDER BY user_id DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                users.add(createUserFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return users;
    }
    
    /**
     * Get search result count
     */
    public int getSearchResultCount(String searchTerm, String role, String status, 
                                   String dateFrom, String dateTo) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) as total FROM users WHERE 1=1 ");
        
        List<Object> params = new ArrayList<>();
        
        // Add search filter
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql.append("AND (username LIKE ? OR email LIKE ? OR full_name LIKE ?) ");
            String searchPattern = "%" + searchTerm.trim() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }
        
        // Add role filter
        if (role != null && !role.trim().isEmpty() && !"all".equalsIgnoreCase(role)) {
            sql.append("AND role = ? ");
            params.add(role);
        }
        
        // Add status filter
        if (status != null && !status.trim().isEmpty() && !"all".equalsIgnoreCase(status)) {
            if ("active".equalsIgnoreCase(status)) {
                sql.append("AND status = 1 ");
            } else if ("inactive".equalsIgnoreCase(status)) {
                sql.append("AND status = 0 ");
            }
        }
        
        // Add date filter
        if (dateFrom != null && !dateFrom.trim().isEmpty()) {
            sql.append("AND created_date >= ? ");
            params.add(dateFrom);
        }
        
        if (dateTo != null && !dateTo.trim().isEmpty()) {
            sql.append("AND created_date <= ? ");
            params.add(dateTo + " 23:59:59");
        }
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Get all users for HR (only Inventory and Barista roles)
     */
    public List<User> getAllUsersForHR() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.UserID, u.FullName, u.Email, u.PasswordHash, u.RoleID, " +
                    "u.IsActive, u.CreatedAt, u.Phone, u.Address, s.Value as RoleName " +
                    "FROM Users u " +
                    "JOIN Setting s ON u.RoleID = s.SettingID " +
                    "WHERE s.Type = 'Role' AND s.Value IN ('Inventory', 'Barista') " +
                    "ORDER BY u.CreatedAt DESC";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setUserID(rs.getInt("UserID"));
                user.setFullName(rs.getString("FullName"));
                user.setEmail(rs.getString("Email"));
                user.setPasswordHash(rs.getString("PasswordHash"));
                user.setRoleID(rs.getInt("RoleID"));
                user.setActive(rs.getBoolean("IsActive"));
                user.setPhone(rs.getString("Phone"));
                user.setAddress(rs.getString("Address"));
                user.setRole(rs.getString("RoleName"));
                
                // Set created date
                java.sql.Timestamp timestamp = rs.getTimestamp("CreatedAt");
                if (timestamp != null) {
                    user.setCreatedDate(timestamp.toLocalDateTime());
                }
                
                users.add(user);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return users;
    }
    
    /**
     * Get all users (original method)
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, username, email, full_name, role, created_date, status, phone, address " +
                    "FROM users ORDER BY user_id DESC";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                users.add(createUserFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return users;
    }
    
    /**
     * Get active user count
     */
    public int getActiveUserCount() {
        String sql = "SELECT COUNT(*) as total FROM Users WHERE IsActive = 1";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Get inactive user count
     */
    public int getInactiveUserCount() {
        String sql = "SELECT COUNT(*) as total FROM Users WHERE IsActive = 0";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Get new users this month
     */
    public int getNewUsersThisMonth() {
        String sql = "SELECT COUNT(*) as total FROM Users WHERE MONTH(CreatedAt) = MONTH(CURDATE()) AND YEAR(CreatedAt) = YEAR(CURDATE())";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Get user count by role
     */
    public Map<String, Integer> getUserCountByRole() {
        Map<String, Integer> roleStats = new HashMap<>();
        String sql = "SELECT s.Value as RoleName, COUNT(u.UserID) as UserCount " +
                    "FROM Users u " +
                    "INNER JOIN Setting s ON u.RoleID = s.SettingID " +
                    "WHERE s.Type = 'Role' AND u.IsActive = 1 " +
                    "GROUP BY s.Value, s.SettingID";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String roleName = rs.getString("RoleName");
                int count = rs.getInt("UserCount");
                roleStats.put(roleName, count);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return roleStats;
    }
    
    /**
     * Check if email exists
     */
    public boolean isEmailExists(String email) {
        String sql = "SELECT COUNT(*) as total FROM users WHERE email = ?";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total") > 0;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Add new user
     */
    public boolean addUser(User user) {
        String sql = "INSERT INTO Users (FullName, Email, PasswordHash, RoleID, IsActive, Phone, Address, CreatedAt) VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setInt(4, user.getRoleID());
            ps.setBoolean(5, user.isActive());
            ps.setString(6, user.getPhone());
            ps.setString(7, user.getAddress());
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get user by email
     */
    public User getUserByEmail(String email) {
        String sql = "SELECT user_id, username, email, full_name, role, created_date, status, phone, address " +
                    "FROM users WHERE email = ?";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return createUserFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Update user with password
     */
    public boolean updateUserWithPassword(User user) {
        String sql = "UPDATE users SET full_name = ?, email = ?, password_hash = ?, role = ?, phone = ?, address = ? WHERE user_id = ?";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getRole());
            ps.setString(5, user.getPhone());
            ps.setString(6, user.getAddress());
            ps.setInt(7, user.getUserID());
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update user without password and without changing status
     */
    public boolean updateUser(User user) {
        String sql = "UPDATE Users SET FullName = ?, Email = ?, RoleID = ?, Phone = ?, Address = ? WHERE UserID = ?";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            System.out.println("DEBUG DAO: Executing SQL: " + sql);
            System.out.println("DEBUG DAO: Parameters - ID: " + user.getUserID() + 
                             ", Name: " + user.getFullName() + 
                             ", Email: " + user.getEmail() + 
                             ", RoleID: " + user.getRoleID() + 
                             ", Phone: " + user.getPhone() + 
                             ", Address: " + user.getAddress());
            
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setInt(3, user.getRoleID());
            ps.setString(4, user.getPhone());
            ps.setString(5, user.getAddress());
            ps.setInt(6, user.getUserID());
            
            int rowsAffected = ps.executeUpdate();
            System.out.println("DEBUG DAO: Rows affected: " + rowsAffected);
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.out.println("DEBUG DAO: SQL Exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update user including status (for admin operations)
     */
    public boolean updateUserWithStatus(User user) {
        String sql = "UPDATE Users SET FullName = ?, Email = ?, RoleID = ?, Phone = ?, Address = ?, IsActive = ? WHERE UserID = ?";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setInt(3, user.getRoleID());
            ps.setString(4, user.getPhone());
            ps.setString(5, user.getAddress());
            ps.setBoolean(6, user.isActive());
            ps.setInt(7, user.getUserID());
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Delete user (soft delete)
     */
    public boolean deleteUser(int userId) {
        String sql = "UPDATE Users SET IsActive = 0 WHERE UserID = ?";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update user status
     */
    public boolean updateUserStatus(int userId, boolean isActive) {
        String sql = "UPDATE Users SET IsActive = ? WHERE UserID = ?";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setBoolean(1, isActive);
            ps.setInt(2, userId);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get user login count (placeholder - implement based on your login tracking)
     */
    public int getUserLoginCount(int userId) {
        // This would need a login_logs table or similar
        // For now, return 0 as placeholder
        return 0;
    }
    
    /**
     * Get user last login (placeholder - implement based on your login tracking)
     */
    public java.time.LocalDateTime getUserLastLogin(int userId) {
        // This would need a login_logs table or similar
        // For now, return null as placeholder
        return null;
    }
    
    /**
     * Get recent users (latest registered users)
     * @param limit number of recent users to retrieve
     * @return list of recent users
     */
    public List<User> getRecentUsers(int limit) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.UserID, u.FullName, u.Email, u.PasswordHash, u.RoleID, " +
                    "u.IsActive, u.CreatedAt, u.Phone, u.Address, s.Value as RoleName " +
                    "FROM Users u " +
                    "LEFT JOIN Setting s ON u.RoleID = s.SettingID AND s.Type = 'Role' " +
                    "ORDER BY u.CreatedAt DESC " +
                    "LIMIT ?";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                User user = new User();
                user.setUserID(rs.getInt("UserID"));
                user.setFullName(rs.getString("FullName"));
                user.setEmail(rs.getString("Email"));
                user.setPasswordHash(rs.getString("PasswordHash"));
                user.setRoleID(rs.getInt("RoleID"));
                user.setActive(rs.getBoolean("IsActive"));
                user.setPhone(rs.getString("Phone"));
                user.setAddress(rs.getString("Address"));
                user.setRole(rs.getString("RoleName"));
                
                // Set created date
                java.sql.Timestamp timestamp = rs.getTimestamp("CreatedAt");
                if (timestamp != null) {
                    user.setCreatedAt(timestamp.toLocalDateTime());
                }
                
                users.add(user);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error getting recent users: " + e.getMessage());
        }
        
        return users;
    }
}