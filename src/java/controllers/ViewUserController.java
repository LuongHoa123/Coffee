package controllers;

import dao.UserDAO;
import models.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * ViewUserController - Xem chi tiet nguoi dung
 */
@WebServlet(name = "ViewUserController", urlPatterns = {"/hr/user-details"})
public class ViewUserController extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String idParam = request.getParameter("id");
        
        if (idParam != null && !idParam.trim().isEmpty()) {
            try {
                int userId = Integer.parseInt(idParam);
                User user = userDAO.getUserById(userId);
                
                if (user != null && (user.getRoleID() == 3 || user.getRoleID() == 4)) {
                    // Get role name
                    String roleName = getRoleName(user.getRoleID());
                    
                    request.setAttribute("user", user);
                    request.setAttribute("roleName", roleName);
                    request.getRequestDispatcher("/WEB-INF/views/hr/user-details.jsp").forward(request, response);
                } else {
                    request.setAttribute("errorMessage", "User not found or no permission to view.");
                    request.getRequestDispatcher("/WEB-INF/views/hr/user-details.jsp").forward(request, response);
                }
                
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/hr/user-list?error=invalid_id");
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/hr/user-list?error=system_error");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/hr/user-list");
        }
    }
    
    /**
     * Get role name from RoleID
     */
    private String getRoleName(int roleId) {
        switch (roleId) {
            case 1:
                return "Admin";
            case 2:
                return "HR";
            case 3:
                return "Inventory";
            case 4:
                return "Barista";
            default:
                return "Unknown";
        }
    }
}