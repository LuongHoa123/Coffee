package controllers;

import dao.UserDAO;
import models.User;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * UserListController - Hiển thị danh sách người dùng
 */
@WebServlet(name = "UserListController", urlPatterns = {"/hr/user-list"})
public class UserListController extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String search = request.getParameter("search");
            List<User> users;
            
            if (search != null && !search.trim().isEmpty()) {
                users = userDAO.searchUsersForHR(search.trim());
            } else {
                users = userDAO.getAllUsersForHR();
            }
            
            // Debug log
            System.out.println("DEBUG: Found " + users.size() + " users");
            for (User user : users) {
                System.out.println("DEBUG: User ID=" + user.getUserID() + ", Name=" + user.getFullName() + ", Role=" + user.getRole());
            }
            
            request.setAttribute("users", users);
            request.setAttribute("currentSearch", search);
            
            // Hiển thị thông báo thành công nếu có
            String success = request.getParameter("success");
            if ("add".equals(success)) {
                request.setAttribute("success", "Thêm người dùng thành công!");
            } else if ("update".equals(success)) {
                request.setAttribute("success", "Cập nhật người dùng thành công!");
            }
            
            // Hiển thị thông báo lỗi nếu có
            String error = request.getParameter("error");
            if ("user_not_found".equals(error)) {
                request.setAttribute("error", "Không tìm thấy người dùng!");
            } else if ("invalid_id".equals(error)) {
                request.setAttribute("error", "ID người dùng không hợp lệ!");
            } else if ("system_error".equals(error)) {
                request.setAttribute("error", "Lỗi hệ thống!");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Không thể tải danh sách người dùng: " + e.getMessage());
        }
        
        request.getRequestDispatcher("/WEB-INF/views/hr/user-list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        try {
            if ("activate".equals(action)) {
                int userId = Integer.parseInt(request.getParameter("userId"));
                boolean success = userDAO.updateUserStatus(userId, true);
                
                if (success) {
                    request.setAttribute("success", "Kích hoạt tài khoản thành công!");
                } else {
                    request.setAttribute("error", "Không thể kích hoạt tài khoản!");
                }
            } else if ("deactivate".equals(action)) {
                int userId = Integer.parseInt(request.getParameter("userId"));
                boolean success = userDAO.updateUserStatus(userId, false);
                
                if (success) {
                    request.setAttribute("success", "Vô hiệu hóa tài khoản thành công!");
                } else {
                    request.setAttribute("error", "Không thể vô hiệu hóa tài khoản!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi: " + e.getMessage());
        }
        
        // Reload danh sách sau khi thực hiện action
        doGet(request, response);
    }
}