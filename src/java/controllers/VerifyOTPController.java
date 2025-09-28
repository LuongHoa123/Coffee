package controllers;

import service.OTPService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Verify OTP Controller
 * Handles OTP verification for password reset
 */
public class VerifyOTPController extends HttpServlet {
    
    private OTPService otpService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.otpService = new OTPService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check if user has a valid reset session
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("resetEmail") == null) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }
        
        // Show OTP verification page
        showVerifyOTPPage(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        handleVerifyOTP(request, response);
    }
    
    /**
     * Show OTP verification page
     */
    private void showVerifyOTPPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        String email = (String) session.getAttribute("resetEmail");
        
        // Check for success message from session
        String successMessage = (String) session.getAttribute("successMessage");
        if (successMessage != null) {
            request.setAttribute("successMessage", successMessage);
            session.removeAttribute("successMessage"); // Remove to prevent showing again
        }
        
        // Get remaining time for OTP
        long remainingTime = otpService.getOTPRemainingTime(email);
        request.setAttribute("remainingTime", remainingTime);
        request.setAttribute("email", maskEmail(email));
        
        request.getRequestDispatcher("/WEB-INF/views/verify-otp.jsp").forward(request, response);
    }
    
    /**
     * Handle OTP verification
     */
    private void handleVerifyOTP(HttpServletRequest request, HttpServletResponse response)
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
        
        String otpCode = request.getParameter("otpCode");
        
        // Validate input
        if (otpCode == null || otpCode.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng nhập mã xác thực");
            request.setAttribute("otpCode", otpCode);
            showVerifyOTPPageWithMessage(request, response);
            return;
        }
        
        otpCode = otpCode.trim();
        
        // Validate OTP format (6 digits)
        if (!otpCode.matches("^\\d{6}$")) {
            request.setAttribute("errorMessage", "Mã xác thực phải là 6 chữ số");
            request.setAttribute("otpCode", otpCode);
            showVerifyOTPPageWithMessage(request, response);
            return;
        }
        
        try {
            // Verify OTP
            boolean isValid = otpService.validateOTP(email, otpCode);
            
            if (isValid) {
                // OTP is valid, mark as verified in session
                session.setAttribute("otpVerified", true);
                session.setAttribute("otpVerifiedTime", System.currentTimeMillis());
                
                // Redirect to reset password page
                response.sendRedirect(request.getContextPath() + "/reset-password");
            } else {
                // Check if OTP exists but is expired or invalid
                if (!otpService.hasValidOTP(email)) {
                    request.setAttribute("errorMessage", "Mã xác thực đã hết hạn hoặc không tồn tại. Vui lòng yêu cầu mã mới.");
                } else {
                    request.setAttribute("errorMessage", "Mã xác thực không đúng. Vui lòng thử lại.");
                }
                request.setAttribute("otpCode", otpCode);
                showVerifyOTPPageWithMessage(request, response);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi hệ thống. Vui lòng thử lại sau.");
            request.setAttribute("otpCode", otpCode);
            showVerifyOTPPageWithMessage(request, response);
        }
    }
    
    /**
     * Show OTP verification page with error/success message
     */
    private void showVerifyOTPPageWithMessage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        String email = (String) session.getAttribute("resetEmail");
        
        // Get remaining time for OTP
        long remainingTime = otpService.getOTPRemainingTime(email);
        request.setAttribute("remainingTime", remainingTime);
        request.setAttribute("email", maskEmail(email));
        
        request.getRequestDispatcher("/WEB-INF/views/verify-otp.jsp").forward(request, response);
    }
    
    /**
     * Mask email for display (e.g., test@example.com -> t***@example.com)
     */
    private String maskEmail(String email) {
        if (email == null || email.length() < 3) {
            return email;
        }
        
        int atIndex = email.indexOf('@');
        if (atIndex <= 1) {
            return email;
        }
        
        String localPart = email.substring(0, atIndex);
        String domain = email.substring(atIndex);
        
        if (localPart.length() <= 2) {
            return email;
        }
        
        // Show first character and mask the rest before @
        String maskedLocal = localPart.charAt(0) + "***";
        return maskedLocal + domain;
    }
}