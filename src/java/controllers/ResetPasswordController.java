package controllers;

import dao.LoginDAO;
import service.EmailService;
import service.OTPService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Reset Password Controller
 * Handles password reset after OTP verification
 */
public class ResetPasswordController extends HttpServlet {
    
    private LoginDAO loginDAO;
    private EmailService emailService;
    private OTPService otpService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.loginDAO = new LoginDAO();
        this.emailService = new EmailService();
        this.otpService = new OTPService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check if user has valid OTP verification session
        HttpSession session = request.getSession(false);
        if (session == null || 
            session.getAttribute("resetEmail") == null ||
            session.getAttribute("otpVerified") == null ||
            !Boolean.TRUE.equals(session.getAttribute("otpVerified"))) {
            
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }
        
        // Check if OTP verification is not too old (15 minutes max)
        Long otpVerifiedTime = (Long) session.getAttribute("otpVerifiedTime");
        if (otpVerifiedTime != null) {
            long timeDiff = System.currentTimeMillis() - otpVerifiedTime;
            long maxTime = 15 * 60 * 1000; // 15 minutes
            
            if (timeDiff > maxTime) {
                session.invalidate();
                request.setAttribute("errorMessage", "Phiên đặt lại mật khẩu đã hết hạn. Vui lòng thử lại.");
                request.getRequestDispatcher("/WEB-INF/views/forgot-password.jsp").forward(request, response);
                return;
            }
        }
        
        // Show reset password page
        showResetPasswordPage(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        handleResetPassword(request, response);
    }
    
    /**
     * Show reset password page
     */
    private void showResetPasswordPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/reset-password.jsp").forward(request, response);
    }
    
    /**
     * Handle password reset
     */
    private void handleResetPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || 
            session.getAttribute("resetEmail") == null ||
            !Boolean.TRUE.equals(session.getAttribute("otpVerified"))) {
            
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }
        
        String email = (String) session.getAttribute("resetEmail");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        
        // Validate input
        if (newPassword == null || newPassword.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng nhập mật khẩu mới");
            showResetPasswordPageWithMessage(request, response);
            return;
        }
        
        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng xác nhận mật khẩu mới");
            showResetPasswordPageWithMessage(request, response);
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "Mật khẩu xác nhận không khớp");
            showResetPasswordPageWithMessage(request, response);
            return;
        }
        
        // Validate password strength
        String passwordError = validatePasswordStrength(newPassword);
        if (passwordError != null) {
            request.setAttribute("errorMessage", passwordError);
            showResetPasswordPageWithMessage(request, response);
            return;
        }
        
        try {
            // Get user by email
            var user = loginDAO.getUserByEmail(email);
            if (user == null) {
                request.setAttribute("errorMessage", "Không tìm thấy tài khoản.");
                showResetPasswordPageWithMessage(request, response);
                return;
            }
            
            // Update password
            boolean updated = loginDAO.updatePassword(user.getUserID(), newPassword);
            
            if (updated) {
                // Send confirmation email
                emailService.sendPasswordResetConfirmation(email, user.getFullName());
                
                // Clean up OTP
                otpService.invalidateOTP(email);
                
                // Clear session
                session.invalidate();
                
                // Redirect to login with success message
                request.setAttribute("successMessage", 
                    "Mật khẩu đã được đặt lại thành công. Vui lòng đăng nhập với mật khẩu mới.");
                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
                
            } else {
                request.setAttribute("errorMessage", "Không thể cập nhật mật khẩu. Vui lòng thử lại sau.");
                showResetPasswordPageWithMessage(request, response);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi hệ thống. Vui lòng thử lại sau.");
            showResetPasswordPageWithMessage(request, response);
        }
    }
    
    /**
     * Show reset password page with message
     */
    private void showResetPasswordPageWithMessage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/reset-password.jsp").forward(request, response);
    }
    
    /**
     * Validate password strength
     * @param password Password to validate
     * @return Error message if invalid, null if valid
     */
    private String validatePasswordStrength(String password) {
        if (password == null) {
            return "Mật khẩu không được để trống";
        }
        
        if (password.length() < 8) {
            return "Mật khẩu phải có ít nhất 8 ký tự";
        }
        
        if (password.length() > 50) {
            return "Mật khẩu không được vượt quá 50 ký tự";
        }
        
        // Check for at least one digit
        if (!password.matches(".*\\d.*")) {
            return "Mật khẩu phải chứa ít nhất 1 chữ số";
        }
        
        // Check for at least one letter
        if (!password.matches(".*[a-zA-Z].*")) {
            return "Mật khẩu phải chứa ít nhất 1 chữ cái";
        }
        
        // Check for at least one special character
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            return "Mật khẩu phải chứa ít nhất 1 ký tự đặc biệt (!@#$%^&*...)";
        }
        
        return null; // Valid password
    }
}