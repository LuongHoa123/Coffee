<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle} - ISP392 Inventory System</title>
    
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    
    <!-- Inventory Dashboard CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/inventory-dashboard.css">
    
    <!-- Chart.js for analytics -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body class="inventory-body">
    
    <!-- Inventory Sidebar -->
    <jsp:include page="sidebar.jsp">
        <jsp:param name="page" value="${param.page}" />
    </jsp:include>
    
    <!-- Main Content Area -->
    <main class="inventory-main-content">
        
        <!-- Top Header -->
        <header class="inventory-header">
            <div class="inventory-header-left">
                <button class="inventory-sidebar-toggle" id="sidebarToggle">
                    <i class="fas fa-bars"></i>
                </button>
                <h1 class="inventory-page-title">
                    <i class="fas fa-boxes"></i>
                    Inventory Dashboard
                </h1>
            </div>
            
            <div class="inventory-header-right">
                <div class="inventory-header-info">
                    <span class="inventory-welcome">Welcome, <strong>Inventory Manager</strong></span>
                    <span class="inventory-current-time" id="currentTime"></span>
                </div>
                <div class="inventory-header-actions">
                    <button class="inventory-refresh-btn" onclick="refreshDashboard()">
                        <i class="fas fa-sync-alt"></i>
                        Refresh
                    </button>
                </div>
            </div>
        </header>
        
        <!-- Dashboard Content -->
        <div class="inventory-dashboard-content">
            
            <!-- Success/Error Messages -->
            <c:if test="${not empty successMessage}">
                <div class="inventory-alert inventory-alert-success">
                    <i class="fas fa-check-circle"></i>
                    ${successMessage}
                </div>
            </c:if>
            
            <c:if test="${not empty errorMessage}">
                <div class="inventory-alert inventory-alert-error">
                    <i class="fas fa-exclamation-circle"></i>
                    ${errorMessage}
                </div>
            </c:if>
            
            <!-- Statistics Cards Row -->
            <div class="inventory-stats-grid">
                
                <!-- Total Ingredients Card -->
                <div class="inventory-stat-card">
                    <div class="inventory-stat-icon">
                        <i class="fas fa-seedling"></i>
                    </div>
                    <div class="inventory-stat-content">
                        <h3 class="inventory-stat-number">${totalIngredients != null ? totalIngredients : 0}</h3>
                        <p class="inventory-stat-label">Total Ingredients</p>
                    </div>
                </div>
                
                <!-- Low Stock Alert Card -->
                <div class="inventory-stat-card">
                    <div class="inventory-stat-icon inventory-stat-icon-warning">
                        <i class="fas fa-exclamation-triangle"></i>
                    </div>
                    <div class="inventory-stat-content">
                        <h3 class="inventory-stat-number">${lowStockIngredients != null ? lowStockIngredients : 0}</h3>
                        <p class="inventory-stat-label">Low Stock Items</p>
                    </div>
                </div>
                
                <!-- Critical Stock Card -->
                <div class="inventory-stat-card">
                    <div class="inventory-stat-icon inventory-stat-icon-danger">
                        <i class="fas fa-times-circle"></i>
                    </div>
                    <div class="inventory-stat-content">
                        <h3 class="inventory-stat-number">${criticalStockIngredients != null ? criticalStockIngredients : 0}</h3>
                        <p class="inventory-stat-label">Critical Stock</p>
                    </div>
                </div>
                
                <!-- Purchase Orders Card -->
                <div class="inventory-stat-card">
                    <div class="inventory-stat-icon inventory-stat-icon-info">
                        <i class="fas fa-shopping-cart"></i>
                    </div>
                    <div class="inventory-stat-content">
                        <h3 class="inventory-stat-number">${totalPurchaseOrders != null ? totalPurchaseOrders : 0}</h3>
                        <p class="inventory-stat-label">Purchase Orders</p>
                    </div>
                </div>
                
                <!-- Active Suppliers Card -->
                <div class="inventory-stat-card">
                    <div class="inventory-stat-icon inventory-stat-icon-success">
                        <i class="fas fa-truck"></i>
                    </div>
                    <div class="inventory-stat-content">
                        <h3 class="inventory-stat-number">${activeSuppliers != null ? activeSuppliers : 0}</h3>
                        <p class="inventory-stat-label">Active Suppliers</p>
                    </div>
                </div>
                
                <!-- Pending Issues Card -->
                <div class="inventory-stat-card">
                    <div class="inventory-stat-icon inventory-stat-icon-warning">
                        <i class="fas fa-bug"></i>
                    </div>
                    <div class="inventory-stat-content">
                        <h3 class="inventory-stat-number">${pendingIssues != null ? pendingIssues : 0}</h3>
                        <p class="inventory-stat-label">Pending Issues</p>
                    </div>
                </div>
                
            </div>
            
            <!-- Charts and Analytics Row -->
            <div class="inventory-analytics-grid">
                
                <!-- Stock Status Chart -->
                <div class="inventory-chart-card">
                    <div class="inventory-chart-header">
                        <h3><i class="fas fa-chart-pie"></i> Stock Status Distribution</h3>
                    </div>
                    <div class="inventory-chart-content">
                        <canvas id="stockStatusChart"></canvas>
                    </div>
                </div>
                
                <!-- Purchase Order Status Chart -->
                <div class="inventory-chart-card">
                    <div class="inventory-chart-header">
                        <h3><i class="fas fa-chart-doughnut"></i> Purchase Order Status</h3>
                    </div>
                    <div class="inventory-chart-content">
                        <canvas id="purchaseOrderChart"></canvas>
                    </div>
                </div>
                
            </div>
            
            <!-- Data Tables and Quick Actions Row -->
            <div class="inventory-content-grid">
                
                <!-- Low Stock Alert Table -->
                <div class="inventory-content-card inventory-content-card-large">
                    <div class="inventory-card-header">
                        <h3><i class="fas fa-exclamation-triangle"></i> Low Stock Alert</h3>
                        <a href="${pageContext.request.contextPath}/inventory/low-stock" class="inventory-btn inventory-btn-sm">
                            View All
                        </a>
                    </div>
                    <div class="inventory-card-content">
                        <c:choose>
                            <c:when test="${not empty lowStockItems}">
                                <div class="inventory-table-responsive">
                                    <table class="inventory-table">
                                        <thead>
                                            <tr>
                                                <th>Ingredient</th>
                                                <th>Current Stock</th>
                                                <th>Unit</th>
                                                <th>Supplier</th>
                                                <th>Action</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="item" items="${lowStockItems}" varStatus="status">
                                                <tr>
                                                    <td>
                                                        <div class="inventory-item-info">
                                                            <i class="fas fa-seedling inventory-item-icon"></i>
                                                            ${item.name}
                                                        </div>
                                                    </td>
                                                    <td>
                                                        <span class="inventory-stock-warning">
                                                            ${item.currentStock}
                                                        </span>
                                                    </td>
                                                    <td>${item.unit}</td>
                                                    <td>${item.supplier}</td>
                                                    <td>
                                                        <button class="inventory-btn-icon inventory-btn-order" 
                                                                onclick="createQuickPO('${item.name}')" 
                                                                title="Quick Order">
                                                            <i class="fas fa-plus"></i>
                                                        </button>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="inventory-empty-state">
                                    <i class="fas fa-check-circle"></i>
                                    <p>All ingredients are well stocked</p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                
                <!-- Quick Actions Panel -->
                <div class="inventory-content-card">
                    <div class="inventory-card-header">
                        <h3><i class="fas fa-bolt"></i> Quick Actions</h3>
                    </div>
                    <div class="inventory-card-content">
                        <div class="inventory-quick-actions">
                            
                            <a href="${pageContext.request.contextPath}/inventory/ingredients?action=add" class="inventory-quick-action-btn">
                                <i class="fas fa-plus"></i>
                                <span>Add Ingredient</span>
                            </a>
                            
                            <a href="${pageContext.request.contextPath}/inventory/purchase-orders?action=create" class="inventory-quick-action-btn">
                                <i class="fas fa-shopping-cart"></i>
                                <span>Create PO</span>
                            </a>
                            
                            <a href="${pageContext.request.contextPath}/inventory/suppliers?action=add" class="inventory-quick-action-btn">
                                <i class="fas fa-truck"></i>
                                <span>Add Supplier</span>
                            </a>
                            
                            <a href="${pageContext.request.contextPath}/inventory/issues?action=report" class="inventory-quick-action-btn">
                                <i class="fas fa-bug"></i>
                                <span>Report Issue</span>
                            </a>
                            
                            <a href="${pageContext.request.contextPath}/inventory/stock-adjustment" class="inventory-quick-action-btn">
                                <i class="fas fa-edit"></i>
                                <span>Stock Adjustment</span>
                            </a>
                            
                            <a href="${pageContext.request.contextPath}/inventory/inventory-report" class="inventory-quick-action-btn">
                                <i class="fas fa-file-alt"></i>
                                <span>Generate Report</span>
                            </a>
                            
                        </div>
                    </div>
                </div>
                
            </div>
            
            <!-- Recent Activities Row -->
            <div class="inventory-content-grid">
                
                <!-- Recent Purchase Orders -->
                <div class="inventory-content-card">
                    <div class="inventory-card-header">
                        <h3><i class="fas fa-shopping-cart"></i> Recent Purchase Orders</h3>
                        <a href="${pageContext.request.contextPath}/inventory/purchase-orders" class="inventory-btn inventory-btn-sm">
                            View All
                        </a>
                    </div>
                    <div class="inventory-card-content">
                        <c:choose>
                            <c:when test="${not empty recentPurchaseOrders}">
                                <div class="inventory-activity-list">
                                    <c:forEach var="order" items="${recentPurchaseOrders}">
                                        <div class="inventory-activity-item">
                                            <i class="fas fa-shopping-cart inventory-activity-icon inventory-activity-icon-info"></i>
                                            <div class="inventory-activity-content">
                                                <p><strong>PO ${order.poId}</strong> - ${order.supplier}</p>
                                                <small>Status: ${order.status} - ${order.createdAt}</small>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="inventory-empty-state">
                                    <i class="fas fa-shopping-cart"></i>
                                    <p>No recent purchase orders</p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                
                <!-- Recent Issues -->
                <div class="inventory-content-card">
                    <div class="inventory-card-header">
                        <h3><i class="fas fa-bug"></i> Recent Issues</h3>
                        <a href="${pageContext.request.contextPath}/inventory/issues" class="inventory-btn inventory-btn-sm">
                            View All
                        </a>
                    </div>
                    <div class="inventory-card-content">
                        <c:choose>
                            <c:when test="${not empty recentIssues}">
                                <div class="inventory-activity-list">
                                    <c:forEach var="issue" items="${recentIssues}">
                                        <div class="inventory-activity-item">
                                            <i class="fas fa-bug inventory-activity-icon inventory-activity-icon-warning"></i>
                                            <div class="inventory-activity-content">
                                                <p><strong>${issue.ingredient}</strong></p>
                                                <small>Status: ${issue.status} - Reported by: ${issue.reportedBy}</small>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="inventory-empty-state">
                                    <i class="fas fa-check-circle"></i>
                                    <p>No recent issues reported</p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                
            </div>
            
        </div>
        
    </main>
    
    <!-- Inventory Dashboard JavaScript -->
    <script src="${pageContext.request.contextPath}/js/inventory-dashboard.js"></script>
    
    <!-- Chart Data Script -->
    <script>
        // Pass data from server to JavaScript
        window.inventoryData = {
            stockStatus: {
                inStock: ${stockStatus['inStock'] != null ? stockStatus['inStock'] : 0},
                lowStock: ${stockStatus['lowStock'] != null ? stockStatus['lowStock'] : 0},
                criticalStock: ${stockStatus['criticalStock'] != null ? stockStatus['criticalStock'] : 0}
            },
            purchaseOrderStatus: {
                total: ${poStatus['total'] != null ? poStatus['total'] : 0},
                pending: ${poStatus['pending'] != null ? poStatus['pending'] : 0},
                shipping: ${poStatus['shipping'] != null ? poStatus['shipping'] : 0},
                completed: ${poStatus['completed'] != null ? poStatus['completed'] : 0}
            }
        };
    </script>
    
</body>
</html>