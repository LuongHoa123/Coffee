<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- Barista Sidebar Navigation -->
<nav class="barista-sidebar">
    <div class="barista-sidebar-header">
        <h2><i class="fas fa-coffee"></i> Barista Panel</h2>
        <p>Coffee Brewing & Order Management</p>
    </div>
    
    <div class="barista-nav">
        <!-- Dashboard -->
        <a href="${pageContext.request.contextPath}/barista/dashboard" 
           class="barista-nav-item ${param.page == 'dashboard' || empty param.page ? 'active' : ''}">
            <i class="fas fa-tachometer-alt"></i>
            Dashboard
        </a>
        
        <!-- Order Management -->
        <div class="barista-nav-group">
            <div class="barista-nav-group-title">
                <i class="fas fa-shopping-cart"></i> Order Management
            </div>
            
            <a href="${pageContext.request.contextPath}/barista/orders" 
               class="barista-nav-item ${param.page == 'orders' ? 'active' : ''}">
                <i class="fas fa-list-alt"></i>
                View Orders
            </a>
            
           
        </div>
        
        
        
    </div>
    
    <!-- User Profile Section -->
    <div class="barista-sidebar-footer">
        <div class="barista-user-info">
            <i class="fas fa-user"></i>
            <div class="user-details">
                <span class="user-name">${currentUser.fullName}</span>
                <span class="user-role">Barista</span>
            </div>
        </div>
        
        <div class="barista-quick-actions">
            <a href="${pageContext.request.contextPath}/logout" class="quick-action-btn logout-btn" title="Logout">
                <i class="fas fa-sign-out-alt"></i>
            </a>
        </div>
        
       
</nav>

<!-- Sidebar Scripts -->
<script>
    // Update last updated time
    function updateLastUpdated() {
        const now = new Date();
        const timeString = now.toLocaleTimeString('vi-VN', { 
            hour: '2-digit', 
            minute: '2-digit' 
        });
        document.getElementById('lastUpdated').textContent = timeString;
    }
    
    // Update every 30 seconds
    setInterval(updateLastUpdated, 30000);
    updateLastUpdated(); // Initial call
    
    // Theme toggle function
    function toggleTheme() {
        document.body.classList.toggle('dark-theme');
        
        // Store preference
        const isDark = document.body.classList.contains('dark-theme');
        localStorage.setItem('baristaDarkTheme', isDark);
    }
    
    // Load saved theme preference
    if (localStorage.getItem('baristaDarkTheme') === 'true') {
        document.body.classList.add('dark-theme');
    }
    
    // Settings function
    function showSettings() {
        alert('Settings panel coming soon!');
    }
</script>