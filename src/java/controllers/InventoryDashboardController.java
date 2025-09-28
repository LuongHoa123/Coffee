package controllers;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.User;
import dao.UserDAO;
import jakarta.servlet.annotation.WebServlet;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

/**
 * Inventory Dashboard Controller
 * Handles inventory dashboard operations and displays inventory statistics
 */
@WebServlet(name = "InventoryDashboardController", urlPatterns = {"/inventory/dashboard"})
public class InventoryDashboardController extends HttpServlet {
    
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        userDAO = new UserDAO();
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     * Displays the Inventory dashboard with comprehensive statistics
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // Check authentication and inventory role
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User currentUser = (User) session.getAttribute("user");
        
        // Check if user has Inventory role (RoleID = 3)
        if (currentUser.getRoleID() != 3) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Inventory privileges required.");
            return;
        }
        
        try {
            // Gather inventory dashboard statistics
            Map<String, Object> dashboardStats = new HashMap<>();
            
            // Ingredient Statistics
            int totalIngredients = getTotalIngredientsCount();
            int lowStockIngredients = getLowStockIngredientsCount();
            int criticalStockIngredients = getCriticalStockIngredientsCount();
            
            // Purchase Order Statistics
            int totalPurchaseOrders = getTotalPurchaseOrdersCount();
            int pendingPOs = getPendingPurchaseOrdersCount();
            int shippingPOs = getShippingPurchaseOrdersCount();
            
            // Supplier Statistics
            int totalSuppliers = getTotalSuppliersCount();
            int activeSuppliers = getActiveSuppliersCount();
            
            // Issues Statistics
            int totalIssues = getTotalIssuesCount();
            int pendingIssues = getPendingIssuesCount();
            int resolvedIssues = getResolvedIssuesCount();
            
            // Recent Data
            List<Map<String, Object>> lowStockItems = getLowStockItems(5);
            List<Map<String, Object>> recentPurchaseOrders = getRecentPurchaseOrders(5);
            List<Map<String, Object>> recentIssues = getRecentIssues(5);
            
            // Stock Status Distribution
            Map<String, Integer> stockStatus = new HashMap<>();
            stockStatus.put("inStock", totalIngredients - lowStockIngredients - criticalStockIngredients);
            stockStatus.put("lowStock", lowStockIngredients);
            stockStatus.put("criticalStock", criticalStockIngredients);
            
            // Purchase Order Status Distribution
            Map<String, Integer> poStatus = new HashMap<>();
            poStatus.put("total", totalPurchaseOrders);
            poStatus.put("pending", pendingPOs);
            poStatus.put("shipping", shippingPOs);
            poStatus.put("completed", totalPurchaseOrders - pendingPOs - shippingPOs);
            
            // Set attributes for the dashboard view
            request.setAttribute("totalIngredients", totalIngredients);
            request.setAttribute("lowStockIngredients", lowStockIngredients);
            request.setAttribute("criticalStockIngredients", criticalStockIngredients);
            
            request.setAttribute("totalPurchaseOrders", totalPurchaseOrders);
            request.setAttribute("pendingPOs", pendingPOs);
            request.setAttribute("shippingPOs", shippingPOs);
            
            request.setAttribute("totalSuppliers", totalSuppliers);
            request.setAttribute("activeSuppliers", activeSuppliers);
            
            request.setAttribute("totalIssues", totalIssues);
            request.setAttribute("pendingIssues", pendingIssues);
            request.setAttribute("resolvedIssues", resolvedIssues);
            
            request.setAttribute("lowStockItems", lowStockItems);
            request.setAttribute("recentPurchaseOrders", recentPurchaseOrders);
            request.setAttribute("recentIssues", recentIssues);
            
            request.setAttribute("stockStatus", stockStatus);
            request.setAttribute("poStatus", poStatus);
            
            // User information for display
            request.setAttribute("currentUser", currentUser);
            request.setAttribute("pageTitle", "Inventory Dashboard");
            request.setAttribute("page", "dashboard");
            
            // Forward to inventory dashboard view
            request.getRequestDispatcher("/WEB-INF/views/inventory/dashboard.jsp")
                   .forward(request, response);
                   
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error loading inventory dashboard data: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp")
                   .forward(request, response);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * Handles inventory dashboard actions
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // Check authentication and inventory role
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User currentUser = (User) session.getAttribute("user");
        
        if (currentUser.getRoleID() != 3) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Inventory privileges required.");
            return;
        }
        
        String action = request.getParameter("action");
        
        try {
            switch (action != null ? action : "") {
                case "refreshStats":
                    // Refresh dashboard statistics
                    doGet(request, response);
                    break;
                    
                case "updateStock":
                    handleStockUpdate(request, response);
                    break;
                    
                case "createPO":
                    // Redirect to create purchase order page
                    response.sendRedirect(request.getContextPath() + "/inventory/purchase-orders?action=create");
                    break;
                    
                default:
                    // Default to showing dashboard
                    doGet(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error processing request: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp")
                   .forward(request, response);
        }
    }
    
    /**
     * Handle stock update actions
     */
    private void handleStockUpdate(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String ingredientId = request.getParameter("ingredientId");
        String newQuantity = request.getParameter("newQuantity");
        
        try {
            if (ingredientId != null && newQuantity != null) {
                // TODO: Implement stock update in DAO
                request.setAttribute("successMessage", "Stock updated successfully");
            } else {
                request.setAttribute("errorMessage", "Invalid stock update parameters");
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error updating stock: " + e.getMessage());
        }
        
        // Redirect back to dashboard
        doGet(request, response);
    }
    
    // Mock methods - these should be implemented in proper DAO classes
    
    private int getTotalIngredientsCount() {
        // TODO: Implement with IngredientDAO
        return 20; // Mock data
    }
    
    private int getLowStockIngredientsCount() {
        // TODO: Implement - ingredients with stock < 10
        return 5; // Mock data
    }
    
    private int getCriticalStockIngredientsCount() {
        // TODO: Implement - ingredients with stock < 5
        return 2; // Mock data
    }
    
    private int getTotalPurchaseOrdersCount() {
        // TODO: Implement with PurchaseOrderDAO
        return 25; // Mock data
    }
    
    private int getPendingPurchaseOrdersCount() {
        // TODO: Implement - POs with status 'Pending'
        return 8; // Mock data
    }
    
    private int getShippingPurchaseOrdersCount() {
        // TODO: Implement - POs with status 'Shipping'
        return 5; // Mock data
    }
    
    private int getTotalSuppliersCount() {
        // TODO: Implement with SupplierDAO
        return 15; // Mock data
    }
    
    private int getActiveSuppliersCount() {
        // TODO: Implement - active suppliers
        return 12; // Mock data
    }
    
    private int getTotalIssuesCount() {
        // TODO: Implement with IssueDAO
        return 18; // Mock data
    }
    
    private int getPendingIssuesCount() {
        // TODO: Implement - issues with status 'Reported' or 'Under Investigation'
        return 6; // Mock data
    }
    
    private int getResolvedIssuesCount() {
        // TODO: Implement - issues with status 'Resolved'
        return 10; // Mock data
    }
    
    private List<Map<String, Object>> getLowStockItems(int limit) {
        // TODO: Implement with IngredientDAO
        List<Map<String, Object>> items = new java.util.ArrayList<>();
        
        // Mock data
        Map<String, Object> item1 = new HashMap<>();
        item1.put("name", "Cà phê Arabica hạt");
        item1.put("currentStock", 3.5);
        item1.put("unit", "kg");
        item1.put("supplier", "Highlands Coffee");
        items.add(item1);
        
        Map<String, Object> item2 = new HashMap<>();
        item2.put("name", "Sữa tươi nguyên kem");
        item2.put("currentStock", 8.0);
        item2.put("unit", "lít");
        item2.put("supplier", "TH True Milk");
        items.add(item2);
        
        return items;
    }
    
    private List<Map<String, Object>> getRecentPurchaseOrders(int limit) {
        // TODO: Implement with PurchaseOrderDAO
        List<Map<String, Object>> orders = new java.util.ArrayList<>();
        
        // Mock data
        Map<String, Object> order1 = new HashMap<>();
        order1.put("poId", "PO001");
        order1.put("supplier", "Highlands Coffee");
        order1.put("status", "Shipping");
        order1.put("createdAt", "2025-09-20");
        orders.add(order1);
        
        return orders;
    }
    
    private List<Map<String, Object>> getRecentIssues(int limit) {
        // TODO: Implement with IssueDAO
        List<Map<String, Object>> issues = new java.util.ArrayList<>();
        
        // Mock data
        Map<String, Object> issue1 = new HashMap<>();
        issue1.put("issueId", 1);
        issue1.put("ingredient", "Cà phê Robusta hạt");
        issue1.put("status", "Under Investigation");
        issue1.put("reportedBy", "Barista 01");
        issues.add(issue1);
        
        return issues;
    }

    /**
     * Returns a short description of the servlet.
     */
    @Override
    public String getServletInfo() {
        return "Inventory Dashboard Controller - Manages inventory dashboard operations";
    }
}