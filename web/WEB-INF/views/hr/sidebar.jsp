<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- HR Sidebar Navigation -->
<nav class="hr-sidebar">
    <div class="hr-sidebar-header">
        <h2><i class="fas fa-users-cog"></i> HR Panel</h2>
        <p>Human Resources Management</p>
    </div>
    
    <div class="hr-nav">
        <!-- Dashboard -->
        <a href="${pageContext.request.contextPath}/hr/dashboard" 
           class="hr-nav-item ${param.page == 'dashboard' || empty param.page ? 'active' : ''}">
            <i class="fas fa-tachometer-alt"></i>
            Dashboard
        </a>
        
        <!-- User Management -->
        <div class="hr-nav-group">
            <div class="hr-nav-group-title">
                <i class="fas fa-users"></i> User Management
            </div>
            
            <a href="${pageContext.request.contextPath}/hr/user-list" 
               class="hr-nav-item ${param.page == 'user-list' ? 'active' : ''}">
                <i class="fas fa-list"></i>
                View User List
            </a>
            
            <a href="${pageContext.request.contextPath}/hr/add-user" 
               class="hr-nav-item ${param.page == 'add-user' ? 'active' : ''}">
                <i class="fas fa-user-plus"></i>
                Add New User
            </a>
            
            <a href="${pageContext.request.contextPath}/hr/edit-user" 
               class="hr-nav-item ${param.page == 'edit-user' ? 'active' : ''}">
                <i class="fas fa-user-edit"></i>
                Edit User
            </a>
        </div>
        
        <!-- Role & Permission Management -->
        <div class="hr-nav-group">
            <div class="hr-nav-group-title">
                <i class="fas fa-shield-alt"></i> Roles & Permissions
            </div>
            
            <a href="${pageContext.request.contextPath}/hr/roles" 
               class="hr-nav-item ${param.page == 'roles' ? 'active' : ''}">
                <i class="fas fa-user-tag"></i>
                Manage Roles
            </a>
            
            <a href="${pageContext.request.contextPath}/hr/permissions" 
               class="hr-nav-item ${param.page == 'permissions' ? 'active' : ''}">
                <i class="fas fa-key"></i>
                Permissions
            </a>
        </div>
        
        <!-- System Settings -->
        <div class="hr-nav-group">
            <div class="hr-nav-group-title">
                <i class="fas fa-cogs"></i> System Settings
            </div>
            
            <a href="${pageContext.request.contextPath}/hr/settings" 
               class="hr-nav-item ${param.page == 'settings' ? 'active' : ''}">
                <i class="fas fa-sliders-h"></i>
                System Settings
            </a>
            
            <a href="${pageContext.request.contextPath}/hr/backup" 
               class="hr-nav-item ${param.page == 'backup' ? 'active' : ''}">
                <i class="fas fa-database"></i>
                Backup & Restore
            </a>
        </div>
        
        <!-- Divider -->
        <div class="hr-nav-divider"></div>
        
        <!-- User Profile -->
        <a href="${pageContext.request.contextPath}/hr/profile" 
           class="hr-nav-item ${param.page == 'profile' ? 'active' : ''}">
            <i class="fas fa-user-circle"></i>
            My Profile
        </a>
        
        <!-- Logout -->
        <a href="${pageContext.request.contextPath}/logout" class="hr-nav-item logout">
            <i class="fas fa-sign-out-alt"></i>
            Logout
        </a>
    </div>
</nav>

<style>
/* Sidebar group styles */
.hr-nav-group {
    margin: 15px 0;
}

.hr-nav-group-title {
    padding: 8px 15px;
    font-size: 12px;
    font-weight: 600;
    color: rgba(255, 255, 255, 0.7);
    text-transform: uppercase;
    letter-spacing: 0.5px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    margin-bottom: 5px;
}

.hr-nav-group .hr-nav-item {
    padding-left: 35px;
    font-size: 14px;
}

.hr-nav-divider {
    height: 1px;
    background: rgba(255, 255, 255, 0.1);
    margin: 20px 0;
}

.hr-nav-item.logout {
    margin-top: 10px;
    border-top: 1px solid rgba(255, 255, 255, 0.1);
    padding-top: 15px;
}

.hr-nav-item.logout:hover {
    background-color: #dc3545;
}
</style>