package controllers;

import dao.LoginDAO;
import models.User;
import service.EmailService;
import service.OTPService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Forgot Password Controller
 * Handles password reset requests and OTP generation
 */
public class ForgotPasswordController extends HttpServlet {
    
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
    
    /**
     * Check if email service is in mock mode
     */
    private boolean isEmailMockMode() {
        // For now, always return true since we're using mock service
        // This can be updated when real email service is implemented
        return true;
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if ("resend".equals(action)) {
            handleResendOTP(request, response);
        } else {
            // Show forgot password page
            showForgotPasswordPage(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getServletPath();
        
        if ("/forgot-password".equals(action)) {
            handleForgotPassword(request, response);
        }
    }
    
    /**
     * Show forgot password page
     */
    private void showForgotPasswordPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/forgot-password.jsp").forward(request, response);
    }
    
    /**
     * Handle forgot password request
     */
    private void handleForgotPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        
        // Validate input
        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng nhập địa chỉ email");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/views/forgot-password.jsp").forward(request, response);
            return;
        }
        
        email = email.trim().toLowerCase();
        
        // Validate email format
        if (!isValidEmail(email)) {
            request.setAttribute("errorMessage", "Định dạng email không hợp lệ");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/views/forgot-password.jsp").forward(request, response);
            return;
        }
        
        try {
            // Check if email exists and user is active
            if (!loginDAO.checkEmailExistsAndActive(email)) {
                request.setAttribute("errorMessage", "Email này không tồn tại trong hệ thống hoặc tài khoản đã bị vô hiệu hóa.");
                request.setAttribute("email", email);
                request.getRequestDispatcher("/WEB-INF/views/forgot-password.jsp").forward(request, response);
                return;
            }
            
            // Get user information
            User user = loginDAO.getUserByEmail(email);
            if (user == null) {
                request.setAttribute("errorMessage", "Có lỗi xảy ra. Vui lòng thử lại sau.");
                request.setAttribute("email", email);
                request.getRequestDispatcher("/WEB-INF/views/forgot-password.jsp").forward(request, response);
                return;
            }
            
            // Generate OTP
            String otpCode = otpService.generateOTP(email);
            
            // Send OTP email
            boolean emailSent = emailService.sendOTPEmail(email, otpCode, user.getFullName());
            
            if (emailSent) {
                // Store email in session for OTP verification
                HttpSession session = request.getSession(true);
                session.setAttribute("resetEmail", email);
                session.setAttribute("resetStartTime", System.currentTimeMillis());
                session.setAttribute("resetUserName", user.getFullName());
                
                // Email sent successfully (or fallback to console)
                String successMessage = "Mã xác thực đã được tạo cho địa chỉ email " + email ;
                
                session.setAttribute("successMessage", successMessage);
                response.sendRedirect(request.getContextPath() + "/verify-otp");
            } else {
                request.setAttribute("errorMessage", "Không thể tạo mã xác thực. Vui lòng thử lại sau.");
                request.setAttribute("email", email);
                request.getRequestDispatcher("/WEB-INF/views/forgot-password.jsp").forward(request, response);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi hệ thống. Vui lòng thử lại sau.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/views/forgot-password.jsp").forward(request, response);
        }
    }
    
    /**
     * Handle resend OTP request
     */
    private void handleResendOTP(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }
        
        String email = (String) session.getAttribute("resetEmail");
        if (email == null) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }
        
        try {
            User user = loginDAO.getUserByEmail(email);
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/forgot-password");
                return;
            }
            
            // Generate new OTP
            String otpCode = otpService.generateOTP(email);
            
            // Send OTP email
            boolean emailSent = emailService.sendOTPEmail(email, otpCode, user.getFullName());
            
            if (emailSent) {
                // Update session timestamp
                session.setAttribute("resetStartTime", System.currentTimeMillis());
                
                // Redirect back to OTP verification with success message
                request.setAttribute("successMessage", "Mã xác thực mới đã được gửi đến email của bạn.");
                request.getRequestDispatcher("/WEB-INF/views/verify-otp.jsp").forward(request, response);
            } else {
                request.setAttribute("errorMessage", "Không thể gửi lại mã xác thực. Vui lòng thử lại sau.");
                request.getRequestDispatcher("/WEB-INF/views/verify-otp.jsp").forward(request, response);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi hệ thống. Vui lòng thử lại sau.");
            request.getRequestDispatcher("/WEB-INF/views/verify-otp.jsp").forward(request, response);
        }
    }
    
    /**
     * Simple email validation
     * @param email Email address to validate
     * @return true if email format is valid
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        // Basic email regex pattern
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }
}