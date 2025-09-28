package controllers;

import dao.UserDAO;
import models.User;
import utils.PasswordUtil;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * EditUserController - Chỉnh sửa người dùng
 */
@WebServlet(name = "EditUserController", urlPatterns = {"/hr/edit-user"})
public class EditUserController extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        
        if (idParam != null && !idParam.trim().isEmpty()) {
            try {
                int userId = Integer.parseInt(idParam);
                User user = userDAO.getUserById(userId);
                
                if (user != null) {
                    request.setAttribute("user", user);
                    request.getRequestDispatcher("/WEB-INF/views/hr/user-edit.jsp").forward(request, response);
                } else {
                    response.sendRedirect(request.getContextPath() + "/hr/user-list?error=user_not_found");
                }
                
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/hr/user-list?error=invalid_id");
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/hr/user-list?error=system_error");
            }
        } else {
            // Không có ID, hiển thị danh sách để chọn user
            response.sendRedirect(request.getContextPath() + "/hr/user-list");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        try {
            // Lấy thông tin từ form
            int userId = Integer.parseInt(request.getParameter("userId"));
            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");
            String newPassword = request.getParameter("newPassword");
            String confirmPassword = request.getParameter("confirmPassword");
            String role = request.getParameter("role");
            String phone = request.getParameter("phone");
            String address = request.getParameter("address");
            boolean isActive = "true".equals(request.getParameter("isActive"));
            
            // Lấy user hiện tại từ DB để kiểm tra
            User originalUser = userDAO.getUserById(userId);
            if (originalUser == null) {
                response.sendRedirect(request.getContextPath() + "/hr/user-list?error=user_not_found");
                return;
            }
            
            // Tạo user object từ form data để giữ lại dữ liệu đã nhập
            // Nếu form data bị thiếu, sử dụng dữ liệu gốc
            User formUser = new User();
            formUser.setUserID(userId);
            formUser.setFullName(fullName != null && !fullName.trim().isEmpty() ? fullName.trim() : originalUser.getFullName());
            formUser.setEmail(email != null && !email.isEmpty() && !email.trim().isEmpty() ? email.trim() : originalUser.getEmail());
            formUser.setPhone(phone != null ? phone.trim() : originalUser.getPhone());
            formUser.setAddress(address != null ? address.trim() : originalUser.getAddress());
            formUser.setActive(originalUser.isActive()); // Giữ nguyên status hiện tại
            
            // Set role - sử dụng role từ form hoặc giữ nguyên role gốc
            if (role != null && !role.trim().isEmpty()) {
                formUser.setRoleID(getRoleID(role));
                formUser.setRole(role);
            } else {
                formUser.setRoleID(originalUser.getRoleID());
                formUser.setRole(originalUser.getRole());
            }
            
            // Validate dữ liệu sử dụng formUser (đã có fallback values)
            if (formUser.getFullName() == null || formUser.getFullName().trim().isEmpty()) {
                request.setAttribute("error", "Họ tên không được để trống");
                request.setAttribute("user", formUser);
                request.getRequestDispatcher("/WEB-INF/views/hr/user-edit.jsp").forward(request, response);
                return;
            }
            
            if (formUser.getEmail() == null || formUser.getEmail().trim().isEmpty()) {
                request.setAttribute("error", "Email không được để trống");
                request.setAttribute("user", formUser);
                request.getRequestDispatcher("/WEB-INF/views/hr/user-edit.jsp").forward(request, response);
                return;
            }
            
            // Kiểm tra password nếu có thay đổi
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                if (!newPassword.equals(confirmPassword)) {
                    request.setAttribute("error", "Xác nhận mật khẩu không khớp");
                    request.setAttribute("user", formUser);
                    request.getRequestDispatcher("/WEB-INF/views/hr/user-edit.jsp").forward(request, response);
                    return;
                }
            }
            
            // Kiểm tra email trùng (ngoại trừ user hiện tại)
            User existingUser = userDAO.getUserByEmail(formUser.getEmail());
            if (existingUser != null && existingUser.getUserID() != userId) {
                request.setAttribute("error", "Email đã được sử dụng bởi người dùng khác");
                request.setAttribute("user", formUser);
                request.getRequestDispatcher("/WEB-INF/views/hr/user-edit.jsp").forward(request, response);
                return;
            }
            
            // Cập nhật thông tin vào originalUser sử dụng formUser data (giữ nguyên status hiện tại)
            originalUser.setFullName(formUser.getFullName());
            originalUser.setEmail(formUser.getEmail());
            originalUser.setPhone(formUser.getPhone());
            originalUser.setAddress(formUser.getAddress());
            originalUser.setRoleID(formUser.getRoleID());
            originalUser.setRole(formUser.getRole());
            // Không cập nhật isActive - giữ nguyên trạng thái hiện tại
            
            // Cập nhật mật khẩu nếu có
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                originalUser.setPasswordHash(PasswordUtil.hashPassword(newPassword));
            }
            
            // Debug: In thông tin trước khi cập nhật
            System.out.println("DEBUG: Updating user - ID: " + originalUser.getUserID() + 
                             ", Name: " + originalUser.getFullName() + 
                             ", Email: " + originalUser.getEmail() + 
                             ", Role: " + originalUser.getRole());
            
            // Cập nhật trong database
            boolean success = userDAO.updateUser(originalUser);
            
            System.out.println("DEBUG: Update result: " + success);
            
            if (success) {
                System.out.println("DEBUG: Redirecting to user-list...");
                response.sendRedirect(request.getContextPath() + "/hr/user-list?success=update");
            } else {
                System.out.println("DEBUG: Update failed, showing error...");
                request.setAttribute("error", "Không thể cập nhật người dùng");
                request.setAttribute("user", formUser);
                request.getRequestDispatcher("/WEB-INF/views/hr/user-edit.jsp").forward(request, response);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi: " + e.getMessage());
            try {
                int userId = Integer.parseInt(request.getParameter("userId"));
                // Tạo formUser từ dữ liệu đã nhập để giữ lại khi có lỗi
                User formUser = new User();
                formUser.setUserID(userId);
                formUser.setFullName(request.getParameter("fullName") != null ? request.getParameter("fullName").trim() : "");
                formUser.setEmail(request.getParameter("email") != null ? request.getParameter("email").trim() : "");
                formUser.setPhone(request.getParameter("phone") != null ? request.getParameter("phone").trim() : "");
                formUser.setAddress(request.getParameter("address") != null ? request.getParameter("address").trim() : "");
                formUser.setActive("true".equals(request.getParameter("isActive")));
                String formRole = request.getParameter("role");
                if (formRole != null) {
                    formUser.setRoleID(getRoleID(formRole));
                    formUser.setRole(formRole);
                }
                request.setAttribute("user", formUser);
                request.getRequestDispatcher("/WEB-INF/views/hr/user-edit.jsp").forward(request, response);
            } catch (Exception ex) {
                response.sendRedirect(request.getContextPath() + "/hr/user-list?error=system_error");
            }
        }
    }
    
    /**
     * Convert role name to roleID
     */
    private int getRoleID(String roleName) {
        switch (roleName) {
            case "HR":
                return 1;
            case "Admin":
                return 2;
            case "Inventory":
                return 3;
            case "Barista":
                return 4;
            default:
                return 4; // Default to Barista
        }
    }
}