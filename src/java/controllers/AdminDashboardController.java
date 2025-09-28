package controllers;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.User;
import dao.UserDAO;
import dao.HrDAO;
import jakarta.servlet.annotation.WebServlet;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Admin Dashboard Controller
 * Handles admin dashboard operations and displays comprehensive system statistics
 */
@WebServlet(name = "AdminDashboardController", urlPatterns = {"/admin/dashboard"})
public class AdminDashboardController extends HttpServlet {
    
    private UserDAO userDAO;
    private HrDAO hrDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        userDAO = new UserDAO();
        hrDAO = new HrDAO();
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     * Displays the Admin dashboard with comprehensive statistics
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // Check authentication and admin role
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User currentUser = (User) session.getAttribute("user");
        
        // Check if user has Admin role (RoleID 2 is Admin based on SQL data)
        if (currentUser.getRoleID() != 2) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Admin privileges required.");
            return;
        }
        
        try {
            // Gather comprehensive dashboard statistics
            Map<String, Object> dashboardStats = new HashMap<>();
            
            // User Statistics
            int totalUsers = userDAO.getTotalUserCount();
            int activeUsers = userDAO.getActiveUserCount();
            int inactiveUsers = userDAO.getInactiveUserCount();
            int newUsersThisMonth = userDAO.getNewUsersThisMonth();
            
            // Get recent users (last 5 users) - use dedicated method
            List<User> recentUsers = userDAO.getRecentUsers(5);
            
            // System Statistics
            Map<String, Integer> usersByRole = userDAO.getUserCountByRole();
            Map<String, Integer> usersByStatus = new HashMap<>();
            usersByStatus.put("active", activeUsers);
            usersByStatus.put("inactive", inactiveUsers);
            usersByStatus.put("total", totalUsers);
            usersByStatus.put("newThisMonth", newUsersThisMonth);
            
            // Recent activity logs (if available)
            // List<ActivityLog> recentActivity = activityDAO.getRecentActivity(10);
            
            // Set attributes for the dashboard view
            request.setAttribute("totalUsers", totalUsers);
            request.setAttribute("activeUsers", activeUsers);
            request.setAttribute("inactiveUsers", inactiveUsers);
            request.setAttribute("recentUsers", recentUsers);
            request.setAttribute("usersByRole", usersByRole);
            request.setAttribute("usersByStatus", usersByStatus);
            // request.setAttribute("monthlyRegistrations", monthlyRegistrations); // TODO: Implement monthly stats
            request.setAttribute("dashboardStats", dashboardStats);
            
            // User information for display
            request.setAttribute("currentUser", currentUser);
            request.setAttribute("pageTitle", "Admin Dashboard");
            request.setAttribute("page", "dashboard");
            
            // Forward to admin dashboard view
            request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp")
                   .forward(request, response);
                   
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error loading dashboard data: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp")
                   .forward(request, response);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * Handles admin dashboard actions like quick operations
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // Check authentication and admin role
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User currentUser = (User) session.getAttribute("user");
        
        if (currentUser.getRoleID() != 2) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Admin privileges required.");
            return;
        }
        
        String action = request.getParameter("action");
        
        try {
            switch (action != null ? action : "") {
                case "refreshStats":
                    // Refresh dashboard statistics
                    doGet(request, response);
                    break;
                    
                case "quickUserAction":
                    handleQuickUserAction(request, response);
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
     * Handle quick user actions from dashboard
     */
    private void handleQuickUserAction(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String userAction = request.getParameter("userAction");
        String userId = request.getParameter("userId");
        
        try {
            switch (userAction != null ? userAction : "") {
                case "deactivate":
                    if (userId != null) {
                        // TODO: Implement deactivateUser method in UserDAO
                        request.setAttribute("infoMessage", "User deactivation feature coming soon");
                    }
                    break;
                    
                case "activate":
                    if (userId != null) {
                        // TODO: Implement activateUser method in UserDAO
                        request.setAttribute("infoMessage", "User activation feature coming soon");
                    }
                    break;
                    
                default:
                    request.setAttribute("errorMessage", "Invalid user action");
                    break;
            }
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid user ID format");
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error performing user action: " + e.getMessage());
        }
        
        // Redirect back to dashboard
        doGet(request, response);
    }

    /**
     * Returns a short description of the servlet.
     */
    @Override
    public String getServletInfo() {
        return "Admin Dashboard Controller - Manages admin dashboard operations";
    }
}