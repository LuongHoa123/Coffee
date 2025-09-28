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
    
    <!-- Settings specific CSS -->
    <style>
        .settings-header {
            display: flex;
            justify-content: between;
            align-items: center;
            margin-bottom: 2rem;
        }
        
        .settings-stats {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 1rem;
            margin-bottom: 2rem;
        }
        
        .settings-stat-card {
            background: white;
            padding: 1.5rem;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            text-align: center;
        }
        
        .settings-filters {
            background: white;
            padding: 1.5rem;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            margin-bottom: 2rem;
        }
        
        .filter-row {
            display: grid;
            grid-template-columns: 1fr 200px 150px;
            gap: 1rem;
            align-items: end;
        }
        
        .settings-table-container {
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        
        .settings-table {
            width: 100%;
            border-collapse: collapse;
        }
        
        .settings-table th {
            background: #f8f9fa;
            padding: 1rem;
            text-align: left;
            font-weight: 600;
            border-bottom: 2px solid #e0e0e0;
        }
        
        .settings-table td {
            padding: 1rem;
            border-bottom: 1px solid #e0e0e0;
            vertical-align: middle;
        }
        
        .settings-table tbody tr:hover {
            background: #f8f9fa;
        }
        
        .type-badge {
            background: #e3f2fd;
            color: #1976d2;
            padding: 0.25rem 0.75rem;
            border-radius: 20px;
            font-size: 0.8rem;
            font-weight: 500;
        }
        
        .status-badge {
            padding: 0.25rem 0.75rem;
            border-radius: 20px;
            font-size: 0.8rem;
            font-weight: 500;
        }
        
        .status-active {
            background: #d4edda;
            color: #155724;
        }
        
        .status-inactive {
            background: #f8d7da;
            color: #721c24;
        }
        
        .action-buttons {
            display: flex;
            gap: 0.5rem;
        }
        
        .btn-icon {
            background: none;
            border: 1px solid #ddd;
            padding: 0.5rem;
            border-radius: 4px;
            cursor: pointer;
            transition: all 0.3s ease;
            color: #666;
        }
        
        .btn-icon:hover {
            background: #f0f0f0;
        }
        
        .btn-view:hover {
            background: #e3f2fd;
            border-color: #1976d2;
            color: #1976d2;
        }
        
        .btn-edit:hover {
            background: #fff3e0;
            border-color: #f57c00;
            color: #f57c00;
        }
        
        .btn-delete:hover {
            background: #ffebee;
            border-color: #f44336;
            color: #f44336;
        }
        
        .btn-toggle:hover {
            background: #f3e5f5;
            border-color: #9c27b0;
            color: #9c27b0;
        }
    </style>
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
                    <i class="fas fa-cogs"></i>
                    Settings Management
                </h1>
            </div>
            
            <div class="admin-header-right">
                <a href="${pageContext.request.contextPath}/admin/settings?action=add" class="admin-btn">
                    <i class="fas fa-plus"></i>
                    Add New Setting
                </a>
            </div>
        </header>
        
        <!-- Settings Content -->
        <div class="admin-dashboard-content">
            
            <!-- Success/Error Messages -->
            <c:if test="${param.success == 'created'}">
                <div class="admin-alert admin-alert-success">
                    <i class="fas fa-check-circle"></i>
                    Setting created successfully!
                </div>
            </c:if>
            
            <c:if test="${param.success == 'updated'}">
                <div class="admin-alert admin-alert-success">
                    <i class="fas fa-check-circle"></i>
                    Setting updated successfully!
                </div>
            </c:if>
            
            <c:if test="${param.success == 'deleted'}">
                <div class="admin-alert admin-alert-success">
                    <i class="fas fa-check-circle"></i>
                    Setting deleted successfully!
                </div>
            </c:if>
            
            <c:if test="${param.success == 'toggled'}">
                <div class="admin-alert admin-alert-success">
                    <i class="fas fa-check-circle"></i>
                    Setting status toggled successfully!
                </div>
            </c:if>
            
            <c:if test="${param.error != null}">
                <div class="admin-alert admin-alert-error">
                    <i class="fas fa-exclamation-circle"></i>
                    Error: ${param.error}
                </div>
            </c:if>
            
            <c:if test="${not empty errorMessage}">
                <div class="admin-alert admin-alert-error">
                    <i class="fas fa-exclamation-circle"></i>
                    ${errorMessage}
                </div>
            </c:if>
            
            <!-- Settings Statistics -->
            <div class="settings-stats">
                <div class="settings-stat-card">
                    <h3>${totalSettings}</h3>
                    <p><i class="fas fa-cogs"></i> Total Settings</p>
                </div>
                
                <c:forEach var="entry" items="${settingsCountByType}">
                    <div class="settings-stat-card">
                        <h3>${entry.value}</h3>
                        <p><i class="fas fa-tag"></i> ${entry.key}</p>
                    </div>
                </c:forEach>
            </div>
            
            <!-- Search and Filter Section -->
            <div class="admin-search-section">
                <div class="admin-search-header">
                    <div class="admin-search-controls">
                        <form method="GET" action="${pageContext.request.contextPath}/admin/settings" 
                              style="display: flex; gap: 1rem; align-items: end; flex-wrap: wrap;">
                            <div class="admin-search-group">
                                <label for="searchQuery">Search Settings:</label>
                                <input type="text" 
                                       id="searchQuery"
                                       name="search" 
                                       class="admin-search-input"
                                       placeholder="Search by value or description..."
                                       value="${searchKeyword}">
                            </div>
                            
                            <div class="admin-search-group">
                                <label for="typeFilter">Filter by Type:</label>
                                <select id="typeFilter" name="type" class="admin-filter-select">
                                    <option value="all" ${selectedType == 'all' || empty selectedType ? 'selected' : ''}>All Types</option>
                                    <c:forEach var="type" items="${settingTypes}">
                                        <option value="${type}" ${selectedType == type ? 'selected' : ''}>${type}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            
                            <button type="submit" class="admin-search-btn">
                                <i class="fas fa-search"></i> Search
                            </button>
                            
                            <c:if test="${not empty searchKeyword or (not empty selectedType and selectedType != 'all')}">
                                <a href="${pageContext.request.contextPath}/admin/settings" class="admin-btn" 
                                   style="background: #6c757d;">
                                    <i class="fas fa-times"></i> Clear
                                </a>
                            </c:if>
                        </form>
                    </div>
                </div>
            </div>
            
            <!-- Results Summary -->
            <div class="admin-results-summary">
                <div class="admin-results-count">
                    <strong>${totalSettings != null ? totalSettings : 0}</strong> settings found
                    <c:if test="${not empty searchKeyword}">
                        for "<em>${searchKeyword}</em>"
                    </c:if>
                    <c:if test="${not empty selectedType and selectedType != 'all'}">
                        in <strong>${selectedType}</strong> type
                    </c:if>
                </div>
                
                <div class="admin-results-actions">
                    <div class="admin-bulk-actions">
                        <select class="admin-bulk-select" id="bulkAction">
                            <option value="">Bulk Actions</option>
                            <option value="activate">Activate Selected</option>
                            <option value="deactivate">Deactivate Selected</option>
                            <option value="delete">Delete Selected</option>
                        </select>
                        <button class="admin-bulk-btn" onclick="processBulkAction()">Apply</button>
                    </div>
                </div>
            </div>
            
            <!-- Settings Table -->
            <div class="settings-table-container">
                <c:choose>
                    <c:when test="${not empty settings}">
                        <table class="settings-table">
                            <thead>
                                <tr>
                                    <th>
                                        <input type="checkbox" onchange="toggleAllCheckboxes(this)" title="Select All">
                                    </th>
                                    <th>ID</th>
                                    <th>Type</th>
                                    <th>Value</th>
                                    <th>Description</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="setting" items="${settings}">
                                    <tr>
                                        <td>
                                            <input type="checkbox" name="selectedIds" value="${setting.settingID}">
                                        </td>
                                        <td>${setting.settingID}</td>
                                        <td>
                                            <span class="type-badge">${setting.type}</span>
                                        </td>
                                        <td>
                                            <strong>${setting.value}</strong>
                                        </td>
                                        <td>
                                            ${setting.description}
                                        </td>
                                        <td>
                                            <span class="status-badge ${setting.active ? 'status-active' : 'status-inactive'}">
                                                ${setting.active ? 'Active' : 'Inactive'}
                                            </span>
                                        </td>
                                        <td>
                                            <div class="action-buttons">
                                                <a href="${pageContext.request.contextPath}/admin/settings?action=view&id=${setting.settingID}" 
                                                   class="btn-icon btn-view" title="View Details">
                                                    <i class="fas fa-eye"></i>
                                                </a>
                                                
                                                <a href="${pageContext.request.contextPath}/admin/settings?action=edit&id=${setting.settingID}" 
                                                   class="btn-icon btn-edit" title="Edit Setting">
                                                    <i class="fas fa-edit"></i>
                                                </a>
                                                
                                                <button onclick="toggleSetting(${setting.settingID})" 
                                                        class="btn-icon btn-toggle" 
                                                        title="Toggle Status">
                                                    <i class="fas fa-toggle-${setting.active ? 'on' : 'off'}"></i>
                                                </button>
                                                
                                                <button onclick="deleteSetting(${setting.settingID}, '${setting.value}')" 
                                                        class="btn-icon btn-delete" title="Delete Setting">
                                                    <i class="fas fa-trash"></i>
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <div class="admin-empty-state" style="padding: 3rem; text-align: center;">
                            <i class="fas fa-cogs" style="font-size: 4rem; color: #ccc; margin-bottom: 1rem;"></i>
                            <h3>No Settings Found</h3>
                            <p>There are no settings matching your criteria.</p>
                            <a href="${pageContext.request.contextPath}/admin/settings?action=add" class="admin-btn">
                                <i class="fas fa-plus"></i>
                                Add First Setting
                            </a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
            
            <!-- Pagination Section -->
            <c:if test="${totalPages > 1}">
                <div class="admin-pagination-section">
                    <div class="admin-pagination-info">
                        <div class="admin-pagination-text">
                            Showing <strong>${(currentPage - 1) * pageSize + 1}</strong> to 
                            <strong>${currentPage * pageSize > totalSettings ? totalSettings : currentPage * pageSize}</strong> 
                            of <strong>${totalSettings}</strong> results
                        </div>
                        
                        <div class="admin-pagination-controls">
                            <label for="pageSizeSelect">Show:</label>
                            <select id="pageSizeSelect" class="admin-pagination-select" onchange="changePageSize(this.value)">
                                <option value="10" ${pageSize == 10 ? 'selected' : ''}>10</option>
                                <option value="25" ${pageSize == 25 ? 'selected' : ''}>25</option>
                                <option value="50" ${pageSize == 50 ? 'selected' : ''}>50</option>
                                <option value="100" ${pageSize == 100 ? 'selected' : ''}>100</option>
                            </select>
                            <span>per page</span>
                        </div>
                    </div>
                    
                    <nav class="admin-pagination">
                        <!-- First Page -->
                        <c:if test="${currentPage > 1}">
                            <a href="?page=1${not empty searchKeyword ? '&search=' += searchKeyword : ''}${not empty selectedType and selectedType != 'all' ? '&type=' += selectedType : ''}" 
                               class="admin-pagination-item admin-pagination-prev">
                                <i class="fas fa-angle-double-left"></i> First
                            </a>
                        </c:if>
                        
                        <!-- Previous Page -->
                        <c:if test="${currentPage > 1}">
                            <a href="?page=${currentPage - 1}${not empty searchKeyword ? '&search=' += searchKeyword : ''}${not empty selectedType and selectedType != 'all' ? '&type=' += selectedType : ''}" 
                               class="admin-pagination-item admin-pagination-prev">
                                <i class="fas fa-angle-left"></i> Previous
                            </a>
                        </c:if>
                        
                        <!-- Page Numbers -->
                        <c:set var="startPage" value="${currentPage - 2 > 0 ? currentPage - 2 : 1}" />
                        <c:set var="endPage" value="${currentPage + 2 <= totalPages ? currentPage + 2 : totalPages}" />
                        
                        <c:if test="${startPage > 1}">
                            <a href="?page=1${not empty searchKeyword ? '&search=' += searchKeyword : ''}${not empty selectedType and selectedType != 'all' ? '&type=' += selectedType : ''}" 
                               class="admin-pagination-item">1</a>
                            <c:if test="${startPage > 2}">
                                <span class="admin-pagination-ellipsis">...</span>
                            </c:if>
                        </c:if>
                        
                        <c:forEach begin="${startPage}" end="${endPage}" var="page">
                            <c:choose>
                                <c:when test="${page == currentPage}">
                                    <span class="admin-pagination-item active">${page}</span>
                                </c:when>
                                <c:otherwise>
                                    <a href="?page=${page}${not empty searchKeyword ? '&search=' += searchKeyword : ''}${not empty selectedType and selectedType != 'all' ? '&type=' += selectedType : ''}" 
                                       class="admin-pagination-item">${page}</a>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                        
                        <c:if test="${endPage < totalPages}">
                            <c:if test="${endPage < totalPages - 1}">
                                <span class="admin-pagination-ellipsis">...</span>
                            </c:if>
                            <a href="?page=${totalPages}${not empty searchKeyword ? '&search=' += searchKeyword : ''}${not empty selectedType and selectedType != 'all' ? '&type=' += selectedType : ''}" 
                               class="admin-pagination-item">${totalPages}</a>
                        </c:if>
                        
                        <!-- Next Page -->
                        <c:if test="${currentPage < totalPages}">
                            <a href="?page=${currentPage + 1}${not empty searchKeyword ? '&search=' += searchKeyword : ''}${not empty selectedType and selectedType != 'all' ? '&type=' += selectedType : ''}" 
                               class="admin-pagination-item admin-pagination-next">
                                Next <i class="fas fa-angle-right"></i>
                            </a>
                        </c:if>
                        
                        <!-- Last Page -->
                        <c:if test="${currentPage < totalPages}">
                            <a href="?page=${totalPages}${not empty searchKeyword ? '&search=' += searchKeyword : ''}${not empty selectedType and selectedType != 'all' ? '&type=' += selectedType : ''}" 
                               class="admin-pagination-item admin-pagination-next">
                                Last <i class="fas fa-angle-double-right"></i>
                            </a>
                        </c:if>
                    </nav>
                </div>
            </c:if>
            
        </div>
        
    </main>
    
    <!-- JavaScript -->
    <script src="${pageContext.request.contextPath}/js/admin-dashboard.js"></script>
    
    <script>
        function toggleSetting(settingId) {
            if (confirm('Are you sure you want to toggle this setting status?')) {
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = '${pageContext.request.contextPath}/admin/settings';
                
                const actionInput = document.createElement('input');
                actionInput.type = 'hidden';
                actionInput.name = 'action';
                actionInput.value = 'toggle';
                
                const idInput = document.createElement('input');
                idInput.type = 'hidden';
                idInput.name = 'id';
                idInput.value = settingId;
                
                form.appendChild(actionInput);
                form.appendChild(idInput);
                document.body.appendChild(form);
                form.submit();
            }
        }
        
        function deleteSetting(settingId, settingValue) {
            if (confirm('Are you sure you want to delete the setting "' + settingValue + '"?\\n\\nThis action cannot be undone.')) {
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = '${pageContext.request.contextPath}/admin/settings';
                
                const actionInput = document.createElement('input');
                actionInput.type = 'hidden';
                actionInput.name = 'action';
                actionInput.value = 'delete';
                
                const idInput = document.createElement('input');
                idInput.type = 'hidden';
                idInput.name = 'id';
                idInput.value = settingId;
                
                form.appendChild(actionInput);
                form.appendChild(idInput);
                document.body.appendChild(form);
                form.submit();
            }
        }
        
        // Change page size function
        function changePageSize(newSize) {
            const url = new URL(window.location);
            url.searchParams.set('pageSize', newSize);
            url.searchParams.set('page', '1'); // Reset to first page
            window.location = url.toString();
        }
        
        // Bulk actions function
        function processBulkAction() {
            const action = document.getElementById('bulkAction').value;
            const checkboxes = document.querySelectorAll('input[name="selectedIds"]:checked');
            
            if (!action) {
                alert('Please select an action first.');
                return;
            }
            
            if (checkboxes.length === 0) {
                alert('Please select at least one setting.');
                return;
            }
            
            const settingIds = Array.from(checkboxes).map(cb => cb.value);
            let confirmMessage = '';
            
            switch (action) {
                case 'activate':
                    confirmMessage = `Are you sure you want to activate ${settingIds.length} selected setting(s)?`;
                    break;
                case 'deactivate':
                    confirmMessage = `Are you sure you want to deactivate ${settingIds.length} selected setting(s)?`;
                    break;
                case 'delete':
                    confirmMessage = `Are you sure you want to delete ${settingIds.length} selected setting(s)?\\n\\nThis action cannot be undone.`;
                    break;
            }
            
            if (confirm(confirmMessage)) {
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = '${pageContext.request.contextPath}/admin/settings';
                
                const actionInput = document.createElement('input');
                actionInput.type = 'hidden';
                actionInput.name = 'action';
                actionInput.value = 'bulk_' + action;
                form.appendChild(actionInput);
                
                settingIds.forEach(id => {
                    const idInput = document.createElement('input');
                    idInput.type = 'hidden';
                    idInput.name = 'selectedIds';
                    idInput.value = id;
                    form.appendChild(idInput);
                });
                
                document.body.appendChild(form);
                form.submit();
            }
        }
        
        // Select all checkboxes
        function toggleAllCheckboxes(source) {
            const checkboxes = document.querySelectorAll('input[name="selectedIds"]');
            checkboxes.forEach(checkbox => {
                checkbox.checked = source.checked;
            });
        }
        
        // Refresh settings function
        function refreshSettings() {
            window.location.reload();
        }
        
        // Auto-hide alerts after 5 seconds
        document.addEventListener('DOMContentLoaded', function() {
            const alerts = document.querySelectorAll('.admin-alert');
            alerts.forEach(alert => {
                setTimeout(() => {
                    alert.style.opacity = '0';
                    setTimeout(() => {
                        alert.remove();
                    }, 300);
                }, 5000);
            });
        });
    </script>
    
</body>
</html>