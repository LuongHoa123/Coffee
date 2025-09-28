package filters;

import service.AuthService;
import service.AuthService.UserSession;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Cookie;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Authentication Filter
 * Checks user authentication and authorization for protected resources
 */
public class AuthenticationFilter implements Filter {
    
    private AuthService authService;
    
    // URLs that don't require authentication
    private final List<String> publicUrls = Arrays.asList(
        "/login", "/css/", "/js/", "/images/", "/static/", 
        "/favicon.ico", "/register", "/forgot-password", 
        "/verify-otp", "/reset-password"
    );
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.authService = new AuthService();
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        
        // Remove context path from URI
        if (requestURI.startsWith(contextPath)) {
            requestURI = requestURI.substring(contextPath.length());
        }
        
        // Check if URL is public (doesn't require authentication)
        if (isPublicUrl(requestURI)) {
            chain.doFilter(request, response);
            return;
        }
        
        // Check authentication
        String sessionId = getSessionId(httpRequest);
        UserSession userSession = null;
        
        if (sessionId != null) {
            userSession = authService.getUserSession(sessionId);
        }
        
        if (userSession == null) {
            // Not authenticated, redirect to login
            redirectToLogin(httpRequest, httpResponse);
            return;
        }
        
        // Check authorization (role-based access)
        if (!authService.hasAccess(sessionId, requestURI)) {
            // Access denied
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, 
                "Bạn không có quyền truy cập trang này");
            return;
        }
        
        // Update session in HTTP session
        HttpSession httpSession = httpRequest.getSession(false);
        if (httpSession != null) {
            httpSession.setAttribute("sessionId", sessionId);
            httpSession.setAttribute("user", userSession.getUser());
            httpSession.setAttribute("role", userSession.getRole());
        }
        
        // Continue with request
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        // Cleanup if needed
    }
    
    /**
     * Check if URL is public (doesn't require authentication)
     */
    private boolean isPublicUrl(String requestURI) {
        for (String publicUrl : publicUrls) {
            if (requestURI.startsWith(publicUrl)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get session ID from HTTP session or cookie
     */
    private String getSessionId(HttpServletRequest request) {
        // First try to get from HTTP session
        HttpSession session = request.getSession(false);
        if (session != null) {
            String sessionId = (String) session.getAttribute("sessionId");
            if (sessionId != null && authService.isValidSession(sessionId)) {
                return sessionId;
            }
        }
        
        // If not found in session, try cookies (remember me functionality)
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("sessionId".equals(cookie.getName())) {
                    String sessionId = cookie.getValue();
                    if (authService.isValidSession(sessionId)) {
                        return sessionId;
                    }
                }
            }
        }
        
        return null;
    }
    
    /**
     * Redirect to login page
     */
    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        String loginUrl = request.getContextPath() + "/login";
        
        // Add return URL parameter for redirect after login
        String returnUrl = request.getRequestURI();
        if (request.getQueryString() != null) {
            returnUrl += "?" + request.getQueryString();
        }
        
        if (!returnUrl.equals(request.getContextPath() + "/login")) {
            loginUrl += "?returnUrl=" + java.net.URLEncoder.encode(returnUrl, "UTF-8");
        }
        
        response.sendRedirect(loginUrl);
    }
}