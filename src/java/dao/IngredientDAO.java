package dao;

import dal.DBContext;
import models.Ingredient;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class IngredientDAO {
    
    private DBContext dbContext;
    
    public IngredientDAO() {
        this.dbContext = new DBContext();
    }
    
    /**
     * Get all ingredients with unit names and supplier names
     */
    public List<Ingredient> getAllIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();
        String sql = """
            SELECT i.IngredientID, i.Name, i.UnitID, u.Value as UnitName, 
                   i.StockQuantity, i.SupplierID, s.SupplierName, 
                   i.IsActive, i.CreatedAt
            FROM Ingredients i
            LEFT JOIN Setting u ON i.UnitID = u.SettingID AND u.Type = 'Unit'
            LEFT JOIN Suppliers s ON i.SupplierID = s.SupplierID
            WHERE i.IngredientID IS NOT NULL
            ORDER BY i.Name ASC
            """;
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Ingredient ingredient = new Ingredient(
                    rs.getInt("IngredientID"),
                    rs.getString("Name"),
                    rs.getString("UnitName"),
                    rs.getBigDecimal("StockQuantity").doubleValue(),
                    rs.getString("SupplierName"),
                    rs.getBoolean("IsActive"),
                    rs.getTimestamp("CreatedAt").toLocalDateTime()
                );
                ingredients.add(ingredient);
            }
        } catch (SQLException e) {
            System.err.println("Error getting ingredients: " + e.getMessage());
            e.printStackTrace();
        }
        
        return ingredients;
    }
    
    /**
     * Get ingredients with filters
     */
    public List<Ingredient> getIngredientsWithFilters(String stockFilter, String supplierFilter, String unitFilter, String searchKeyword) {
        List<Ingredient> ingredients = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT i.IngredientID, i.Name, i.UnitID, u.Value as UnitName, 
                   i.StockQuantity, i.SupplierID, s.SupplierName, 
                   i.IsActive, i.CreatedAt
            FROM Ingredients i
            LEFT JOIN Setting u ON i.UnitID = u.SettingID AND u.Type = 'Unit'
            LEFT JOIN Suppliers s ON i.SupplierID = s.SupplierID
            WHERE i.IngredientID IS NOT NULL
            """);
        
        List<String> conditions = new ArrayList<>();
        List<Object> parameters = new ArrayList<>();
        
        // Stock level filter
        if (stockFilter != null && !stockFilter.trim().isEmpty() && !stockFilter.equals("all")) {
            switch (stockFilter) {
                case "low":
                    conditions.add("i.StockQuantity <= ?");
                    parameters.add(new BigDecimal("10"));
                    break;
                case "normal":
                    conditions.add("i.StockQuantity > ? AND i.StockQuantity <= ?");
                    parameters.add(new BigDecimal("10"));
                    parameters.add(new BigDecimal("50"));
                    break;
                case "high":
                    conditions.add("i.StockQuantity > ?");
                    parameters.add(new BigDecimal("50"));
                    break;
            }
        }
        
        // Supplier filter
        if (supplierFilter != null && !supplierFilter.trim().isEmpty() && !supplierFilter.equals("all")) {
            try {
                // Nếu supplierFilter là số (SupplierID)
                int supplierId = Integer.parseInt(supplierFilter);
                conditions.add("i.SupplierID = ?");
                parameters.add(supplierId);
            } catch (NumberFormatException e) {
                // Nếu supplierFilter là tên (fallback)
                conditions.add("LOWER(s.SupplierName) LIKE ?");
                parameters.add("%" + supplierFilter.toLowerCase() + "%");
            }
        }
        
        // Unit filter
        if (unitFilter != null && !unitFilter.trim().isEmpty() && !unitFilter.equals("all")) {
            conditions.add("LOWER(u.Value) LIKE ?");
            parameters.add("%" + unitFilter.toLowerCase() + "%");
        }
        
        // Search keyword
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            conditions.add("(LOWER(i.Name) LIKE ? OR LOWER(s.SupplierName) LIKE ?)");
            parameters.add("%" + searchKeyword.toLowerCase() + "%");
            parameters.add("%" + searchKeyword.toLowerCase() + "%");
        }
        
        // Add conditions to SQL
        if (!conditions.isEmpty()) {
            sql.append(" AND ").append(String.join(" AND ", conditions));
        }
        
        sql.append(" ORDER BY i.Name ASC");
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            // Set parameters
            for (int i = 0; i < parameters.size(); i++) {
                ps.setObject(i + 1, parameters.get(i));
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Ingredient ingredient = new Ingredient(
                        rs.getInt("IngredientID"),
                        rs.getString("Name"),
                        rs.getString("UnitName"),
                        rs.getBigDecimal("StockQuantity").doubleValue(),
                        rs.getString("SupplierName"),
                        rs.getBoolean("IsActive"),
                        rs.getTimestamp("CreatedAt").toLocalDateTime()
                    );
                    ingredients.add(ingredient);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting filtered ingredients: " + e.getMessage());
            e.printStackTrace();
        }
        
        return ingredients;
    }
    
    /**
     * Get ingredient by ID
     */
    public Ingredient getIngredientById(int ingredientID) {
        String sql = """
            SELECT i.IngredientID, i.Name, i.UnitID, u.Value as UnitName, 
                   i.StockQuantity, i.SupplierID, s.SupplierName, 
                   i.IsActive, i.CreatedAt
            FROM Ingredients i
            LEFT JOIN Setting u ON i.UnitID = u.SettingID AND u.Type = 'Unit'
            LEFT JOIN Suppliers s ON i.SupplierID = s.SupplierID
            WHERE i.IngredientID = ?
            """;
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, ingredientID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Ingredient(
                        rs.getInt("IngredientID"),
                        rs.getString("Name"),
                        rs.getString("UnitName"),
                        rs.getBigDecimal("StockQuantity").doubleValue(),
                        rs.getString("SupplierName"),
                        rs.getBoolean("IsActive"),
                        rs.getTimestamp("CreatedAt").toLocalDateTime()
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting ingredient by ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Count total ingredients
     */
    public int getTotalIngredients() {
        String sql = "SELECT COUNT(*) FROM Ingredients WHERE IsActive = 1";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting ingredients: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Get low stock ingredients count
     */
    public int getLowStockCount() {
        String sql = "SELECT COUNT(*) FROM Ingredients WHERE StockQuantity <= 10 AND IsActive = 1";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting low stock ingredients: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Get ingredients with pagination
     */
    public List<Ingredient> getIngredientsPaginated(int offset, int limit, String stockFilter, String supplierFilter, String unitFilter, String searchKeyword) {
        List<Ingredient> ingredients = getIngredientsWithFilters(stockFilter, supplierFilter, unitFilter, searchKeyword);
        
        // Simple pagination for now - in production, use SQL LIMIT/OFFSET
        int startIndex = Math.max(0, offset);
        int endIndex = Math.min(startIndex + limit, ingredients.size());
        
        if (startIndex >= ingredients.size()) {
            return new ArrayList<>();
        }
        
        return ingredients.subList(startIndex, endIndex);
    }
    
    /**
     * Get all active suppliers for dropdown
     */
    public List<java.util.Map<String, Object>> getAllSuppliers() {
        List<java.util.Map<String, Object>> suppliers = new ArrayList<>();
        String sql = """
            SELECT DISTINCT s.SupplierID, s.SupplierName 
            FROM Suppliers s
            INNER JOIN Ingredients i ON s.SupplierID = i.SupplierID 
            WHERE s.IsActive = 1 AND i.IsActive = 1
            ORDER BY s.SupplierName
            """;
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                java.util.Map<String, Object> supplier = new java.util.HashMap<>();
                supplier.put("id", rs.getInt("SupplierID"));
                supplier.put("name", rs.getString("SupplierName"));
                suppliers.add(supplier);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting suppliers for ingredients: " + e.getMessage());
            e.printStackTrace();
        }
        
        return suppliers;
    }
}