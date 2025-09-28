package controllers;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.BaristaDashboardService;
import models.User;
import jakarta.servlet.annotation.WebServlet;
import java.util.Map;

/**
 * Barista Dashboard Controller
 * Handles barista dashboard operations and displays order statistics, issues, and product availability
 */
@WebServlet(name = "BaristaDashboardController", urlPatterns = {"/barista/dashboard"})
public class BaristaDashboardController extends HttpServlet {
    
    private BaristaDashboardService baristaDashboardService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        baristaDashboardService = new BaristaDashboardService();
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     * Displays the Barista dashboard with comprehensive statistics
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // Check authentication and barista role
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User currentUser = (User) session.getAttribute("user");
        
        // Check if user has Barista role (RoleID = 4)
        if (currentUser.getRoleID() != 4) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Barista privileges required.");
            return;
        }
        
        try {
            // Get dashboard data using service
            Map<String, Object> dashboardData = baristaDashboardService.getDashboardDataForUser(currentUser.getUserID());
            
            // Set all dashboard data as attributes for JSP
            for (Map.Entry<String, Object> entry : dashboardData.entrySet()) {
                request.setAttribute(entry.getKey(), entry.getValue());
            }
            
            // Set page metadata
            request.setAttribute("currentUser", currentUser);
            request.setAttribute("pageTitle", "Barista Dashboard");
            request.setAttribute("page", "dashboard");
            
            // Forward to barista dashboard view
            request.getRequestDispatcher("/WEB-INF/views/barista/dashboard.jsp")
                   .forward(request, response);
                   
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
        
        HttpSession session = request.getSession(false);
        
        // Check authentication
        if (session == null || session.getAttribute("user") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Authentication required\"}");
            return;
        }
        
        User currentUser = (User) session.getAttribute("user");
        
        // Check role
        if (currentUser.getRoleID() != 4) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"error\":\"Access denied\"}");
            return;
        }
        
        String action = request.getParameter("action");
        
        try {
            switch (action != null ? action : "") {
                case "refreshStats":
                    // Return JSON statistics
                    Map<String, Object> stats = baristaDashboardService.getDashboardStatistics();
                    response.getWriter().write(mapToJson(stats));
                    break;
                    
                case "getOrderStatus":
                    // Return order status chart data
                    String orderStatusJson = baristaDashboardService.getOrderStatusJson();
                    response.getWriter().write(orderStatusJson);
                    break;
                    
                case "getPopularProducts":
                    // Return popular products chart data
                    String productsJson = baristaDashboardService.getPopularProductsJson();
                    response.getWriter().write(productsJson);
                    break;
                    
                default:
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\":\"Invalid action\"}");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
    
    /**
     * Simple method to convert Map to JSON string
     * In real application, use Jackson or Gson
     */
    private String mapToJson(Map<String, Object> map) {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) json.append(",");
            json.append("\"").append(entry.getKey()).append("\":");
            
            Object value = entry.getValue();
            if (value instanceof String) {
                json.append("\"").append(value).append("\"");
            } else if (value instanceof Number) {
                json.append(value);
            } else if (value instanceof Boolean) {
                json.append(value);
            } else {
                json.append("\"").append(value.toString()).append("\"");
            }
            first = false;
        }
        json.append("}");
        return json.toString();
    }

    /**
     * Returns a short description of the servlet.
     */
    @Override
    public String getServletInfo() {
        return "Barista Dashboard Controller - Handles barista dashboard operations";
    }
}