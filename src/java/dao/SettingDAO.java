package dao;

import dal.DBContext;
import models.Setting;
import java.sql.*;
import java.util.*;

/**
 * Setting Data Access Object
 * Handles database operations for Setting table
 */
public class SettingDAO {
    
    private DBContext dbContext;
    
    public SettingDAO() {
        this.dbContext = new DBContext();
    }
    
    /**
     * Get all settings
     */
    public List<Setting> getAllSettings() {
        List<Setting> settings = new ArrayList<>();
        String sql = "SELECT SettingID, Type, Value, Description, IsActive FROM Setting ORDER BY Type, Value";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Setting setting = new Setting();
                setting.setSettingID(rs.getInt("SettingID"));
                setting.setType(rs.getString("Type"));
                setting.setValue(rs.getString("Value"));
                setting.setDescription(rs.getString("Description"));
                setting.setActive(rs.getBoolean("IsActive"));
                settings.add(setting);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return settings;
    }
    
    /**
     * Get settings by type
     */
    public List<Setting> getSettingsByType(String type) {
        List<Setting> settings = new ArrayList<>();
        String sql = "SELECT SettingID, Type, Value, Description, IsActive FROM Setting WHERE Type = ? ORDER BY Value";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, type);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Setting setting = new Setting();
                setting.setSettingID(rs.getInt("SettingID"));
                setting.setType(rs.getString("Type"));
                setting.setValue(rs.getString("Value"));
                setting.setDescription(rs.getString("Description"));
                setting.setActive(rs.getBoolean("IsActive"));
                settings.add(setting);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return settings;
    }
    
    /**
     * Get setting by ID
     */
    public Setting getSettingById(int settingId) {
        String sql = "SELECT SettingID, Type, Value, Description, IsActive FROM Setting WHERE SettingID = ?";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, settingId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Setting setting = new Setting();
                setting.setSettingID(rs.getInt("SettingID"));
                setting.setType(rs.getString("Type"));
                setting.setValue(rs.getString("Value"));
                setting.setDescription(rs.getString("Description"));
                setting.setActive(rs.getBoolean("IsActive"));
                return setting;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Add new setting
     */
    public boolean addSetting(Setting setting) {
        String sql = "INSERT INTO Setting (Type, Value, Description, IsActive) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, setting.getType());
            ps.setString(2, setting.getValue());
            ps.setString(3, setting.getDescription());
            ps.setBoolean(4, setting.isActive());
            
            int result = ps.executeUpdate();
            
            if (result > 0) {
                // Get generated ID
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    setting.setSettingID(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Update existing setting
     */
    public boolean updateSetting(Setting setting) {
        String sql = "UPDATE Setting SET Type = ?, Value = ?, Description = ?, IsActive = ? WHERE SettingID = ?";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, setting.getType());
            ps.setString(2, setting.getValue());
            ps.setString(3, setting.getDescription());
            ps.setBoolean(4, setting.isActive());
            ps.setInt(5, setting.getSettingID());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Delete setting (soft delete - set IsActive = false)
     */
    public boolean deleteSetting(int settingId) {
        String sql = "UPDATE Setting SET IsActive = 0 WHERE SettingID = ?";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, settingId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Hard delete setting
     */
    public boolean hardDeleteSetting(int settingId) {
        String sql = "DELETE FROM Setting WHERE SettingID = ?";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, settingId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Get distinct setting types
     */
    public List<String> getSettingTypes() {
        List<String> types = new ArrayList<>();
        String sql = "SELECT DISTINCT Type FROM Setting ORDER BY Type";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                types.add(rs.getString("Type"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return types;
    }
    
    /**
     * Search settings by keyword
     */
    public List<Setting> searchSettings(String keyword, String type) {
        List<Setting> settings = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT SettingID, Type, Value, Description, IsActive FROM Setting WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (Value LIKE ? OR Description LIKE ?)");
            String searchPattern = "%" + keyword.trim() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
        }
        
        if (type != null && !type.trim().isEmpty() && !"all".equals(type)) {
            sql.append(" AND Type = ?");
            params.add(type);
        }
        
        sql.append(" ORDER BY Type, Value");
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Setting setting = new Setting();
                setting.setSettingID(rs.getInt("SettingID"));
                setting.setType(rs.getString("Type"));
                setting.setValue(rs.getString("Value"));
                setting.setDescription(rs.getString("Description"));
                setting.setActive(rs.getBoolean("IsActive"));
                settings.add(setting);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return settings;
    }
    
    /**
     * Get total settings count
     */
    public int getTotalSettingsCount() {
        String sql = "SELECT COUNT(*) as total FROM Setting";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Get settings count by type
     */
    public Map<String, Integer> getSettingsCountByType() {
        Map<String, Integer> counts = new HashMap<>();
        String sql = "SELECT Type, COUNT(*) as count FROM Setting GROUP BY Type ORDER BY Type";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                counts.put(rs.getString("Type"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return counts;
    }
    
    /**
     * Check if setting value exists for a type (for validation)
     */
    public boolean isValueExists(String type, String value, int excludeId) {
        String sql = "SELECT COUNT(*) FROM Setting WHERE Type = ? AND Value = ? AND SettingID != ?";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, type);
            ps.setString(2, value);
            ps.setInt(3, excludeId);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
}