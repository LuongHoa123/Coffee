<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle} - CoffeeLux Barista System</title>
    
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    
    <!-- Barista Dashboard CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/barista-dashboard.css">
    
    <!-- Chart.js for analytics -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body class="barista-body">
    
    <!-- Barista Sidebar -->
    <jsp:include page="sidebar.jsp">
        <jsp:param name="page" value="${param.page}" />
    </jsp:include>
    
    <!-- Main Content Area -->
    <main class="barista-main-content">
        
        <!-- Top Header -->
        <header class="barista-header">
            <div class="barista-header-left">
                <button class="barista-sidebar-toggle" id="sidebarToggle">
                    <i class="fas fa-bars"></i>
                </button>
                <h1 class="barista-page-title">
                    <i class="fas fa-coffee"></i>
                    Barista Dashboard
                </h1>
            </div>
            
            <div class="barista-header-right">
                <div class="barista-header-info">
                    <span class="barista-welcome">Welcome, <strong>${currentUser.fullName}</strong></span>
                    <span class="barista-current-time" id="currentTime"></span>
                </div>
                <div class="barista-header-actions">
                    <button class="barista-refresh-btn" onclick="refreshDashboard()">
                        <i class="fas fa-sync-alt"></i>
                        Refresh
                    </button>
                </div>
            </div>
        </header>

        <!-- Dashboard Content -->
        <div class="barista-dashboard-content">
            
            <!-- Error Message Display -->
            <c:if test="${not empty error}">
                <div class="alert alert-error">
                    <i class="fas fa-exclamation-triangle"></i>
                    ${error}
                </div>
            </c:if>
            
            <!-- Statistics Overview Cards -->
            <section class="barista-stats-overview">
                <h2 class="section-title">Today's Overview</h2>
                
                <div class="stats-cards-grid">
                    <!-- Today Orders Card -->
                    <div class="stats-card orders-card">
                        <div class="card-icon">
                            <i class="fas fa-shopping-cart"></i>
                        </div>
                        <div class="card-content">
                            <h3>Today's Orders</h3>
                            <div class="card-value">${todayOrders}</div>
                            <div class="card-subtitle">Total orders processed</div>
                        </div>
                    </div>
                    
                    <!-- Completed Orders Card -->
                    <div class="stats-card completed-card">
                        <div class="card-icon">
                            <i class="fas fa-check-circle"></i>
                        </div>
                        <div class="card-content">
                            <h3>Completed</h3>
                            <div class="card-value">${completedOrders}</div>
                            <div class="card-subtitle">
                                <fmt:formatNumber value="${completedOrders / todayOrders * 100}" pattern="#.#" />% completion rate
                            </div>
                        </div>
                    </div>
                    
                    <!-- Preparing Orders Card -->
                    <div class="stats-card preparing-card">
                        <div class="card-icon">
                            <i class="fas fa-blender"></i>
                        </div>
                        <div class="card-content">
                            <h3>Preparing</h3>
                            <div class="card-value">${preparingOrders}</div>
                            <div class="card-subtitle">Orders in progress</div>
                        </div>
                    </div>
                    
                    <!-- Revenue Card -->
                    <div class="stats-card revenue-card">
                        <div class="card-icon">
                            <i class="fas fa-dollar-sign"></i>
                        </div>
                        <div class="card-content">
                            <h3>Today's Revenue</h3>
                            <div class="card-value">
                                <fmt:formatNumber value="${todayRevenue}" pattern="#,##0" /> VND
                            </div>
                            <div class="card-subtitle">Total sales today</div>
                        </div>
                    </div>
                </div>
            </section>

            <!-- Order Status Overview -->
            <section class="barista-order-status">
                <div class="section-header">
                    <h2 class="section-title">Order Status Distribution</h2>
                </div>
                
                <div class="order-status-grid">
                    <!-- Order Status Chart -->
                    <div class="chart-container">
                        <canvas id="orderStatusChart"></canvas>
                    </div>
                    
                    <!-- Order Status Details -->
                    <div class="order-status-details">
                        <div class="status-item new">
                            <i class="fas fa-plus-circle"></i>
                            <span class="status-label">New Orders</span>
                            <span class="status-count">${pendingOrders}</span>
                        </div>
                        <div class="status-item preparing">
                            <i class="fas fa-blender"></i>
                            <span class="status-label">Preparing</span>
                            <span class="status-count">${preparingOrders}</span>
                        </div>
                        <div class="status-item ready">
                            <i class="fas fa-check"></i>
                            <span class="status-label">Ready</span>
                            <span class="status-count">${readyOrders}</span>
                        </div>
                        <div class="status-item completed">
                            <i class="fas fa-check-circle"></i>
                            <span class="status-label">Completed</span>
                            <span class="status-count">${completedOrders}</span>
                        </div>
                    </div>
                </div>
            </section>

            <!-- Recent Orders and Popular Products -->
            <section class="barista-data-overview">
                
                <!-- Recent Orders -->
                <div class="data-card recent-orders">
                    <div class="card-header">
                        <h3><i class="fas fa-history"></i> Recent Orders</h3>
                        <button class="view-all-btn">View All</button>
                    </div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${not empty recentOrders}">
                                <div class="orders-table-container">
                                    <table class="orders-table">
                                        <thead>
                                            <tr>
                                                <th>Order ID</th>
                                                <th>Shop</th>
                                                <th>Status</th>
                                                <th>Amount</th>
                                                <th>Time</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="order" items="${recentOrders}" varStatus="status">
                                                <c:if test="${status.index < 10}">
                                                    <tr>
                                                        <td>#${order.orderID}</td>
                                                        <td>${order.shopName}</td>
                                                        <td><span class="status-badge status-${order.status.toLowerCase()}">${order.status}</span></td>
                                                        <td><fmt:formatNumber value="${order.totalAmount}" pattern="#,##0" /> VND</td>
                                                        <td>${order.createdAt}</td>
                                                    </tr>
                                                </c:if>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="no-data">
                                    <i class="fas fa-inbox"></i>
                                    <p>No recent orders found</p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <!-- Popular Products -->
                <div class="data-card popular-products">
                    <div class="card-header">
                        <h3><i class="fas fa-star"></i> Popular Products</h3>
                    </div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${not empty popularProducts}">
                                <div class="products-list">
                                    <c:forEach var="product" items="${popularProducts}" varStatus="status">
                                        <div class="product-item">
                                            <div class="product-rank">#${status.index + 1}</div>
                                            <div class="product-info">
                                                <div class="product-name">${product.productName}</div>
                                                <div class="product-stats">
                                                    <span class="sold-count">${product.totalSold} sold</span>
                                                    <span class="product-price">
                                                        <fmt:formatNumber value="${product.price}" pattern="#,##0" /> VND
                                                    </span>
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="no-data">
                                    <i class="fas fa-coffee"></i>
                                    <p>No product data available</p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </section>

            <!-- Issues and Stock Alerts -->
            <section class="barista-alerts-section">
                
                <!-- My Issues -->
                <div class="alert-card issues-card">
                    <div class="card-header">
                        <h3><i class="fas fa-exclamation-triangle"></i> My Recent Issues</h3>
                        <div class="issues-count">
                            <span class="count-badge">${myReportedIssues}</span>
                        </div>
                    </div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${not empty myRecentIssues}">
                                <div class="issues-list">
                                    <c:forEach var="issue" items="${myRecentIssues}">
                                        <div class="issue-item">
                                            <div class="issue-icon">
                                                <i class="fas fa-warning"></i>
                                            </div>
                                            <div class="issue-details">
                                                <div class="issue-ingredient">${issue.ingredientName}</div>
                                                <div class="issue-info">
                                                    <span class="issue-quantity">${issue.quantity} units</span>
                                                    <span class="issue-status status-${issue.status.toLowerCase().replace(' ', '-')}">${issue.status}</span>
                                                </div>
                                                <div class="issue-date">${issue.createdAt}</div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="no-data">
                                    <i class="fas fa-check-circle"></i>
                                    <p>No recent issues reported</p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <!-- Low Stock Alerts -->
                <div class="alert-card stock-card">
                    <div class="card-header">
                        <h3><i class="fas fa-box"></i> Low Stock Ingredients</h3>
                    </div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${not empty lowStockIngredients}">
                                <div class="stock-list">
                                    <c:forEach var="ingredient" items="${lowStockIngredients}">
                                        <div class="stock-item">
                                            <div class="stock-icon">
                                                <i class="fas fa-box-open"></i>
                                            </div>
                                            <div class="stock-details">
                                                <div class="stock-name">${ingredient.name}</div>
                                                <div class="stock-info">
                                                    <span class="stock-quantity">
                                                        ${ingredient.stockQuantity} ${ingredient.unit}
                                                    </span>
                                                    <span class="stock-level low">Low Stock</span>
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="no-data">
                                    <i class="fas fa-check-circle"></i>
                                    <p>All ingredients in stock</p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </section>

        </div>
    </main>

    <!-- JavaScript -->
    <script src="${pageContext.request.contextPath}/js/barista-dashboard.js"></script>
    
    <script>
        // Initialize dashboard data for charts
        window.dashboardData = {
            orderStatus: {
                labels: ['New', 'Preparing', 'Ready', 'Completed'],
                data: [${pendingOrders}, ${preparingOrders}, ${readyOrders}, ${completedOrders}]
            }
        };
        
        // Update current time
        function updateTime() {
            const now = new Date();
            const timeString = now.toLocaleTimeString('vi-VN', { 
                hour: '2-digit', 
                minute: '2-digit',
                second: '2-digit'
            });
            const dateString = now.toLocaleDateString('vi-VN');
            document.getElementById('currentTime').textContent = `${timeString} - ${dateString}`;
        }
        
        // Update time every second
        setInterval(updateTime, 1000);
        updateTime(); // Initial call
        
        // Refresh dashboard function
        function refreshDashboard() {
            location.reload();
        }
    </script>
</body>
</html>