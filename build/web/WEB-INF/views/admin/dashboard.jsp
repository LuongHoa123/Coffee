<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle} - ISP392 Admin Panel</title>
    
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    
    <!-- Admin Dashboard CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-dashboard.css">
    
    <!-- Chart.js for analytics -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body class="admin-body">
    
    <!-- Admin Sidebar -->
    <jsp:include page="sidebar.jsp">
        <jsp:param name="page" value="${param.page}" />
    </jsp:include>
    
    <!-- Main Content Area -->
    <main class="admin-main-content">
        
        <!-- Top Header -->
        <header class="admin-header">
            <div class="admin-header-left">
                <button class="admin-sidebar-toggle" id="sidebarToggle">
                    <i class="fas fa-bars"></i>
                </button>
                <h1 class="admin-page-title">
                    <i class="fas fa-tachometer-alt"></i>
                    Admin Dashboard
                </h1>
            </div>
            
            <div class="admin-header-right">
                <div class="admin-header-info">
                    <span class="admin-welcome">Welcome, <strong>Administrator</strong></span>
                    <span class="admin-current-time" id="currentTime"></span>
                </div>
                <div class="admin-header-actions">
                    <button class="admin-refresh-btn" onclick="refreshDashboard()">
                        <i class="fas fa-sync-alt"></i>
                        Refresh
                    </button>
                </div>
            </div>
        </header>
        
        <!-- Dashboard Content -->
        <div class="admin-dashboard-content">
            
            <!-- Success/Error Messages -->
            <c:if test="${not empty successMessage}">
                <div class="admin-alert admin-alert-success">
                    <i class="fas fa-check-circle"></i>
                    ${successMessage}
                </div>
            </c:if>
            
            <c:if test="${not empty errorMessage}">
                <div class="admin-alert admin-alert-error">
                    <i class="fas fa-exclamation-circle"></i>
                    ${errorMessage}
                </div>
            </c:if>
            
            <c:if test="${not empty infoMessage}">
                <div class="admin-alert admin-alert-info">
                    <i class="fas fa-info-circle"></i>
                    ${infoMessage}
                </div>
            </c:if>
            
            <!-- Statistics Cards Row -->
            <div class="admin-stats-grid">
                
                <!-- Total Users Card -->
                <div class="admin-stat-card">
                    <div class="admin-stat-icon">
                        <i class="fas fa-users"></i>
                    </div>
                    <div class="admin-stat-content">
                        <h3 class="admin-stat-number">${totalUsers != null ? totalUsers : 0}</h3>
                        <p class="admin-stat-label">Total Users</p>
                    </div>
                </div>
                
                <!-- Active Users Card -->
                <div class="admin-stat-card">
                    <div class="admin-stat-icon admin-stat-icon-success">
                        <i class="fas fa-user-check"></i>
                    </div>
                    <div class="admin-stat-content">
                        <h3 class="admin-stat-number">${activeUsers != null ? activeUsers : 0}</h3>
                        <p class="admin-stat-label">Active Users</p>
                    </div>
                </div>
                
                <!-- Inactive Users Card -->
                <div class="admin-stat-card">
                    <div class="admin-stat-icon admin-stat-icon-warning">
                        <i class="fas fa-user-times"></i>
                    </div>
                    <div class="admin-stat-content">
                        <h3 class="admin-stat-number">${inactiveUsers != null ? inactiveUsers : 0}</h3>
                        <p class="admin-stat-label">Inactive Users</p>
                    </div>
                </div>
                
                <!-- New Users This Month -->
                <div class="admin-stat-card">
                    <div class="admin-stat-icon admin-stat-icon-info">
                        <i class="fas fa-user-plus"></i>
                    </div>
                    <div class="admin-stat-content">
                        <h3 class="admin-stat-number">${usersByStatus['newThisMonth'] != null ? usersByStatus['newThisMonth'] : 0}</h3>
                        <p class="admin-stat-label">New This Month</p>
                    </div>
                </div>
                
            </div>
            
            <!-- Charts and Analytics Row -->
            <div class="admin-analytics-grid">
                
                <!-- User Role Distribution Chart -->
                <div class="admin-chart-card">
                    <div class="admin-chart-header">
                        <h3><i class="fas fa-chart-pie"></i> User Distribution by Role</h3>
                    </div>
                    <div class="admin-chart-content">
                        <canvas id="roleDistributionChart"></canvas>
                    </div>
                </div>
                
                <!-- User Status Chart -->
                <div class="admin-chart-card">
                    <div class="admin-chart-header">
                        <h3><i class="fas fa-chart-doughnut"></i> User Status Overview</h3>
                    </div>
                    <div class="admin-chart-content">
                        <canvas id="statusChart"></canvas>
                    </div>
                </div>
                
            </div>
            
            <!-- Recent Users and Quick Actions Row -->
            <div class="admin-content-grid">
                
                <!-- Recent Users Table -->
                <div class="admin-content-card admin-content-card-large">
                    <div class="admin-card-header">
                        <h3><i class="fas fa-clock"></i> Recent Users</h3>
                        <a href="${pageContext.request.contextPath}/admin/user-list" class="admin-btn admin-btn-sm">
                            View All Users
                        </a>
                    </div>
                    <div class="admin-card-content">
                        <c:choose>
                            <c:when test="${not empty recentUsers}">
                                <div class="admin-table-responsive">
                                    <table class="admin-table">
                                        <thead>
                                            <tr>
                                                <th>ID</th>
                                                <th>Name</th>
                                                <th>Email</th>
                                                <th>Role</th>
                                                <th>Status</th>
                                                <th>Actions</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="user" items="${recentUsers}" varStatus="status">
                                                <tr>
                                                    <td>${user.userID}</td>
                                                    <td>
                                                        <div class="admin-user-info-cell">
                                                            <i class="fas fa-user-circle admin-user-avatar"></i>
                                                            ${user.fullName}
                                                        </div>
                                                    </td>
                                                    <td>${user.email}</td>
                                                    <td>
                                                        <span class="admin-role-badge">
                                                            ${user.role != null ? user.role : 'User'}
                                                        </span>
                                                    </td>
                                                    <td>
                                                        <span class="admin-status-badge ${user.active ? 'admin-status-active' : 'admin-status-inactive'}">
                                                            ${user.active ? 'Active' : 'Inactive'}
                                                        </span>
                                                    </td>
                                                    <td>
                                                        <div class="admin-action-buttons">
                                                            <button class="admin-btn-icon admin-btn-view" 
                                                                    onclick="viewUser(${user.userID})" 
                                                                    title="View User">
                                                                <i class="fas fa-eye"></i>
                                                            </button>
                                                            <button class="admin-btn-icon admin-btn-edit" 
                                                                    onclick="editUser(${user.userID})" 
                                                                    title="Edit User">
                                                                <i class="fas fa-edit"></i>
                                                            </button>
                                                        </div>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="admin-empty-state">
                                    <i class="fas fa-users"></i>
                                    <p>No recent users found</p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                
                <!-- Quick Actions Panel -->
                <div class="admin-content-card">
                    <div class="admin-card-header">
                        <h3><i class="fas fa-bolt"></i> Quick Actions</h3>
                    </div>
                    <div class="admin-card-content">
                        <div class="admin-quick-actions">
                            
                            <a href="${pageContext.request.contextPath}/admin/add-user" class="admin-quick-action-btn">
                                <i class="fas fa-user-plus"></i>
                                <span>Add New User</span>
                            </a>
                            
                            <a href="${pageContext.request.contextPath}/admin/user-list" class="admin-quick-action-btn">
                                <i class="fas fa-list"></i>
                                <span>Manage Users</span>
                            </a>
                            
                            <a href="${pageContext.request.contextPath}/admin/settings" class="admin-quick-action-btn">
                                <i class="fas fa-cogs"></i>
                                <span>System Settings</span>
                            </a>
                            
                            <a href="${pageContext.request.contextPath}/admin/logs" class="admin-quick-action-btn">
                                <i class="fas fa-file-alt"></i>
                                <span>View Logs</span>
                            </a>
                            
                            <a href="${pageContext.request.contextPath}/admin/backup" class="admin-quick-action-btn">
                                <i class="fas fa-database"></i>
                                <span>Backup System</span>
                            </a>
                            
                            <button class="admin-quick-action-btn" onclick="clearCache()">
                                <i class="fas fa-sync"></i>
                                <span>Clear Cache</span>
                            </button>
                            
                        </div>
                    </div>
                </div>
                
            </div>
            
            <!-- System Information Row -->
            <div class="admin-content-grid">
                
                <!-- System Info Card -->
                <div class="admin-content-card">
                    <div class="admin-card-header">
                        <h3><i class="fas fa-info-circle"></i> System Information</h3>
                    </div>
                    <div class="admin-card-content">
                        <div class="admin-system-info">
                            <div class="admin-info-item">
                                <strong>System Version:</strong>
                                <span>ISP392 v1.0.0</span>
                            </div>
                            <div class="admin-info-item">
                                <strong>Database:</strong>
                                <span>Connected</span>
                                <span class="admin-status-indicator admin-status-success"></span>
                            </div>
                            <div class="admin-info-item">
                                <strong>Server Status:</strong>
                                <span>Running</span>
                                <span class="admin-status-indicator admin-status-success"></span>
                            </div>
                            <div class="admin-info-item">
                                <strong>Last Backup:</strong>
                                <span>Not Available</span>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- Recent Activity Card -->
                <div class="admin-content-card admin-content-card-large">
                    <div class="admin-card-header">
                        <h3><i class="fas fa-history"></i> Recent Activity</h3>
                        <a href="${pageContext.request.contextPath}/admin/logs" class="admin-btn admin-btn-sm">
                            View All Logs
                        </a>
                    </div>
                    <div class="admin-card-content">
                        <div class="admin-activity-list">
                            <div class="admin-activity-item">
                                <i class="fas fa-user-plus admin-activity-icon admin-activity-icon-success"></i>
                                <div class="admin-activity-content">
                                    <p><strong>New user registered</strong></p>
                                    <small>System activity - Just now</small>
                                </div>
                            </div>
                            <div class="admin-activity-item">
                                <i class="fas fa-sign-in-alt admin-activity-icon admin-activity-icon-info"></i>
                                <div class="admin-activity-content">
                                    <p><strong>Administrator logged in</strong></p>
                                    <small>Login activity - 5 minutes ago</small>
                                </div>
                            </div>
                            <div class="admin-activity-item">
                                <i class="fas fa-cogs admin-activity-icon admin-activity-icon-warning"></i>
                                <div class="admin-activity-content">
                                    <p><strong>System settings updated</strong></p>
                                    <small>Configuration - 1 hour ago</small>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                
            </div>
            
        </div>
        
    </main>
    
    <!-- Admin Dashboard JavaScript -->
    <script src="${pageContext.request.contextPath}/js/admin-dashboard.js"></script>
    
    <!-- Chart Data Script -->
    <script>
        // Pass data from server to JavaScript
        window.dashboardData = {
            usersByRole: {
                <c:forEach var="entry" items="${usersByRole}" varStatus="status">
                    '${entry.key}': ${entry.value}<c:if test="${!status.last}">,</c:if>
                </c:forEach>
            },
            usersByStatus: {
                active: ${usersByStatus['active'] != null ? usersByStatus['active'] : 0},
                inactive: ${usersByStatus['inactive'] != null ? usersByStatus['inactive'] : 0}
            }
        };
    </script>
    
</body>
</html>