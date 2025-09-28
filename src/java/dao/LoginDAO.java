package dao;

import dal.DBContext;
import models.User;
import models.Setting;
import utils.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User authentication and management
 */
public class LoginDAO extends DBContext {

    /**
     * Authenticate user by email and password
     * @param email User email
     * @param password Plain text password
     * @return User object if authentication successful, null otherwise
     */
    public User authenticate(String email, String password) {
        String sql = "SELECT u.UserID, u.FullName, u.Email, u.PasswordHash, u.RoleID, " +
                     "u.IsActive, u.CreatedAt, s.Value as RoleName " +
                     "FROM Users u " +
                     "LEFT JOIN Setting s ON u.RoleID = s.SettingID " +
                     "WHERE u.Email = ? AND u.IsActive = 1";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                String storedHash = rs.getString("PasswordHash");
                
                // Verify password
                if (PasswordUtil.verifyPassword(password, storedHash)) {
                    User user = new User();
                    user.setUserID(rs.getInt("UserID"));
                    user.setFullName(rs.getString("FullName"));
                    user.setEmail(rs.getString("Email"));
                    user.setPasswordHash(storedHash);
                    user.setRoleID(rs.getInt("RoleID"));
                    user.setActive(rs.getBoolean("IsActive"));
                    
                    Timestamp timestamp = rs.getTimestamp("CreatedAt");
                    if (timestamp != null) {
                        user.setCreatedAt(timestamp.toLocalDateTime());
                    }
                    
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in authenticate: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * Get user by email
     * @param email User email
     * @return User object if found, null otherwise
     */
    public User getUserByEmail(String email) {
        String sql = "SELECT UserID, FullName, Email, PasswordHash, RoleID, IsActive, CreatedAt " +
                     "FROM Users WHERE Email = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setUserID(rs.getInt("UserID"));
                user.setFullName(rs.getString("FullName"));
                user.setEmail(rs.getString("Email"));
                user.setPasswordHash(rs.getString("PasswordHash"));
                user.setRoleID(rs.getInt("RoleID"));
                user.setActive(rs.getBoolean("IsActive"));
                
                Timestamp timestamp = rs.getTimestamp("CreatedAt");
                if (timestamp != null) {
                    user.setCreatedAt(timestamp.toLocalDateTime());
                }
                
                return user;
            }
        } catch (SQLException e) {
            System.err.println("Error in getUserByEmail: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }



    

    /**
     * Update user password
     * @param userID User ID
     * @param newPassword New plain text password
     * @return true if password updated successfully, false otherwise
     */
    public boolean updatePassword(int userID, String newPassword) {
        String sql = "UPDATE Users SET PasswordHash = ? WHERE UserID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            String hashedPassword = PasswordUtil.hashPassword(newPassword);
            ps.setString(1, hashedPassword);
            ps.setInt(2, userID);
            
            int result = ps.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Error in updatePassword: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    /**
     * Get role name by role ID
     * @param roleID Role ID
     * @return Role name string
     */
    public String getRoleName(int roleID) {
        String sql = "SELECT Value FROM Setting WHERE SettingID = ? AND Type = 'Role'";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, roleID);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getString("Value");
            }
        } catch (SQLException e) {
            System.err.println("Error in getRoleName: " + e.getMessage());
            e.printStackTrace();
        }
        
        return "Unknown";
    }

    /**
     * Get all roles from Setting table
     * @return List of Setting objects representing roles
     */
    public List<Setting> getAllRoles() {
        List<Setting> roles = new ArrayList<>();
        String sql = "SELECT SettingID, Type, Value, Description, IsActive " +
                     "FROM Setting WHERE Type = 'Role' AND IsActive = 1";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Setting role = new Setting();
                role.setSettingID(rs.getInt("SettingID"));
                role.setType(rs.getString("Type"));
                role.setValue(rs.getString("Value"));
                role.setDescription(rs.getString("Description"));
                role.setActive(rs.getBoolean("IsActive"));
                
                roles.add(role);
            }
        } catch (SQLException e) {
            System.err.println("Error in getAllRoles: " + e.getMessage());
            e.printStackTrace();
        }
        
        return roles;
    }

    /**
     * Check if email already exists
     * @param email Email to check
     * @return true if email exists, false otherwise
     */
    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM Users WHERE Email = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error in emailExists: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    /**
     * Check if email exists and user is active
     * @param email Email to check
     * @return true if email exists and user is active, false otherwise
     */
    public boolean checkEmailExistsAndActive(String email) {
        String sql = "SELECT COUNT(*) FROM Users WHERE Email = ? AND IsActive = 1";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error in checkEmailExistsAndActive: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }


    
}