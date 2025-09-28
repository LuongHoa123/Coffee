<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard.css">

<!-- Dashboard Content -->
<div class="dashboard-container">
    <!-- Welcome Section -->
    <div class="welcome-section">
        <h2>Welcome to HR Dashboard</h2>
        <p>Manage users, monitor system activity, and maintain organizational structure.</p>
    </div>

    <!-- Statistics Cards -->
    <div class="hr-stats-grid">
        <!-- User Management Statistics -->
        <div class="hr-stat-card primary">
            <div class="hr-stat-number" id="totalUsers">${totalUsers != null ? totalUsers : 0}</div>
            <div class="hr-stat-label"><i class="fas fa-users"></i> Total Users</div>
        </div>
        
        <div class="hr-stat-card success">
            <div class="hr-stat-number" id="activeUsers">${activeUsers != null ? activeUsers : 0}</div>
            <div class="hr-stat-label"><i class="fas fa-user-check"></i> Active Users</div>
        </div>
        
        <div class="hr-stat-card info">
            <div class="hr-stat-number" id="totalShops">${totalShops != null ? totalShops : 0}</div>
            <div class="hr-stat-label"><i class="fas fa-store"></i> Coffee Shops</div>
        </div>
        
        <div class="hr-stat-card warning">
            <div class="hr-stat-number" id="totalOrdersToday">${totalOrdersToday != null ? totalOrdersToday : 0}</div>
            <div class="hr-stat-label"><i class="fas fa-shopping-cart"></i> Orders Today</div>
        </div>
    </div>

    <!-- Secondary Statistics -->
    <div class="hr-stats-grid secondary">
        <div class="hr-stat-card secondary">
            <div class="hr-stat-number" id="completedOrdersToday">${completedOrdersToday != null ? completedOrdersToday : 0}</div>
            <div class="hr-stat-label"><i class="fas fa-check-circle"></i> Completed Today</div>
        </div>
        
        <div class="hr-stat-card secondary">
            <div class="hr-stat-number" id="totalIngredients">${totalIngredients != null ? totalIngredients : 0}</div>
            <div class="hr-stat-label"><i class="fas fa-boxes"></i> Total Ingredients</div>
        </div>
        
        <div class="hr-stat-card danger">
            <div class="hr-stat-number" id="lowStockIngredients">${lowStockIngredients != null ? lowStockIngredients : 0}</div>
            <div class="hr-stat-label"><i class="fas fa-exclamation-triangle"></i> Low Stock</div>
        </div>
        
        <div class="hr-stat-card info">
            <div class="hr-stat-number" id="newUsersThisMonth">${newUsersThisMonth != null ? newUsersThisMonth : 0}</div>
            <div class="hr-stat-label"><i class="fas fa-user-plus"></i> New Users This Month</div>
        </div>
    </div>

    <!-- Quick Actions -->
    <div class="quick-actions">
        <div class="hr-card">
            <div class="hr-card-header">
                <h3 class="hr-card-title">Quick Actions</h3>
            </div>
            <div class="actions-grid">
                <a href="${pageContext.request.contextPath}/hr/users" class="action-item">
                    <i class="fas fa-users"></i>
                    <span>Quản Lý Người Dùng</span>
                </a>
                
                <a href="${pageContext.request.contextPath}/hr/users/add" class="action-item">
                    <i class="fas fa-user-plus"></i>
                    <span>Thêm Người Dùng</span>
                </a>
                
                <a href="${pageContext.request.contextPath}/hr/shops" class="action-item">
                    <i class="fas fa-store"></i>
                    <span>Quản Lý Cửa Hàng</span>
                </a>
                
                <a href="${pageContext.request.contextPath}/hr/reports" class="action-item">
                    <i class="fas fa-chart-bar"></i>
                    <span>Báo Cáo</span>
                </a>
                
                <a href="${pageContext.request.contextPath}/hr/settings" class="action-item">
                    <i class="fas fa-cog"></i>
                    <span>Cài Đặt Hệ Thống</span>
                </a>
                
                <a href="${pageContext.request.contextPath}/hr/backup" class="action-item">
                    <i class="fas fa-database"></i>
                    <span>Sao Lưu Dữ Liệu</span>
                </a>
            </div>
        </div>
    </div>

    <!-- Recent Users and Role Distribution -->
    <div class="dashboard-grid">
        <!-- Recent Users -->
        <div class="hr-card">
            <div class="hr-card-header">
                <h3 class="hr-card-title">Recent Users</h3>
                <button onclick="refreshRecentUsers()" class="hr-btn hr-btn-secondary">
                    <i class="fas fa-sync-alt"></i> Refresh
                </button>
            </div>
            
            <div class="recent-users-list" id="recentUsersList">
                <c:choose>
                    <c:when test="${not empty recentUsers}">
                        <c:forEach var="user" items="${recentUsers}">
                            <div class="user-item">
                                <div class="user-avatar">
                                    <c:choose>
                                        <c:when test="${not empty user.fullName}">
                                            ${user.fullName.substring(0,1).toUpperCase()}
                                        </c:when>
                                        <c:otherwise>U</c:otherwise>
                                    </c:choose>
                                </div>
                                <div class="user-info">
                                    <div class="user-name">${user.fullName != null ? user.fullName : user.username}</div>
                                    <div class="user-email">${user.email}</div>
                                    <div class="user-role">${user.role != null ? user.role : 'User'}</div>
                                </div>
                                <div class="user-status">
                                    <c:choose>
                                        <c:when test="${user.active}">
                                            <span class="status-badge active">Active</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="status-badge inactive">Inactive</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div class="no-data">
                            <i class="fas fa-users"></i>
                            <p>No recent users found</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- User Role Distribution -->
        <div class="hr-card">
            <div class="hr-card-header">
                <h3 class="hr-card-title">User Distribution by Role</h3>
            </div>
            
            <div class="role-distribution" id="roleDistribution">
                <c:choose>
                    <c:when test="${not empty usersByRole}">
                        <c:forEach var="roleEntry" items="${usersByRole}">
                            <div class="role-item">
                                <div class="role-name">${roleEntry.key}</div>
                                <div class="role-count">
                                    <div class="role-bar">
                                        <div class="role-progress" style="width: ${(roleEntry.value * 100) / totalUsers}%"></div>
                                    </div>
                                    <span class="count">${roleEntry.value}</span>
                                </div>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div class="no-data">
                            <i class="fas fa-chart-pie"></i>
                            <p>No role data available</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <!-- User Growth Chart -->
    <div class="hr-card">
        <div class="hr-card-header">
            <h3 class="hr-card-title">User Growth Over Time</h3>
            <div class="chart-controls">
                <select id="growthPeriod" onchange="this.dataset.manualUpdate='true'; updateGrowthChart()" class="hr-form-control">
                    <option value="3months">Last 3 Months</option>
                    <option value="6months" selected>Last 6 Months</option>
                    <option value="1year">Last Year</option>
                </select>
               
            </div>
        </div>
        
        <div class="chart-container">
            <canvas id="userGrowthChart" width="400" height="200"></canvas>
        </div>
    </div>

  
