package controllers;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.HRDashboardService;
import models.User;
import dao.HrDAO;
import jakarta.servlet.annotation.WebServlet;
import java.util.Map;
import java.util.List;

/**
 * HR Dashboard Controller
 * Handles HR dashboard operations and displays dashboard statistics
 */
@WebServlet(name = "HRDashboardController", urlPatterns = {"/hr/dashboard"})
public class HRDashboardController extends HttpServlet {
    
    private HRDashboardService hrDashboardService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        hrDashboardService = new HRDashboardService();
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     * Displays the HR dashboard with statistics
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Temporarily disable authentication for testing
        // TODO: Re-enable authentication once redirect loop is fixed
        /*
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User currentUser = (User) session.getAttribute("user");
        
        // Check if user has HR role
        if (!isHRUser(currentUser)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. HR privileges required.");
            return;
        }
        */
        
        try {
            // Get dashboard statistics
            Map<String, Object> dashboardStats = hrDashboardService.getDashboardStatistics();
            
            // Set user management attributes for JSP
            request.setAttribute("totalUsers", dashboardStats.get("totalUsers"));
            request.setAttribute("activeUsers", dashboardStats.get("activeUsers"));
            request.setAttribute("inactiveUsers", dashboardStats.get("inactiveUsers"));
            request.setAttribute("newUsersThisMonth", dashboardStats.get("newUsersThisMonth"));
            request.setAttribute("usersByRole", dashboardStats.get("usersByRole"));
            request.setAttribute("recentUsers", dashboardStats.get("recentUsers"));
            request.setAttribute("userGrowthData", dashboardStats.get("userGrowthData"));
            
            // Set business statistics attributes for JSP
            request.setAttribute("totalShops", dashboardStats.get("totalShops"));
            request.setAttribute("activeShops", dashboardStats.get("activeShops"));
            request.setAttribute("totalOrdersToday", dashboardStats.get("totalOrdersToday"));
            request.setAttribute("completedOrdersToday", dashboardStats.get("completedOrdersToday"));
            request.setAttribute("totalIngredients", dashboardStats.get("totalIngredients"));
            request.setAttribute("lowStockIngredients", dashboardStats.get("lowStockIngredients"));
            request.setAttribute("staffByRole", dashboardStats.get("staffByRole"));
            
            // Set page attribute for layout
            request.setAttribute("page", "dashboard");
            
            // Forward to HR layout
            request.getRequestDispatcher("/WEB-INF/views/hr-layout.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading dashboard: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * Handles AJAX requests for dashboard data refresh
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Temporarily disable authentication for testing
        // TODO: Re-enable authentication once redirect loop is fixed
        /*
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Session expired\"}");
            return;
        }
        
        User currentUser = (User) session.getAttribute("user");
        if (!isHRUser(currentUser)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"error\":\"Access denied\"}");
            return;
        }
        */
        
        String action = request.getParameter("action");
        
        try {
            switch (action != null ? action : "") {
                case "refreshStats":
                    handleRefreshStats(request, response);
                    break;
                case "getUserGrowth":
                    handleGetUserGrowth(request, response);
                    break;
                case "test":
                    response.getWriter().write("{\"success\":true,\"message\":\"Connection OK\"}");
                    break;
                default:
                    System.out.println("Unknown action: " + action);
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\":\"Invalid action: " + action + "\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
    
    /**
     * Handle refresh dashboard statistics
     */
    private void handleRefreshStats(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        Map<String, Object> stats = hrDashboardService.getDashboardStatistics();
        
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"totalUsers\":").append(stats.get("totalUsers")).append(",");
        json.append("\"activeUsers\":").append(stats.get("activeUsers")).append(",");
        json.append("\"inactiveUsers\":").append(stats.get("inactiveUsers")).append(",");
        json.append("\"newUsersThisMonth\":").append(stats.get("newUsersThisMonth")).append(",");
        json.append("\"success\":true");
        json.append("}");
        
        response.getWriter().write(json.toString());
    }
    
    /**
     * Handle get user growth data
     */
    private void handleGetUserGrowth(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String period = request.getParameter("period");
        if (period == null) period = "6months";
        
        System.out.println("Getting user growth data for period: " + period);
        
        Map<String, Object> result = hrDashboardService.getUserGrowthData(period);
        System.out.println("Service returned: " + result);
        
        StringBuilder json = new StringBuilder();
        json.append("{");
        
        if (result.get("success").equals(true)) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> data = (List<Map<String, Object>>) result.get("data");
            
            json.append("\"growthData\":[");
            for (int i = 0; i < data.size(); i++) {
                Map<String, Object> item = data.get(i);
                if (i > 0) json.append(",");
                json.append("{");
                json.append("\"period\":\"").append(item.get("period")).append("\",");
                json.append("\"count\":").append(item.get("count"));
                json.append("}");
            }
            json.append("],");
            json.append("\"success\":true");
        } else {
            json.append("\"success\":false,");
            json.append("\"error\":\"").append(result.get("error")).append("\"");
        }
        
        json.append("}");
        
        response.getWriter().write(json.toString());
    }
    
    /**
     * Check if user has HR privileges
     */
    private boolean isHRUser(User user) {
        if (user == null) return false;
        
        // Check if user has HR or Admin role from the Setting table
        String userRole = user.getRole();
        return "HR".equalsIgnoreCase(userRole) || "Admin".equalsIgnoreCase(userRole);
    }

    /**
     * Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "HR Dashboard Controller - Manages HR dashboard operations";
    }
}