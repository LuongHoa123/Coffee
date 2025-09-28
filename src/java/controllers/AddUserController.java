package controllers;

import dao.UserDAO;
import models.User;
import utils.PasswordUtil;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

/**
 * AddUserController - Thêm người dùng mới
 */
@WebServlet(name = "AddUserController", urlPatterns = {"/hr/add-user"})
public class AddUserController extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Lấy danh sách roles cho HR
        try {
            List<String> availableRoles = userDAO.getAvailableRolesForHR();
            request.setAttribute("availableRoles", availableRoles);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Hiển thị form thêm người dùng
        request.getRequestDispatcher("/WEB-INF/views/hr/user-add.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        try {
            // Lấy thông tin từ form
            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String confirmPassword = request.getParameter("confirmPassword");
            String role = request.getParameter("role");
            String phone = request.getParameter("phone");
            String address = request.getParameter("address");
            
            // Tạo formData object để giữ lại dữ liệu khi có lỗi
            User formData = new User();
            formData.setFullName(fullName != null ? fullName.trim() : "");
            formData.setEmail(email != null ? email.trim() : "");
            formData.setPhone(phone != null ? phone.trim() : "");
            formData.setAddress(address != null ? address.trim() : "");
            if (role != null) {
                formData.setRole(role);
            }
            
            // Validate dữ liệu và giữ lại form data khi có lỗi
            if (fullName == null || fullName.trim().isEmpty()) {
                request.setAttribute("error", "Họ tên không được để trống");
                request.setAttribute("formData", formData);
                setAvailableRoles(request);
                request.getRequestDispatcher("/WEB-INF/views/hr/user-add.jsp").forward(request, response);
                return;
            }
            
            if (email == null || email.trim().isEmpty()) {
                request.setAttribute("error", "Email không được để trống");
                request.setAttribute("formData", formData);
                setAvailableRoles(request);
                request.getRequestDispatcher("/WEB-INF/views/hr/user-add.jsp").forward(request, response);
                return;
            }
            
            if (password == null || password.trim().isEmpty()) {
                request.setAttribute("error", "Mật khẩu không được để trống");
                request.setAttribute("formData", formData);
                setAvailableRoles(request);
                request.getRequestDispatcher("/WEB-INF/views/hr/user-add.jsp").forward(request, response);
                return;
            }
            
            if (!password.equals(confirmPassword)) {
                request.setAttribute("error", "Xác nhận mật khẩu không khớp");
                request.setAttribute("formData", formData);
                setAvailableRoles(request);
                request.getRequestDispatcher("/WEB-INF/views/hr/user-add.jsp").forward(request, response);
                return;
            }
            
            // Kiểm tra email đã tồn tại chưa
            if (userDAO.isEmailExists(email.trim())) {
                request.setAttribute("error", "Email đã tồn tại trong hệ thống");
                request.setAttribute("formData", formData);
                setAvailableRoles(request);
                request.getRequestDispatcher("/WEB-INF/views/hr/user-add.jsp").forward(request, response);
                return;
            }
            
            // Lấy RoleID từ role name
            String roleName = role != null ? role : "Inventory"; // Default to Inventory
            int roleID = userDAO.getRoleIdByName(roleName);
            
            if (roleID == 0) {
                request.setAttribute("error", "Vai trò không hợp lệ");
                request.setAttribute("formData", formData);
                setAvailableRoles(request);
                request.getRequestDispatcher("/WEB-INF/views/hr/user-add.jsp").forward(request, response);
                return;
            }
            
            // Tạo user mới
            User newUser = new User();
            newUser.setFullName(fullName.trim());
            newUser.setEmail(email.trim());
            newUser.setPasswordHash(PasswordUtil.hashPassword(password)); 
            newUser.setRoleID(roleID);
            newUser.setRole(roleName);
            newUser.setPhone(phone != null ? phone.trim() : "");
            newUser.setAddress(address != null ? address.trim() : "");
            newUser.setActive(true);
            newUser.setCreatedDate(LocalDateTime.now());
            
            // Thêm vào database
            boolean success = userDAO.addUser(newUser);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/hr/user-list?success=add");
            } else {
                request.setAttribute("error", "Không thể thêm người dùng");
                request.setAttribute("formData", formData);
                setAvailableRoles(request);
                request.getRequestDispatcher("/WEB-INF/views/hr/user-add.jsp").forward(request, response);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi: " + e.getMessage());
            // Tạo formData từ request để giữ lại dữ liệu khi có exception
            User formData = new User();
            formData.setFullName(request.getParameter("fullName") != null ? request.getParameter("fullName").trim() : "");
            formData.setEmail(request.getParameter("email") != null ? request.getParameter("email").trim() : "");
            formData.setPhone(request.getParameter("phone") != null ? request.getParameter("phone").trim() : "");
            formData.setAddress(request.getParameter("address") != null ? request.getParameter("address").trim() : "");
            if (request.getParameter("role") != null) {
                formData.setRole(request.getParameter("role"));
            }
            request.setAttribute("formData", formData);
            setAvailableRoles(request);
            request.getRequestDispatcher("/WEB-INF/views/hr/user-add.jsp").forward(request, response);
        }
    }
    
    /**
     * Helper method to set available roles
     */
    private void setAvailableRoles(HttpServletRequest request) {
        try {
            List<String> availableRoles = userDAO.getAvailableRolesForHR();
            request.setAttribute("availableRoles", availableRoles);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}