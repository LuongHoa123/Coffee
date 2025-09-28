package controllers;

import service.AuthService;
import service.AuthService.UserSession;
import dao.LoginDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Cookie;
import java.io.IOException;

/**
 * Login Controller
 * Handles user authentication and session management
 */
public class LoginController extends HttpServlet {
    
    private AuthService authService;
    private LoginDAO loginDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.authService = new AuthService();
        this.loginDAO = new LoginDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        
        
            showLoginPage(request, response);
        
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getServletPath();
        
        if ("/login".equals(action)) {
            handleLogin(request, response);
        }
    }
    
    /**
     * Show login page
     */
    private void showLoginPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check if already logged in
        HttpSession session = request.getSession(false);
        if (session != null) {
            String sessionId = (String) session.getAttribute("sessionId");
            if (sessionId != null && authService.isValidSession(sessionId)) {
                UserSession userSession = authService.getUserSession(sessionId);
                if (userSession != null) {
                    String redirectUrl = authService.getRedirectURLByRole(userSession.getRole());
                    response.sendRedirect(request.getContextPath() + redirectUrl);
                    return;
                }
            }
        }
        
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }
    
    /**
     * Handle login authentication
     */
    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String rememberMe = request.getParameter("rememberMe");
        
        // Validate input
        if (email == null || email.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            
            request.setAttribute("errorMessage", "Email và mật khẩu không được để trống");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            return;
        }
        
        try {
            // Authenticate user
            UserSession userSession = authService.login(email.trim(), password);
            
            if (userSession != null) {
                // Create HTTP session and store session ID
                HttpSession httpSession = request.getSession(true);
                httpSession.setAttribute("sessionId", userSession.getSessionId());
                httpSession.setAttribute("user", userSession.getUser());
                httpSession.setAttribute("role", userSession.getRole());
                
                // Set session timeout (30 minutes)
                httpSession.setMaxInactiveInterval(30 * 60);
                
                // Handle "Remember Me" functionality
                if ("on".equals(rememberMe)) {
                    Cookie sessionCookie = new Cookie("sessionId", userSession.getSessionId());
                    sessionCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
                    sessionCookie.setPath(request.getContextPath());
                    sessionCookie.setHttpOnly(true);
                    response.addCookie(sessionCookie);
                }
                
                // Redirect based on role
                String redirectUrl = authService.getRedirectURLByRole(userSession.getRole());
                response.sendRedirect(request.getContextPath() + redirectUrl);
                
            } else {
                // Authentication failed
                request.setAttribute("errorMessage", "Email hoặc mật khẩu không đúng");
                request.setAttribute("email", email);
                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi hệ thống. Vui lòng thử lại sau.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }
    
    
}