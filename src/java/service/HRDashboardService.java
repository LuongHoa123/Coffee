package service;

import dao.HrDAO;
import models.User;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 * HR Dashboard Service
 * Business logic for HR dashboard operations
 */
public class HRDashboardService {
    
    private HrDAO hrDashboardDAO;
    
    public HRDashboardService() {
        this.hrDashboardDAO = new HrDAO();
    }
    
    /**
     * Get dashboard statistics for HR management
     */
    public Map<String, Object> getDashboardStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // Get user management statistics
            int totalUsers = hrDashboardDAO.getTotalUsersCount();
            int activeUsers = hrDashboardDAO.getActiveUsersCount();
            int inactiveUsers = totalUsers - activeUsers;
            int newUsersThisMonth = hrDashboardDAO.getNewUsersThisMonth();
            
            // Get user distribution by role
            Map<String, Integer> usersByRole = hrDashboardDAO.getUsersByRole();
            
            // Get recent users
            List<User> recentUsers = hrDashboardDAO.getRecentUsers(5);
            
            // Get user growth data for charts  
            String userGrowthDataJson = getUserGrowthDataJson("6months");
            
            // Get coffee shop business statistics
            Map<String, Object> businessStats = getBusinessStatistics();
            
            stats.put("totalUsers", totalUsers);
            stats.put("activeUsers", activeUsers);
            stats.put("inactiveUsers", inactiveUsers);
            stats.put("newUsersThisMonth", newUsersThisMonth);
            stats.put("usersByRole", usersByRole);
            stats.put("recentUsers", recentUsers);
            stats.put("userGrowthData", userGrowthDataJson);
            
            // Add business statistics
            stats.putAll(businessStats);
            
        } catch (Exception e) {
            e.printStackTrace();
            // Return default values on error
            stats.put("totalUsers", 0);
            stats.put("activeUsers", 0);
            stats.put("inactiveUsers", 0);
            stats.put("newUsersThisMonth", 0);
            stats.put("usersByRole", new HashMap<>());
            stats.put("recentUsers", List.of());
            stats.put("userGrowthData", List.of());
        }
        
        return stats;
    }
    
    /**
     * Get business statistics for coffee shop management
     */
    private Map<String, Object> getBusinessStatistics() {
        Map<String, Object> businessStats = new HashMap<>();
        
        try {
            // Get shop statistics
            int totalShops = hrDashboardDAO.getTotalShopsCount();
            int activeShops = hrDashboardDAO.getActiveShopsCount();
            
            // Get recent orders statistics
            int totalOrdersToday = hrDashboardDAO.getTotalOrdersToday();
            int completedOrdersToday = hrDashboardDAO.getCompletedOrdersToday();
            
            // Get inventory statistics
            int totalIngredients = hrDashboardDAO.getTotalIngredientsCount();
            int lowStockIngredients = hrDashboardDAO.getLowStockIngredientsCount();
            
            // Get staff distribution
            Map<String, Integer> staffByRole = hrDashboardDAO.getStaffByRole();
            
            businessStats.put("totalShops", totalShops);
            businessStats.put("activeShops", activeShops);
            businessStats.put("totalOrdersToday", totalOrdersToday);
            businessStats.put("completedOrdersToday", completedOrdersToday);
            businessStats.put("totalIngredients", totalIngredients);
            businessStats.put("lowStockIngredients", lowStockIngredients);
            businessStats.put("staffByRole", staffByRole);
            
        } catch (Exception e) {
            e.printStackTrace();
            // Return default values on error
            businessStats.put("totalShops", 0);
            businessStats.put("activeShops", 0);
            businessStats.put("totalOrdersToday", 0);
            businessStats.put("completedOrdersToday", 0);
            businessStats.put("totalIngredients", 0);
            businessStats.put("lowStockIngredients", 0);
            businessStats.put("staffByRole", new HashMap<>());
        }
        
        return businessStats;
    }
    
    /**
     * Get user growth data for specific period
     */
    public Map<String, Object> getUserGrowthData(String period) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<Map<String, Object>> growthData = hrDashboardDAO.getUserGrowthData(period);
            result.put("data", growthData);
            result.put("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("data", List.of());
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Get user growth data as JSON string for JSP
     */
    public String getUserGrowthDataJson(String period) {
        try {
            List<Map<String, Object>> growthData = hrDashboardDAO.getUserGrowthData(period);
            StringBuilder json = new StringBuilder("[");
            
            for (int i = 0; i < growthData.size(); i++) {
                Map<String, Object> item = growthData.get(i);
                if (i > 0) json.append(",");
                json.append("{");
                json.append("\"period\":\"").append(item.get("period")).append("\",");
                json.append("\"count\":").append(item.get("count"));
                json.append("}");
            }
            
            json.append("]");
            return json.toString();
            
        } catch (Exception e) {
            e.printStackTrace();
            return "[]";
        }
    }
    
    /**
     * Get user activity summary
     */
    public Map<String, Object> getUserActivitySummary() {
        Map<String, Object> summary = new HashMap<>();
        
        try {
            int usersLoggedInToday = hrDashboardDAO.getUsersLoggedInToday();
            int usersLoggedInThisWeek = hrDashboardDAO.getUsersLoggedInThisWeek();
            int usersLoggedInThisMonth = hrDashboardDAO.getUsersLoggedInThisMonth();
            
            summary.put("usersLoggedInToday", usersLoggedInToday);
            summary.put("usersLoggedInThisWeek", usersLoggedInThisWeek);
            summary.put("usersLoggedInThisMonth", usersLoggedInThisMonth);
            
        } catch (Exception e) {
            e.printStackTrace();
            summary.put("usersLoggedInToday", 0);
            summary.put("usersLoggedInThisWeek", 0);
            summary.put("usersLoggedInThisMonth", 0);
        }
        
        return summary;
    }
    
    /**
     * Get system health metrics
     */
    public Map<String, Object> getSystemHealthMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        try {
            // Calculate system health indicators
            int totalUsers = hrDashboardDAO.getTotalUsersCount();
            int activeUsers = hrDashboardDAO.getActiveUsersCount();
            
            double activeUserPercentage = totalUsers > 0 ? (double) activeUsers / totalUsers * 100 : 0;
            String systemStatus = activeUserPercentage > 80 ? "Healthy" : 
                                 activeUserPercentage > 50 ? "Warning" : "Critical";
            
            metrics.put("systemStatus", systemStatus);
            metrics.put("activeUserPercentage", activeUserPercentage);
            metrics.put("lastUpdate", new java.util.Date());
            
        } catch (Exception e) {
            e.printStackTrace();
            metrics.put("systemStatus", "Unknown");
            metrics.put("activeUserPercentage", 0.0);
            metrics.put("lastUpdate", new java.util.Date());
        }
        
        return metrics;
    }
}