</div>

<!-- Include Dashboard JavaScript -->
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script src="${pageContext.request.contextPath}/js/dashboard.js"></script>

<script>
    // Initialize dashboard with server data
    const dashboardData = {
        totalUsers: <c:out value="${totalUsers != null ? totalUsers : 0}" />,
        activeUsers: <c:out value="${activeUsers != null ? activeUsers : 0}" />,
        inactiveUsers: <c:out value="${inactiveUsers != null ? inactiveUsers : 0}" />,
        newUsersThisMonth: <c:out value="${newUsersThisMonth != null ? newUsersThisMonth : 0}" />,
        totalShops: <c:out value="${totalShops != null ? totalShops : 0}" />,
        totalOrdersToday: <c:out value="${totalOrdersToday != null ? totalOrdersToday : 0}" />,
        completedOrdersToday: <c:out value="${completedOrdersToday != null ? completedOrdersToday : 0}" />,
        totalIngredients: <c:out value="${totalIngredients != null ? totalIngredients : 0}" />,
        lowStockIngredients: <c:out value="${lowStockIngredients != null ? lowStockIngredients : 0}" />,
        userGrowthData: <c:out value="${userGrowthData != null ? userGrowthData : '[]'}" escapeXml="false" />
    };
    
    console.log('Dashboard data loaded:', dashboardData);
    
    // Initialize dashboard when page loads
    document.addEventListener('DOMContentLoaded', function() {
        if (typeof initDashboard === 'function') {
            initDashboard(dashboardData);
        } else {
            console.error('initDashboard function not found');
        }
    });
</script>