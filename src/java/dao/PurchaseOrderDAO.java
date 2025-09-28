package dao;

import dal.DBContext;
import models.PurchaseOrder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PurchaseOrderDAO {
    
    private DBContext dbContext;
    
    public PurchaseOrderDAO() {
        this.dbContext = new DBContext();
    }
    
    /**
     * Get all purchase orders with supplier names and status
     */
    public List<PurchaseOrder> getAllPurchaseOrders() {
        List<PurchaseOrder> purchaseOrders = new ArrayList<>();
        String sql = """
            SELECT po.POID, po.ShopID, po.SupplierID, s.SupplierName, 
                   po.CreatedBy, po.StatusID, st.Value as Status, po.CreatedAt
            FROM PurchaseOrders po
            LEFT JOIN Suppliers s ON po.SupplierID = s.SupplierID
            LEFT JOIN Setting st ON po.StatusID = st.SettingID AND st.Type = 'POStatus'
            WHERE po.POID IS NOT NULL
            ORDER BY po.CreatedAt DESC
            """;
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                PurchaseOrder po = new PurchaseOrder(
                    rs.getInt("POID"),
                    rs.getInt("ShopID"),
                    rs.getInt("SupplierID"),
                    rs.getString("SupplierName"),
                    rs.getInt("CreatedBy"),
                    rs.getString("Status"),
                    rs.getTimestamp("CreatedAt").toLocalDateTime()
                );
                purchaseOrders.add(po);
            }
        } catch (SQLException e) {
            System.err.println("Error getting purchase orders: " + e.getMessage());
            e.printStackTrace();
        }
        
        return purchaseOrders;
    }
    
    /**
     * Get purchase orders with filters
     */
    public List<PurchaseOrder> getPurchaseOrdersWithFilters(String statusFilter, String supplierFilter, String searchKeyword) {
        List<PurchaseOrder> purchaseOrders = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT po.POID, po.ShopID, po.SupplierID, s.SupplierName, 
                   po.CreatedBy, po.StatusID, st.Value as Status, po.CreatedAt
            FROM PurchaseOrders po
            LEFT JOIN Suppliers s ON po.SupplierID = s.SupplierID
            LEFT JOIN Setting st ON po.StatusID = st.SettingID AND st.Type = 'POStatus'
            WHERE po.POID IS NOT NULL
            """);
        
        List<String> conditions = new ArrayList<>();
        List<Object> parameters = new ArrayList<>();
        
        // Status filter
        if (statusFilter != null && !statusFilter.trim().isEmpty() && !statusFilter.equals("all")) {
            conditions.add("LOWER(st.Value) LIKE ?");
            parameters.add("%" + statusFilter.toLowerCase() + "%");
        }
        
        // Supplier filter
        if (supplierFilter != null && !supplierFilter.trim().isEmpty() && !supplierFilter.equals("all")) {
            try {
                // Nếu supplierFilter là số (SupplierID)
                int supplierId = Integer.parseInt(supplierFilter);
                conditions.add("po.SupplierID = ?");
                parameters.add(supplierId);
            } catch (NumberFormatException e) {
                // Nếu supplierFilter là tên (fallback)
                conditions.add("LOWER(s.SupplierName) LIKE ?");
                parameters.add("%" + supplierFilter.toLowerCase() + "%");
            }
        }
        
        // Search keyword
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            conditions.add("(po.POID LIKE ? OR LOWER(s.SupplierName) LIKE ?)");
            parameters.add("%" + searchKeyword + "%");
            parameters.add("%" + searchKeyword.toLowerCase() + "%");
        }
        
        // Add conditions to SQL
        if (!conditions.isEmpty()) {
            sql.append(" AND ").append(String.join(" AND ", conditions));
        }
        
        sql.append(" ORDER BY po.CreatedAt DESC");
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            // Set parameters
            for (int i = 0; i < parameters.size(); i++) {
                ps.setObject(i + 1, parameters.get(i));
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PurchaseOrder po = new PurchaseOrder(
                        rs.getInt("POID"),
                        rs.getInt("ShopID"),
                        rs.getInt("SupplierID"),
                        rs.getString("SupplierName"),
                        rs.getInt("CreatedBy"),
                        rs.getString("Status"),
                        rs.getTimestamp("CreatedAt").toLocalDateTime()
                    );
                    purchaseOrders.add(po);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting filtered purchase orders: " + e.getMessage());
            e.printStackTrace();
        }
        
        return purchaseOrders;
    }
    
    /**
     * Get purchase order by ID
     */
    public PurchaseOrder getPurchaseOrderById(int poID) {
        String sql = """
            SELECT po.POID, po.ShopID, po.SupplierID, s.SupplierName, 
                   po.CreatedBy, po.StatusID, st.Value as Status, po.CreatedAt
            FROM PurchaseOrders po
            LEFT JOIN Suppliers s ON po.SupplierID = s.SupplierID
            LEFT JOIN Setting st ON po.StatusID = st.SettingID AND st.Type = 'POStatus'
            WHERE po.POID = ?
            """;
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, poID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new PurchaseOrder(
                        rs.getInt("POID"),
                        rs.getInt("ShopID"),
                        rs.getInt("SupplierID"),
                        rs.getString("SupplierName"),
                        rs.getInt("CreatedBy"),
                        rs.getString("Status"),
                        rs.getTimestamp("CreatedAt").toLocalDateTime()
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting purchase order by ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Count total purchase orders
     */
    public int getTotalPurchaseOrders() {
        String sql = "SELECT COUNT(*) FROM PurchaseOrders";
        
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting purchase orders: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Get purchase orders with pagination
     */
    public List<PurchaseOrder> getPurchaseOrdersPaginated(int offset, int limit, String statusFilter, String supplierFilter, String searchKeyword) {
        List<PurchaseOrder> purchaseOrders = getPurchaseOrdersWithFilters(statusFilter, supplierFilter, searchKeyword);
        
        // Simple pagination for now - in production, use SQL LIMIT/OFFSET
        int startIndex = Math.max(0, offset);
        int endIndex = Math.min(startIndex + limit, purchaseOrders.size());
        
        if (startIndex >= purchaseOrders.size()) {
            return new ArrayList<>();
        }
        
        return purchaseOrders.subList(startIndex, endIndex);
    }
    
    /**
     * Get all active suppliers for dropdown
     */
    public List<java.util.Map<String, Object>> getAllSuppliers() {
        List<java.util.Map<String, Object>> suppliers = new ArrayList<>();
        String sql = """
            SELECT SupplierID, SupplierName 
            FROM Suppliers 
            WHERE IsActive = 1 
            ORDER BY SupplierName
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
            System.err.println("Error getting suppliers: " + e.getMessage());
            e.printStackTrace();
        }
        
        return suppliers;
    }
}