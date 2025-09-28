<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ingredients List - Inventory</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/inventory-dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ingredient-list.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <div class="dashboard-container">
        <!-- Include Sidebar -->
        <jsp:include page="sidebar.jsp"/>
        
        <!-- Main Content -->
        <main class="main-content">
            <div class="content-header">
                <div class="header-left">
                    <button class="menu-toggle" id="menuToggle">
                        <i class="fas fa-bars"></i>
                    </button>
                    <h1>Ingredients List</h1>
                </div>
                
            </div>

            <!-- Quick Stats -->
            <div class="stats-container">
                <div class="stat-card">
                    <div class="stat-icon">
                        <i class="fas fa-exclamation-triangle"></i>
                    </div>
                    <div class="stat-content">
                        <h3>Low Stock</h3>
                        <div class="stat-value">
                            <c:set var="lowStock" value="0"/>
                            <c:forEach var="ingredient" items="${ingredients}">
                                <c:if test="${ingredient.stockQuantity <= 10}">
                                    <c:set var="lowStock" value="${lowStock + 1}"/>
                                </c:if>
                            </c:forEach>
                            ${lowStock}
                        </div>
                    </div>
                </div>
                
                <div class="stat-card">
                    <div class="stat-icon">
                        <i class="fas fa-boxes"></i>
                    </div>
                    <div class="stat-content">
                        <h3>Total Items</h3>
                        <div class="stat-value">${totalRecords}</div>
                    </div>
                </div>
                
                <div class="stat-card">
                    <div class="stat-icon">
                        <i class="fas fa-check-circle"></i>
                    </div>
                    <div class="stat-content">
                        <h3>Active</h3>
                        <div class="stat-value">
                            <c:set var="activeCount" value="0"/>
                            <c:forEach var="ingredient" items="${ingredients}">
                                <c:if test="${ingredient.active}">
                                    <c:set var="activeCount" value="${activeCount + 1}"/>
                                </c:if>
                            </c:forEach>
                            ${activeCount}
                        </div>
                    </div>
                </div>
            </div>

            <!-- Filters and Search -->
            <div class="content-section">
                <div class="section-header">
                    <h2>Filter Ingredients</h2>
                </div>
                
                <form method="GET" action="${pageContext.request.contextPath}/inventory/ingredient-list" class="filter-form">
                    <div class="filter-row">
                        <div class="filter-group">
                            <label for="search">Search Ingredient:</label>
                            <input type="text" id="search" name="search" value="${searchKeyword}" 
                                   placeholder="Enter ingredient name or supplier...">
                        </div>
                        
                        <div class="filter-group">
                            <label for="stock">Stock Level:</label>
                            <select id="stock" name="stock">
                                <option value="all">All Levels</option>
                                <option value="low" ${stockFilter eq 'low' ? 'selected' : ''}>Low Stock (≤10)</option>
                                <option value="normal" ${stockFilter eq 'normal' ? 'selected' : ''}>Normal (11-50)</option>
                                <option value="high" ${stockFilter eq 'high' ? 'selected' : ''}>High Stock (>50)</option>
                            </select>
                        </div>
                        
                        <div class="filter-group">
                            <label for="unit">Unit:</label>
                            <select id="unit" name="unit">
                                <option value="all">All Units</option>
                                <option value="kg" ${unitFilter eq 'kg' ? 'selected' : ''}>Kilogram (kg)</option>
                                <option value="liter" ${unitFilter eq 'liter' ? 'selected' : ''}>Liter</option>
                                <option value="ml" ${unitFilter eq 'ml' ? 'selected' : ''}>Milliliter (ml)</option>
                                <option value="pack" ${unitFilter eq 'pack' ? 'selected' : ''}>Pack</option>
                                <option value="can" ${unitFilter eq 'can' ? 'selected' : ''}>Can</option>
                            </select>
                        </div>
                        
                        <div class="filter-group">
                            <label for="supplier">Supplier:</label>
                            <select id="supplier" name="supplier">
                                <option value="all" ${supplierFilter eq 'all' or empty supplierFilter ? 'selected' : ''}>All Suppliers</option>
                                <c:if test="${not empty suppliers}">
                                    <c:forEach var="supplier" items="${suppliers}">
                                        <option value="${supplier['id']}" ${supplierFilter eq supplier['id'].toString() ? 'selected' : ''}>${supplier['name']}</option>
                                    </c:forEach>
                                </c:if>
                            </select>
                        </div>
                        
                        <div class="filter-actions">
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-search"></i> Search
                            </button>
                            <a href="${pageContext.request.contextPath}/inventory/ingredient-list" class="btn btn-secondary">
                                <i class="fas fa-refresh"></i> Clear
                            </a>
                        </div>
                    </div>
                </form>
            </div>

            <!-- Ingredients Table -->
            <div class="content-section">
                <div class="section-header">
                    <h2>Ingredients (${totalRecords} items found)</h2>
                </div>
                
                <div class="table-container">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Ingredient Name</th>
                                <th>Stock Quantity</th>
                                <th>Unit</th>
                                <th>Supplier</th>
                                <th>Status</th>
                                <th>Last Updated</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty ingredients}">
                                    <c:forEach var="ingredient" items="${ingredients}">
                                        <tr>
                                            <td>
                                                <strong>#${ingredient.ingredientID}</strong>
                                            </td>
                                            <td>
                                                <div class="ingredient-info">
                                                    <i class="fas fa-seedling"></i>
                                                    <span class="ingredient-name">${ingredient.name}</span>
                                                </div>
                                            </td>
                                            <td>
                                                <div class="stock-info">
                                                    <span class="stock-value">
                                                        <fmt:formatNumber value="${ingredient.stockQuantity}" pattern="#,##0.##"/>
                                                    </span>
                                                    <c:choose>
                                                        <c:when test="${ingredient.stockQuantity <= 10}">
                                                            <span class="stock-badge stock-low">
                                                                <i class="fas fa-exclamation-triangle"></i> Low
                                                            </span>
                                                        </c:when>
                                                        <c:when test="${ingredient.stockQuantity > 50}">
                                                            <span class="stock-badge stock-high">
                                                                <i class="fas fa-check-circle"></i> High
                                                            </span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="stock-badge stock-normal">
                                                                <i class="fas fa-circle"></i> Normal
                                                            </span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </td>
                                            <td>
                                                <span class="unit-badge">${ingredient.unitName}</span>
                                            </td>
                                            <td>
                                                <div class="supplier-info">
                                                    <i class="fas fa-truck"></i>
                                                    ${ingredient.supplierName}
                                                </div>
                                            </td>
                                            <td>
                                                <span class="status-badge ${ingredient.active ? 'status-active' : 'status-inactive'}">
                                                    <c:choose>
                                                        <c:when test="${ingredient.active}">
                                                            <i class="fas fa-check-circle"></i> Active
                                                        </c:when>
                                                        <c:otherwise>
                                                            <i class="fas fa-times-circle"></i> Inactive
                                                        </c:otherwise>
                                                    </c:choose>
                                                </span>
                                            </td>
                                            <td>
                                                <fmt:parseDate var="parsedDate" value="${ingredient.createdAt}" pattern="yyyy-MM-dd'T'HH:mm:ss"/>
                                                <fmt:formatDate value="${parsedDate}" pattern="dd/MM/yyyy"/>
                                            </td>
                                            <td class="table-actions">
                                                <button class="btn btn-sm btn-info" onclick="viewIngredientDetails(${ingredient.ingredientID})" title="View Details">
                                                    <i class="fas fa-eye"></i>
                                                </button>
                                                <button class="btn btn-sm btn-warning" onclick="checkStock(${ingredient.ingredientID})" title="Stock History">
                                                    <i class="fas fa-history"></i>
                                                </button>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="8" class="no-data">
                                            <div class="no-data-message">
                                                <i class="fas fa-seedling"></i>
                                                <h3>No Ingredients Found</h3>
                                                <p>Try adjusting your search criteria or filters.</p>
                                            </div>
                                        </td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>

                <!-- Pagination -->
                <c:if test="${totalPages > 1}">
                    <div class="pagination-container">
                        <nav class="pagination">
                            <!-- Previous button -->
                            <c:if test="${currentPage > 1}">
                                <a href="?page=${currentPage - 1}&search=${searchKeyword}&stock=${stockFilter}&unit=${unitFilter}&supplier=${supplierFilter}" 
                                   class="pagination-btn">
                                    <i class="fas fa-chevron-left"></i> Previous
                                </a>
                            </c:if>
                            
                            <!-- Page numbers -->
                            <c:forEach var="i" begin="1" end="${totalPages}">
                                <c:choose>
                                    <c:when test="${i eq currentPage}">
                                        <span class="pagination-btn active">${i}</span>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="?page=${i}&search=${searchKeyword}&stock=${stockFilter}&unit=${unitFilter}&supplier=${supplierFilter}" 
                                           class="pagination-btn">${i}</a>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                            
                            <!-- Next button -->
                            <c:if test="${currentPage < totalPages}">
                                <a href="?page=${currentPage + 1}&search=${searchKeyword}&stock=${stockFilter}&unit=${unitFilter}&supplier=${supplierFilter}" 
                                   class="pagination-btn">
                                    Next <i class="fas fa-chevron-right"></i>
                                </a>
                            </c:if>
                        </nav>
                        
                        <div class="pagination-info">
                            Showing page ${currentPage} of ${totalPages} 
                            (${totalRecords} total items)
                        </div>
                    </div>
                </c:if>
            </div>
        </main>
    </div>

    <script>
        // Menu toggle functionality
        document.getElementById('menuToggle').addEventListener('click', function() {
            document.querySelector('.sidebar').classList.toggle('collapsed');
            document.querySelector('.main-content').classList.toggle('expanded');
        });

        // View Ingredient Details function
        function viewIngredientDetails(ingredientID) {
            // TODO: Implement view details functionality
            alert('View details for Ingredient #' + ingredientID);
        }

        // Check Stock History function
        function checkStock(ingredientID) {
            // TODO: Implement stock history functionality
            alert('View stock history for Ingredient #' + ingredientID);
        }

        // Auto-submit form on filter change
        document.querySelectorAll('select[name="stock"], select[name="unit"], select[name="supplier"]').forEach(function(select) {
            select.addEventListener('change', function() {
                this.form.submit();
            });
        });
    </script>

    <style>
        .stats-container {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }

        .stat-card {
            background: linear-gradient(135deg, #fff 0%, #f8f9fa 100%);
            border: 1px solid #e3e6f0;
            border-radius: 8px;
            padding: 20px;
            display: flex;
            align-items: center;
            gap: 15px;
            transition: transform 0.2s, box-shadow 0.2s;
        }

        .stat-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        }

        .stat-icon {
            width: 50px;
            height: 50px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            background: linear-gradient(135deg, #28a745, #20c997);
            color: white;
            font-size: 20px;
        }

        .stat-content h3 {
            margin: 0 0 5px 0;
            font-size: 14px;
            color: #666;
            font-weight: 500;
        }

        .stat-value {
            font-size: 24px;
            font-weight: 700;
            color: #2d3436;
        }

        .ingredient-info {
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .ingredient-name {
            font-weight: 500;
        }

        .stock-info {
            display: flex;
            flex-direction: column;
            gap: 5px;
        }

        .stock-value {
            font-weight: 600;
            font-size: 16px;
        }

        .stock-badge {
            display: inline-flex;
            align-items: center;
            gap: 4px;
            padding: 2px 6px;
            border-radius: 10px;
            font-size: 11px;
            font-weight: 500;
        }

        .stock-low { background: #f8d7da; color: #721c24; }
        .stock-normal { background: #d1ecf1; color: #0c5460; }
        .stock-high { background: #d4edda; color: #155724; }

        .unit-badge {
            background: #e9ecef;
            color: #495057;
            padding: 4px 8px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: 500;
        }

        .supplier-info {
            display: flex;
            align-items: center;
            gap: 8px;
            color: #666;
        }

        .status-active { background: #d4edda; color: #155724; }
        .status-inactive { background: #f8d7da; color: #721c24; }

        /* Rest of the styles are inherited from inventory-dashboard.css */
    </style>
    
    <!-- JavaScript -->
    <script src="${pageContext.request.contextPath}/js/ingredient-list.js"></script>
</body>
</html>