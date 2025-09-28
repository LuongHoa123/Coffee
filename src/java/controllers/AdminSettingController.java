package controllers;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.annotation.WebServlet;
import models.User;
import models.Setting;
import dao.SettingDAO;
import java.util.List;
import java.util.Map;

/**
 * Admin Setting Controller
 * Handles setting management operations for admin
 */
@WebServlet(name = "AdminSettingController", urlPatterns = {"/admin/settings"})
public class AdminSettingController extends HttpServlet {
    
    private SettingDAO settingDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        settingDAO = new SettingDAO();
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     * Displays settings list and forms
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check authentication and admin role
        if (!isAdminAuthenticated(request, response)) {
            return;
        }
        
        String action = request.getParameter("action");
        action = action != null ? action : "list";
        
        try {
            switch (action) {
                case "list":
                    handleListSettings(request, response);
                    break;
                case "add":
                    handleAddSettingForm(request, response);
                    break;
                case "edit":
                    handleEditSettingForm(request, response);
                    break;
                case "view":
                    handleViewSetting(request, response);
                    break;
                default:
                    handleListSettings(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error processing request: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * Processes setting operations
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check authentication and admin role
        if (!isAdminAuthenticated(request, response)) {
            return;
        }
        
        String action = request.getParameter("action");
        
        try {
            switch (action != null ? action : "") {
                case "create":
                    handleCreateSetting(request, response);
                    break;
                case "update":
                    handleUpdateSetting(request, response);
                    break;
                case "delete":
                    handleDeleteSetting(request, response);
                    break;
                case "toggle":
                    handleToggleSetting(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/admin/settings");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error processing request: " + e.getMessage());
            doGet(request, response);
        }
    }
    
    /**
     * Check if user is authenticated and has admin role
     */
    private boolean isAdminAuthenticated(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        
        User currentUser = (User) session.getAttribute("user");
        
        // Check if user has Admin role (RoleID 2 based on SQL data)
        if (currentUser.getRoleID() != 2) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Admin privileges required.");
            return false;
        }
        
        return true;
    }
    
    /**
     * Handle settings list display
     */
    private void handleListSettings(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String searchKeyword = request.getParameter("search");
        String filterType = request.getParameter("type");
        
        List<Setting> settings;
        
        if ((searchKeyword != null && !searchKeyword.trim().isEmpty()) || 
            (filterType != null && !filterType.trim().isEmpty() && !"all".equals(filterType))) {
            // Search/filter settings
            settings = settingDAO.searchSettings(searchKeyword, filterType);
            request.setAttribute("searchKeyword", searchKeyword);
            request.setAttribute("selectedType", filterType);
        } else {
            // Get all settings
            settings = settingDAO.getAllSettings();
        }
        
        // Get setting types for filter dropdown
        List<String> settingTypes = settingDAO.getSettingTypes();
        
        // Get statistics
        int totalSettings = settingDAO.getTotalSettingsCount();
        Map<String, Integer> settingsCountByType = settingDAO.getSettingsCountByType();
        
        // Set attributes
        request.setAttribute("settings", settings);
        request.setAttribute("settingTypes", settingTypes);
        request.setAttribute("totalSettings", totalSettings);
        request.setAttribute("settingsCountByType", settingsCountByType);
        request.setAttribute("pageTitle", "Settings Management");
        request.setAttribute("page", "settings");
        
        // Forward to settings list view
        request.getRequestDispatcher("/WEB-INF/views/admin/settings-list.jsp")
               .forward(request, response);
    }
    
    /**
     * Handle add setting form display
     */
    private void handleAddSettingForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Get setting types for dropdown
        List<String> settingTypes = settingDAO.getSettingTypes();
        
        request.setAttribute("settingTypes", settingTypes);
        request.setAttribute("pageTitle", "Add New Setting");
        request.setAttribute("page", "settings");
        request.setAttribute("action", "add");
        
        // Forward to setting form view
        request.getRequestDispatcher("/WEB-INF/views/admin/setting-form.jsp")
               .forward(request, response);
    }
    
    /**
     * Handle edit setting form display
     */
    private void handleEditSettingForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        
        if (idParam == null || idParam.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Setting ID is required");
            handleListSettings(request, response);
            return;
        }
        
        try {
            int settingId = Integer.parseInt(idParam);
            Setting setting = settingDAO.getSettingById(settingId);
            
            if (setting == null) {
                request.setAttribute("errorMessage", "Setting not found");
                handleListSettings(request, response);
                return;
            }
            
            // Get setting types for dropdown
            List<String> settingTypes = settingDAO.getSettingTypes();
            
            request.setAttribute("setting", setting);
            request.setAttribute("settingTypes", settingTypes);
            request.setAttribute("pageTitle", "Edit Setting");
            request.setAttribute("page", "settings");
            request.setAttribute("action", "edit");
            
            // Forward to setting form view
            request.getRequestDispatcher("/WEB-INF/views/admin/setting-form.jsp")
                   .forward(request, response);
                   
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid setting ID format");
            handleListSettings(request, response);
        }
    }
    
