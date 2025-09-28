package service;

import models.User;
import dao.LoginDAO;

import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Authentication and Authorization Service
 * Manages user sessions and role-based access control
 */
public class AuthService {
    
    // Define role constants
    public static final String ROLE_ADMIN = "Admin";
    public static final String ROLE_HR = "HR";
    public static final String ROLE_BARISTA = "Barista";
    public static final String ROLE_INVENTORY = "Inventory";
    
    private LoginDAO loginDAO;
    
    // Role-based URL access mapping
    private Map<String, List<String>> roleAccessMap;
    
    // Session storage (in production, this should be handled by servlet container)
    private Map<String, UserSession> activeSessions;
    
    public AuthService() {
        this.loginDAO = new LoginDAO();
        this.activeSessions = new ConcurrentHashMap<>();
        initializeRoleAccessMap();
    }
    
    /**
     * Initialize role-based access control mapping
     */
    private void initializeRoleAccessMap() {
        roleAccessMap = new HashMap<>();
        
        // Admin has access to everything
        roleAccessMap.put(ROLE_ADMIN, Arrays.asList(
            "/admin/*", "/hr/*", "/inventory/*", "/barista/*", "/user/*", 
            "/reports/*", "/settings/*", "/dashboard"
        ));
        
        // HR manages users and staff
        roleAccessMap.put(ROLE_HR, Arrays.asList(
            "/hr/*", "/user/*", "/reports/user*", "/dashboard"
        ));
        
        // Barista handles orders and products
        roleAccessMap.put(ROLE_BARISTA, Arrays.asList(
            "/barista/*", "/orders/*", "/products/view*", "/dashboard"
        ));
        
        // Inventory manages stock and suppliers
        roleAccessMap.put(ROLE_INVENTORY, Arrays.asList(
            "/inventory/*", "/suppliers/*", "/purchaseorders/*", "/ingredients/*", 
            "/reports/inventory*", "/dashboard"
        ));
    }
    
    /**
     * Authenticate user and create session
     * @param email User email
     * @param password Plain text password
     * @return UserSession if authentication successful, null otherwise
     */
    public UserSession login(String email, String password) {
        User user = loginDAO.authenticate(email, password);
        
        if (user != null) {
            // Get role name
            String roleName = loginDAO.getRoleName(user.getRoleID());
            
            // Create user session
            String sessionId = generateSessionId();
            UserSession userSession = new UserSession(sessionId, user, roleName);
            
            // Store session
            activeSessions.put(sessionId, userSession);
            
            return userSession;
        }
        
        return null;
    }
    
    /**
     * Logout user and invalidate session
     * @param sessionId Session ID
     */
    public void logout(String sessionId) {
        if (sessionId != null) {
            activeSessions.remove(sessionId);
        }
    }
    
    /**
     * Check if session is valid and active
     * @param sessionId Session ID
     * @return true if session is valid, false otherwise
     */
    public boolean isValidSession(String sessionId) {
        if (sessionId == null) {
            return false;
        }
        
        UserSession session = activeSessions.get(sessionId);
        if (session == null) {
            return false;
        }
        
        // Check if session is expired (30 minutes)
        long sessionAge = System.currentTimeMillis() - session.getCreatedTime();
        if (sessionAge > 30 * 60 * 1000) { // 30 minutes in milliseconds
            activeSessions.remove(sessionId);
            return false;
        }
        
        // Update last access time
        session.updateLastAccess();
        return true;
    }
    
    /**
     * Get user session
     * @param sessionId Session ID
     * @return UserSession if valid, null otherwise
     */
    public UserSession getUserSession(String sessionId) {
        if (!isValidSession(sessionId)) {
            return null;
        }
        
        return activeSessions.get(sessionId);
    }
    
    /**
     * Check if user has permission to access a URL
     * @param sessionId Session ID
     * @param requestURI Requested URL path
     * @return true if user has access, false otherwise
     */
    public boolean hasAccess(String sessionId, String requestURI) {
        UserSession session = getUserSession(sessionId);
        if (session == null) {
            return false;
        }
        
        String userRole = session.getRole();
        if (userRole == null) {
            return false;
        }
        
        // Public URLs that all logged-in users can access
        if (isPublicURL(requestURI)) {
            return true;
        }
        
        // Check role-based access
        List<String> allowedPaths = roleAccessMap.get(userRole);
        if (allowedPaths == null) {
            return false;
        }
        
        // Check if requested URI matches any allowed pattern
        for (String allowedPath : allowedPaths) {
            if (matchesPattern(requestURI, allowedPath)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Check if URL is public (accessible to all logged-in users)
     * @param requestURI URL path to check
     * @return true if public URL, false otherwise
     */
    private boolean isPublicURL(String requestURI) {
        String[] publicPaths = {
            "/logout", "/profile", "/change-password", 
            "/static/*", "/css/*", "/js/*", "/images/*",
            "/dashboard", "/home"
        };
        
        for (String publicPath : publicPaths) {
            if (matchesPattern(requestURI, publicPath)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Check if URI matches a pattern (supports wildcard *)
     * @param uri URI to check
     * @param pattern Pattern to match against
     * @return true if matches, false otherwise
     */
    private boolean matchesPattern(String uri, String pattern) {
        if (pattern.endsWith("*")) {
            String prefix = pattern.substring(0, pattern.length() - 1);
            return uri.startsWith(prefix);
        } else {
            return uri.equals(pattern);
        }
    }
    
    /**
     * Check if user has specific role
     * @param sessionId Session ID
     * @param requiredRole Role to check
     * @return true if user has the role, false otherwise
     */
    public boolean hasRole(String sessionId, String requiredRole) {
        UserSession session = getUserSession(sessionId);
        if (session == null) {
            return false;
        }
        
        return requiredRole.equals(session.getRole());
    }
    
    /**
     * Check if user is admin
     * @param sessionId Session ID
     * @return true if user is admin, false otherwise
     */
    public boolean isAdmin(String sessionId) {
        return hasRole(sessionId, ROLE_ADMIN);
    }
    
    /**
     * Get redirect URL after login based on user role
     * @param userRole User's role
     * @return Redirect URL
     */
    public String getRedirectURLByRole(String userRole) {
        switch (userRole) {
            case ROLE_ADMIN:
                return "/admin/dashboard";
            case ROLE_HR:
                return "/hr/dashboard";
            case ROLE_BARISTA:
                return "/barista/dashboard";
            case ROLE_INVENTORY:
                return "/inventory/dashboard";
            default:
                return "/dashboard";
        }
    }
    
    /**
     * Generate unique session ID
     * @return Session ID string
     */
    private String generateSessionId() {
        return "SESSION_" + System.currentTimeMillis() + "_" + 
               (int)(Math.random() * 10000);
    }
    
    /**
     * Inner class to represent user session
     */
    public static class UserSession {
        private String sessionId;
        private User user;
        private String role;
        private long createdTime;
        private long lastAccessTime;
        
        public UserSession(String sessionId, User user, String role) {
            this.sessionId = sessionId;
            this.user = user;
            this.role = role;
            this.createdTime = System.currentTimeMillis();
            this.lastAccessTime = this.createdTime;
        }
        
        // Getters and setters
        public String getSessionId() { return sessionId; }
        public User getUser() { return user; }
        public String getRole() { return role; }
        public long getCreatedTime() { return createdTime; }
        public long getLastAccessTime() { return lastAccessTime; }
        
        public void updateLastAccess() {
            this.lastAccessTime = System.currentTimeMillis();
        }
    }
}