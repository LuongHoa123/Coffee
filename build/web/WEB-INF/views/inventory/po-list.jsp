<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Purchase Orders List - Inventory</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/inventory-dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/po-list.css">
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
                    <h1>Purchase Orders List</h1>
                </div>
                
            </div>

            <!-- Filters and Search -->
            <div class="content-section">
                <div class="section-header">
                    <h2>Filter Purchase Orders</h2>
                </div>
                
                <form method="GET" action="${pageContext.request.contextPath}/inventory/po-list" class="filter-form">
                    <div class="filter-row">
                        <div class="filter-group">
                            <label for="search">Search PO ID or Supplier:</label>
                            <input type="text" id="search" name="search" value="${searchKeyword}" 
                                   placeholder="Enter PO ID or supplier name...">
                        </div>
                        
                        <div class="filter-group">
                            <label for="status">Status:</label>
                            <select id="status" name="status">
                                <option value="all">All Status</option>
                                <option value="pending" ${statusFilter eq 'pending' ? 'selected' : ''}>Pending</option>
                                <option value="approved" ${statusFilter eq 'approved' ? 'selected' : ''}>Approved</option>
                                <option value="in transit" ${statusFilter eq 'in transit' ? 'selected' : ''}>In Transit</option>
                                <option value="delivered" ${statusFilter eq 'delivered' ? 'selected' : ''}>Delivered</option>
                                <option value="cancelled" ${statusFilter eq 'cancelled' ? 'selected' : ''}>Cancelled</option>
                            </select>
                        </div>
                        
                        <div class="filter-group">
                            <label for="supplier">Supplier:</label>
                            <select id="supplier" name="supplier">
                                <option value="all">All Suppliers</option>
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
                            <a href="${pageContext.request.contextPath}/inventory/po-list" class="btn btn-secondary">
                                <i class="fas fa-refresh"></i> Clear
                            </a>
                        </div>
                    </div>
                </form>
            </div>

            <!-- Purchase Orders Table -->
            <div class="content-section">
                <div class="section-header">
                    <h2>Purchase Orders (${totalRecords} records found)</h2>
                </div>
                
                <div class="table-container">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>PO ID</th>
                                <th>Supplier</th>
                                <th>Status</th>
                                <th>Created Date</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty purchaseOrders}">
                                    <c:forEach var="po" items="${purchaseOrders}">
                                        <tr>
                                            <td>
                                                <strong>#${po.poID}</strong>
                                            </td>
                                            <td>
                                                <div class="supplier-info">
                                                    <i class="fas fa-truck"></i>
                                                    ${po.supplierName}
                                                </div>
                                            </td>
                                            <td>
                                                <span class="status-badge status-${po.status.toLowerCase().replace(' ', '-')}">
                                                    <c:choose>
                                                        <c:when test="${po.status eq 'Pending'}">
                                                            <i class="fas fa-clock"></i> Pending
                                                        </c:when>
                                                        <c:when test="${po.status eq 'Approved'}">
                                                            <i class="fas fa-check"></i> Approved
                                                        </c:when>
                                                        <c:when test="${po.status eq 'In Transit'}">
                                                            <i class="fas fa-shipping-fast"></i> In Transit
                                                        </c:when>
                                                        <c:when test="${po.status eq 'Delivered'}">
                                                            <i class="fas fa-check-circle"></i> Delivered
                                                        </c:when>
                                                        <c:when test="${po.status eq 'Cancelled'}">
                                                            <i class="fas fa-times-circle"></i> Cancelled
                                                        </c:when>
                                                        <c:otherwise>
                                                            ${po.status}
                                                        </c:otherwise>
                                                    </c:choose>
                                                </span>
                                            </td>
                                            <td>
                                                <fmt:parseDate var="parsedDate" value="${po.createdAt}" pattern="yyyy-MM-dd'T'HH:mm:ss"/>
                                                <fmt:formatDate value="${parsedDate}" pattern="dd/MM/yyyy HH:mm"/>
                                            </td>
                                            <td class="table-actions">
                                                <button class="btn btn-sm btn-info" onclick="viewPODetails(${po.poID})" title="View Details">
                                                    <i class="fas fa-eye"></i>
                                                </button>
                                                <button class="btn btn-sm btn-secondary" onclick="printPO(${po.poID})" title="Print PO">
                                                    <i class="fas fa-print"></i>
                                                </button>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="5" class="no-data">
                                            <div class="no-data-message">
                                                <i class="fas fa-box-open"></i>
                                                <h3>No Purchase Orders Found</h3>
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
                                <a href="?page=${currentPage - 1}&search=${searchKeyword}&status=${statusFilter}&supplier=${supplierFilter}" 
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
                                        <a href="?page=${i}&search=${searchKeyword}&status=${statusFilter}&supplier=${supplierFilter}" 
                                           class="pagination-btn">${i}</a>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                            
                            <!-- Next button -->
                            <c:if test="${currentPage < totalPages}">
                                <a href="?page=${currentPage + 1}&search=${searchKeyword}&status=${statusFilter}&supplier=${supplierFilter}" 
                                   class="pagination-btn">
                                    Next <i class="fas fa-chevron-right"></i>
                                </a>
                            </c:if>
                        </nav>
                        
                        <div class="pagination-info">
                            Showing page ${currentPage} of ${totalPages} 
                            (${totalRecords} total records)
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

        // View PO Details function
        function viewPODetails(poID) {
            // TODO: Implement view details functionality
            alert('View details for PO #' + poID);
        }

        // Print PO function
        function printPO(poID) {
            // TODO: Implement print functionality
            alert('Print PO #' + poID);
        }

        // Auto-submit form on filter change
        document.querySelectorAll('select[name="status"], select[name="supplier"]').forEach(function(select) {
            select.addEventListener('change', function() {
                this.form.submit();
            });
        });
    </script>

    <style>
        .filter-form {
            background: #fff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            margin-bottom: 20px;
        }

        .filter-row {
            display: grid;
            grid-template-columns: 2fr 1fr 1fr auto;
            gap: 15px;
            align-items: end;
        }

        .filter-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: 500;
            color: #333;
        }

        .filter-group input,
        .filter-group select {
            width: 100%;
            padding: 8px 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 14px;
        }

        .filter-actions {
            display: flex;
            gap: 10px;
        }

        .data-table {
            width: 100%;
            border-collapse: collapse;
            background: #fff;
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }

        .data-table th,
        .data-table td {
            padding: 12px 15px;
            text-align: left;
            border-bottom: 1px solid #eee;
        }

        .data-table th {
            background: #f8f9fa;
            font-weight: 600;
            color: #333;
        }

        .data-table tbody tr:hover {
            background: #f8f9fa;
        }

        .supplier-info {
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .status-badge {
            display: inline-flex;
            align-items: center;
            gap: 5px;
            padding: 4px 8px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: 500;
        }

        .status-pending { background: #fff3cd; color: #856404; }
        .status-approved { background: #d1ecf1; color: #0c5460; }
        .status-in-transit { background: #d4edda; color: #155724; }
        .status-delivered { background: #d1ecf1; color: #0c5460; }
        .status-cancelled { background: #f8d7da; color: #721c24; }

        .table-actions {
            display: flex;
            gap: 5px;
        }

        .no-data-message {
            text-align: center;
            padding: 40px;
            color: #666;
        }

        .no-data-message i {
            font-size: 48px;
            margin-bottom: 15px;
            color: #ddd;
        }

        .pagination-container {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-top: 20px;
        }

        .pagination {
            display: flex;
            gap: 5px;
        }

        .pagination-btn {
            padding: 8px 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            text-decoration: none;
            color: #333;
            background: #fff;
        }

        .pagination-btn:hover {
            background: #f8f9fa;
            border-color: #28a745;
        }

        .pagination-btn.active {
            background: #28a745;
            color: #fff;
            border-color: #28a745;
        }

        .pagination-info {
            color: #666;
            font-size: 14px;
        }

        @media (max-width: 768px) {
            .filter-row {
                grid-template-columns: 1fr;
            }
            
            .filter-actions {
                justify-content: flex-start;
            }
            
            .table-container {
                overflow-x: auto;
            }
            
            .pagination-container {
                flex-direction: column;
                gap: 10px;
            }
        }
    </style>
    
    <!-- JavaScript -->
    <script src="${pageContext.request.contextPath}/js/po-list.js"></script>
</body>
</html>