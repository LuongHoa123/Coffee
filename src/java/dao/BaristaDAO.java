package dao;

import dal.DBContext;
import java.sql.*;
import java.util.*;

/**
 * Barista Dashboard Data Access Object
 * Handles database operations for barista dashboard functionality
 */
public class BaristaDAO {
    
    private DBContext dbContext;
    
    public BaristaDAO() {
        this.dbContext = new DBContext();
    }
    
    /**
     * Get today's orders count
     */
    public int getTodayOrdersCount() {
        String sql = "SELECT COUNT(*) FROM Orders WHERE DATE(CreatedAt) = CURDATE()";
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error getting today's orders count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Get completed orders count for today
     */
    public int getCompletedOrdersCount() {
        String sql = "SELECT COUNT(*) FROM Orders o " +
                    "JOIN Setting s ON o.StatusID = s.SettingID " +
                    "WHERE s.Value = 'Completed' AND DATE(o.CreatedAt) = CURDATE()";
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error getting completed orders count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Get pending orders count for today
     */
    public int getPendingOrdersCount() {
        String sql = "SELECT COUNT(*) FROM Orders o " +
                    "JOIN Setting s ON o.StatusID = s.SettingID " +
                    "WHERE s.Value = 'New' AND DATE(o.CreatedAt) = CURDATE()";
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error getting pending orders count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Get preparing orders count for today
     */
    public int getPreparingOrdersCount() {
        String sql = "SELECT COUNT(*) FROM Orders o " +
                    "JOIN Setting s ON o.StatusID = s.SettingID " +
                    "WHERE s.Value = 'Preparing' AND DATE(o.CreatedAt) = CURDATE()";
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error getting preparing orders count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Get ready orders count for today
     */
    public int getReadyOrdersCount() {
        String sql = "SELECT COUNT(*) FROM Orders o " +
                    "JOIN Setting s ON o.StatusID = s.SettingID " +
                    "WHERE s.Value = 'Ready' AND DATE(o.CreatedAt) = CURDATE()";
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error getting ready orders count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Get total products count
     */
    public int getTotalProductsCount() {
        String sql = "SELECT COUNT(*) FROM Products";
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error getting total products count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Get available products count
     */
    public int getAvailableProductsCount() {
        String sql = "SELECT COUNT(*) FROM Products WHERE IsActive = 1";
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error getting available products count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Get reported issues count (all)
     */
    public int getReportedIssuesCount() {
        String sql = "SELECT COUNT(*) FROM Issues";
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error getting reported issues count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Get my reported issues count
     */
    public int getMyReportedIssuesCount(int userId) {
        String sql = "SELECT COUNT(*) FROM Issues WHERE CreatedBy = ?";
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting my reported issues count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Get total shops count
     */
    public int getTotalShopsCount() {
        String sql = "SELECT COUNT(*) FROM Shops";
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error getting total shops count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Get active shops count
     */
    public int getActiveShopsCount() {
        String sql = "SELECT COUNT(*) FROM Shops WHERE IsActive = 1";
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error getting active shops count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Get recent orders
     */
    public List<Map<String, Object>> getRecentOrders(int limit) {
        // TODO: Implement when database is ready
        List<Map<String, Object>> orders = new ArrayList<>();
        
        // Mock data based on SQL sample
        Map<String, Object> order1 = new HashMap<>();
        order1.put("orderID", 1);
        order1.put("shopName", "CoffeeLux - Chi nhánh Quận 1");
        order1.put("baristaName", "Phạm Thị Linh");
        order1.put("status", "Completed");
        order1.put("createdAt", "2025-09-21 08:30:00");
        order1.put("totalAmount", 60000.0);
        orders.add(order1);
        
        Map<String, Object> order2 = new HashMap<>();
        order2.put("orderID", 2);
        order2.put("shopName", "CoffeeLux - Chi nhánh Quận 1");
        order2.put("baristaName", "Hoàng Minh Tú");
        order2.put("status", "Ready");
        order2.put("createdAt", "2025-09-21 09:15:00");
        order2.put("totalAmount", 85000.0);
        orders.add(order2);
        
        Map<String, Object> order3 = new HashMap<>();
        order3.put("orderID", 3);
        order3.put("shopName", "CoffeeLux - Chi nhánh Quận 3");
        order3.put("baristaName", "Vũ Thị Nam");
        order3.put("status", "Preparing");
        order3.put("createdAt", "2025-09-21 09:45:00");
        order3.put("totalAmount", 135000.0);
        orders.add(order3);
        
        return orders;
    }
    
    /**
     * Get today's orders by status
     */
    public List<Map<String, Object>> getTodayOrdersByStatus() {
        // TODO: Implement when database is ready
        List<Map<String, Object>> statusData = new ArrayList<>();
        
        // Mock data
        Map<String, Object> completed = new HashMap<>();
        completed.put("status", "Completed");
        completed.put("count", 18);
        statusData.add(completed);
        
        Map<String, Object> pending = new HashMap<>();
        pending.put("status", "New");
        pending.put("count", 4);
        statusData.add(pending);
        
        Map<String, Object> preparing = new HashMap<>();
        preparing.put("status", "Preparing");
        preparing.put("count", 2);
        statusData.add(preparing);
        
        Map<String, Object> ready = new HashMap<>();
        ready.put("status", "Ready");
        ready.put("count", 1);
        statusData.add(ready);
        
        return statusData;
    }
    
    /**
     * Get popular products
     */
    public List<Map<String, Object>> getPopularProducts(int limit) {
        // TODO: Implement when database is ready
        List<Map<String, Object>> products = new ArrayList<>();
        
        // Mock data based on SQL sample
        Map<String, Object> product1 = new HashMap<>();
        product1.put("productName", "Americano");
        product1.put("totalSold", 15);
        product1.put("price", 35000.0);
        products.add(product1);
        
        Map<String, Object> product2 = new HashMap<>();
        product2.put("productName", "Caffe Latte");
        product2.put("totalSold", 12);
        product2.put("price", 55000.0);
        products.add(product2);
        
        Map<String, Object> product3 = new HashMap<>();
        product3.put("productName", "Cold Brew Original");
        product3.put("totalSold", 8);
        product3.put("price", 45000.0);
        products.add(product3);
        
        Map<String, Object> product4 = new HashMap<>();
        product4.put("productName", "Croissant");
        product4.put("totalSold", 6);
        product4.put("price", 25000.0);
        products.add(product4);
        
        Map<String, Object> product5 = new HashMap<>();
        product5.put("productName", "Chocolate Frappuccino");
        product5.put("totalSold", 5);
        product5.put("price", 70000.0);
        products.add(product5);
        
        return products;
    }
    
    /**
     * Get my recent issues
     */
    public List<Map<String, Object>> getMyRecentIssues(int userId, int limit) {
        // TODO: Implement when database is ready
        List<Map<String, Object>> issues = new ArrayList<>();
        
        // Mock data based on SQL sample
        Map<String, Object> issue1 = new HashMap<>();
        issue1.put("issueID", 1);
        issue1.put("ingredientName", "Cà phê Arabica hạt");
        issue1.put("quantity", 2.5);
        issue1.put("status", "Resolved");
        issue1.put("createdAt", "2025-09-20 14:30:00");
        issues.add(issue1);
        
        Map<String, Object> issue2 = new HashMap<>();
        issue2.put("issueID", 4);
        issue2.put("ingredientName", "Bột mì đa dụng");
        issue2.put("quantity", 3.0);
        issue2.put("status", "Resolved");
        issue2.put("createdAt", "2025-09-19 11:15:00");
        issues.add(issue2);
        
        return issues;
    }
    
    /**
     * Get low stock ingredients
     */
    public List<Map<String, Object>> getLowStockIngredients(int limit) {
        // TODO: Implement when database is ready
        List<Map<String, Object>> ingredients = new ArrayList<>();
        
        // Mock data based on SQL sample
        Map<String, Object> ingredient1 = new HashMap<>();
        ingredient1.put("name", "Syrup Hazelnut");
        ingredient1.put("stockQuantity", 10.0);
        ingredient1.put("unit", "bottle");
        ingredients.add(ingredient1);
        
        Map<String, Object> ingredient2 = new HashMap<>();
        ingredient2.put("name", "Bột chocolate");
        ingredient2.put("stockQuantity", 8.0);
        ingredient2.put("unit", "kg");
        ingredients.add(ingredient2);
        
        Map<String, Object> ingredient3 = new HashMap<>();
        ingredient3.put("name", "Đường nâu");
        ingredient3.put("stockQuantity", 15.0);
        ingredient3.put("unit", "kg");
        ingredients.add(ingredient3);
        
        return ingredients;
    }
    
    /**
     * Get today's revenue
     */
    public double getTodayRevenue() {
        // TODO: Implement when database is ready
        return 1250000.0; // Mock data - 1.25M VND
    }
    
    /**
     * Get dashboard statistics summary
     */
    public Map<String, Object> getDashboardStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // Order statistics
        stats.put("todayOrders", getTodayOrdersCount());
        stats.put("completedOrders", getCompletedOrdersCount());
        stats.put("pendingOrders", getPendingOrdersCount());
        stats.put("preparingOrders", getPreparingOrdersCount());
        stats.put("readyOrders", getReadyOrdersCount());
        
        // Product statistics
        stats.put("totalProducts", getTotalProductsCount());
        stats.put("availableProducts", getAvailableProductsCount());
        
        // Issue statistics
        stats.put("reportedIssues", getReportedIssuesCount());
        
        // Shop statistics
        stats.put("totalShops", getTotalShopsCount());
        stats.put("activeShops", getActiveShopsCount());
        
        // Revenue
        stats.put("todayRevenue", getTodayRevenue());
        
        return stats;
    }
    
    /**
     * Get dashboard data for specific user
     */
    public Map<String, Object> getDashboardDataForUser(int userId) {
        Map<String, Object> data = new HashMap<>();
        
        // Get basic statistics
        data.putAll(getDashboardStatistics());
        
        // Get user-specific data
        data.put("myReportedIssues", getMyReportedIssuesCount(userId));
        data.put("recentOrders", getRecentOrders(10));
        data.put("todayOrdersByStatus", getTodayOrdersByStatus());
        data.put("popularProducts", getPopularProducts(5));
        data.put("myRecentIssues", getMyRecentIssues(userId, 5));
        data.put("lowStockIngredients", getLowStockIngredients(5));
        
        // Order status distribution
        Map<String, Integer> orderStatus = new HashMap<>();
        orderStatus.put("total", getTodayOrdersCount());
        orderStatus.put("completed", getCompletedOrdersCount());
        orderStatus.put("pending", getPendingOrdersCount());
        orderStatus.put("preparing", getPreparingOrdersCount());
        orderStatus.put("ready", getReadyOrdersCount());
        data.put("orderStatus", orderStatus);
        
        return data;
    }
}