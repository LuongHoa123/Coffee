<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- Admin Sidebar Navigation -->
<nav class="admin-sidebar">
    <div class="admin-sidebar-header">
        <h2><i class="fas fa-shield-alt"></i> Admin Panel</h2>
        <p>System Administration</p>
    </div>
    
    <div class="admin-nav">
        <!-- Dashboard -->
        <a href="${pageContext.request.contextPath}/admin/dashboard" 
           class="admin-nav-item ${param.page == 'dashboard' || empty param.page ? 'active' : ''}">
            <i class="fas fa-tachometer-alt"></i>
            Dashboard
        </a>
        
        <!-- User Management -->
        <div class="admin-nav-group">
            <div class="admin-nav-group-title">
                <i class="fas fa-users"></i> User Management
            </div>
            
            <a href="${pageContext.request.contextPath}/admin/user-list" 
               class="admin-nav-item ${param.page == 'user-list' ? 'active' : ''}">
                <i class="fas fa-list"></i>
                All Users
            </a>
            
            <a href="${pageContext.request.contextPath}/admin/add-user" 
               class="admin-nav-item ${param.page == 'add-user' ? 'active' : ''}">
                <i class="fas fa-user-plus"></i>
                Add New User
            </a>
            
            <a href="${pageContext.request.contextPath}/admin/user-roles" 
               class="admin-nav-item ${param.page == 'user-roles' ? 'active' : ''}">
                <i class="fas fa-user-tag"></i>
                Manage Roles
            </a>
        </div>
        
        <!-- System Management -->
        <div class="admin-nav-group">
            <div class="admin-nav-group-title">
                <i class="fas fa-cogs"></i> System Management
            </div>
            
            <a href="${pageContext.request.contextPath}/admin/settings" 
               class="admin-nav-item ${param.page == 'settings' ? 'active' : ''}">
                <i class="fas fa-sliders-h"></i>
                System Settings
            </a>
            
            
        </div>
        
        
    </div>
    
    <!-- Sidebar Footer -->
    <div class="admin-sidebar-footer">
        <div class="admin-user-info">
            <i class="fas fa-user-shield"></i>
            <span>Administrator</span>
        </div>
        <a href="${pageContext.request.contextPath}/logout" class="admin-logout-btn">
            <i class="fas fa-sign-out-alt"></i>
            Logout
        </a>
    </div>
</nav>