    /**
     * Handle view setting details
     */
    private void handleViewSetting(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        
        if (idParam == null || idParam.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Setting ID is required");
            handleListSettings(request, response);
            return;
        }
        
        try {
            int settingId = Integer.parseInt(idParam);
            Setting setting = settingDAO.getSettingById(settingId);
            
            if (setting == null) {
                request.setAttribute("errorMessage", "Setting not found");
                handleListSettings(request, response);
                return;
            }
            
            request.setAttribute("setting", setting);
            request.setAttribute("pageTitle", "Setting Details");
            request.setAttribute("page", "settings");
            
            // Forward to setting view
            request.getRequestDispatcher("/WEB-INF/views/admin/setting-view.jsp")
                   .forward(request, response);
                   
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid setting ID format");
            handleListSettings(request, response);
        }
    }
    
    /**
     * Handle create new setting
     */
    private void handleCreateSetting(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String type = request.getParameter("type");
        String value = request.getParameter("value");
        String description = request.getParameter("description");
        String activeParam = request.getParameter("isActive");
        
        // Validation
        if (type == null || type.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Type is required");
            handleAddSettingForm(request, response);
            return;
        }
        
        if (value == null || value.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Value is required");
            handleAddSettingForm(request, response);
            return;
        }
        
        // Check if value already exists for this type
        if (settingDAO.isValueExists(type.trim(), value.trim(), 0)) {
            request.setAttribute("errorMessage", "Value already exists for this type");
            handleAddSettingForm(request, response);
            return;
        }
        
        boolean isActive = "on".equals(activeParam) || "true".equals(activeParam);
        
        Setting newSetting = new Setting(type.trim(), value.trim(), 
                                       description != null ? description.trim() : "", 
                                       isActive);
        
        if (settingDAO.addSetting(newSetting)) {
            request.setAttribute("successMessage", "Setting created successfully");
        } else {
            request.setAttribute("errorMessage", "Failed to create setting");
        }
        
        // Redirect to settings list
        response.sendRedirect(request.getContextPath() + "/admin/settings?success=created");
    }
    
    /**
     * Handle update existing setting
     */
    private void handleUpdateSetting(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        String type = request.getParameter("type");
        String value = request.getParameter("value");
        String description = request.getParameter("description");
        String activeParam = request.getParameter("isActive");
        
        if (idParam == null || idParam.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Setting ID is required");
            handleListSettings(request, response);
            return;
        }
        
        try {
            int settingId = Integer.parseInt(idParam);
            
            // Validation
            if (type == null || type.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Type is required");
                handleEditSettingForm(request, response);
                return;
            }
            
            if (value == null || value.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Value is required");
                handleEditSettingForm(request, response);
                return;
            }
            
            // Check if value already exists for this type (excluding current setting)
            if (settingDAO.isValueExists(type.trim(), value.trim(), settingId)) {
                request.setAttribute("errorMessage", "Value already exists for this type");
                handleEditSettingForm(request, response);
                return;
            }
            
            boolean isActive = "on".equals(activeParam) || "true".equals(activeParam);
            
            Setting setting = new Setting(settingId, type.trim(), value.trim(), 
                                         description != null ? description.trim() : "", 
                                         isActive);
            
            if (settingDAO.updateSetting(setting)) {
                response.sendRedirect(request.getContextPath() + "/admin/settings?success=updated");
            } else {
                request.setAttribute("errorMessage", "Failed to update setting");
                handleEditSettingForm(request, response);
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid setting ID format");
            handleListSettings(request, response);
        }
    }
    
    /**
     * Handle delete setting
     */
    private void handleDeleteSetting(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        
        if (idParam == null || idParam.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Setting ID is required");
            handleListSettings(request, response);
            return;
        }
        
        try {
            int settingId = Integer.parseInt(idParam);
            
            if (settingDAO.deleteSetting(settingId)) {
                response.sendRedirect(request.getContextPath() + "/admin/settings?success=deleted");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/settings?error=delete_failed");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/settings?error=invalid_id");
        }
    }
    
    /**
     * Handle toggle setting active status
     */
    private void handleToggleSetting(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/settings?error=invalid_id");
            return;
        }
        
        try {
            int settingId = Integer.parseInt(idParam);
            Setting setting = settingDAO.getSettingById(settingId);
            
            if (setting != null) {
                setting.setActive(!setting.isActive());
                
                if (settingDAO.updateSetting(setting)) {
                    response.sendRedirect(request.getContextPath() + "/admin/settings?success=toggled");
                } else {
                    response.sendRedirect(request.getContextPath() + "/admin/settings?error=toggle_failed");
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/settings?error=not_found");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/settings?error=invalid_id");
        }
    }

    /**
     * Returns a short description of the servlet.
     */
    @Override
    public String getServletInfo() {
        return "Admin Setting Controller - Manages system settings";
    }
}