<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- Inventory Sidebar Navigation -->
<nav class="inventory-sidebar">
    <div class="inventory-sidebar-header">
        <h2><i class="fas fa-boxes"></i> Inventory Panel</h2>
        <p>Inventory Management System</p>
    </div>
    
    <div class="inventory-nav">
        <!-- Dashboard -->
        <a href="${pageContext.request.contextPath}/inventory/dashboard" 
           class="inventory-nav-item ${param.page == 'dashboard' || empty param.page ? 'active' : ''}">
            <i class="fas fa-tachometer-alt"></i>
            Dashboard
        </a>
        
        <!-- Ingredient Management -->
        <div class="inventory-nav-group">
            <div class="inventory-nav-group-title">
                <i class="fas fa-seedling"></i> Ingredient Management
            </div>
            
            <a href="${pageContext.request.contextPath}/inventory/ingredient-list" 
               class="inventory-nav-item ${param.page == 'ingredients' ? 'active' : ''}">
                <i class="fas fa-list"></i>
                View Ingredients
            </a>
            
         
        </div>
        
        <!-- Purchase Orders -->
        <div class="inventory-nav-group">
            <div class="inventory-nav-group-title">
                <i class="fas fa-shopping-cart"></i> Purchase Orders
            </div>
            
            <a href="${pageContext.request.contextPath}/inventory/po-list" 
               class="inventory-nav-item ${param.page == 'purchase-orders' ? 'active' : ''}">
                <i class="fas fa-list"></i>
                View Purchase Orders
            </a>
            
           
        </div>
        <div class="inventory-sidebar-footer">
        <div class="inventory-user-info">
            <i class="fas fa-user-tie"></i>
            <span>Inventory Manager</span>
        </div>
        <a href="${pageContext.request.contextPath}/logout" class="inventory-logout-btn">
            <i class="fas fa-sign-out-alt"></i>
            Logout
        </a>
    </div>
       
        </div>
        
      
        
      
    </div>
    
    <!-- Sidebar Footer -->
    
</nav>