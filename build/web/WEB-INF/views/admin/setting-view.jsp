<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
    
    <!-- View specific CSS -->
    <style>
        .detail-container {
            max-width: 800px;
            margin: 0 auto;
        }
        
        .detail-card {
            background: white;
            padding: 2rem;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        
        .detail-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 2rem;
            padding-bottom: 1rem;
            border-bottom: 1px solid #e0e0e0;
        }
        
        .detail-title {
            margin: 0;
            color: #333;
        }
        
        .detail-actions {
            display: flex;
            gap: 0.5rem;
        }
        
        .detail-grid {
            display: grid;
            grid-template-columns: 200px 1fr;
            gap: 1rem 2rem;
            margin-bottom: 2rem;
        }
        
        .detail-label {
            font-weight: 600;
            color: #666;
            display: flex;
            align-items: center;
        }
        
        .detail-value {
            color: #333;
            display: flex;
            align-items: center;
        }
        
        .type-badge {
            background: #e3f2fd;
            color: #1976d2;
            padding: 0.25rem 0.75rem;
            border-radius: 20px;
            font-size: 0.9rem;
            font-weight: 500;
        }
        
        .status-badge {
            padding: 0.25rem 0.75rem;
            border-radius: 20px;
            font-size: 0.9rem;
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
        
        .btn {
            padding: 0.5rem 1rem;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 0.9rem;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 0.5rem;
            transition: all 0.3s ease;
        }
        
        .btn-primary {
            background: #2a5298;
            color: white;
        }
        
        .btn-primary:hover {
            background: #1e3c72;
        }
        
        .btn-warning {
            background: #ffc107;
            color: #212529;
        }
        
        .btn-warning:hover {
            background: #e0a800;
        }
        
        .btn-danger {
            background: #dc3545;
            color: white;
        }
        
        .btn-danger:hover {
            background: #c82333;
        }
        
        .btn-outline-secondary {
            background: transparent;
            color: #6c757d;
            border: 1px solid #6c757d;
        }
        
        .btn-outline-secondary:hover {
            background: #6c757d;
            color: white;
        }
        
        @media (max-width: 768px) {
            .detail-grid {
                grid-template-columns: 1fr;
                gap: 0.5rem;
            }
            
            .detail-header {
                flex-direction: column;
                align-items: flex-start;
                gap: 1rem;
            }
            
            .detail-actions {
                width: 100%;
                justify-content: flex-end;
            }
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
                    <i class="fas fa-eye"></i>
                    Setting Details
                </h1>
            </div>
            
            <div class="admin-header-right">
                <a href="${pageContext.request.contextPath}/admin/settings" class="admin-btn">
                    <i class="fas fa-arrow-left"></i>
                    Back to Settings
                </a>
            </div>
        </header>
        
        <!-- Detail Content -->
        <div class="admin-dashboard-content">
            
            <!-- Error Messages -->
            <c:if test="${not empty errorMessage}">
                <div class="admin-alert admin-alert-error">
                    <i class="fas fa-exclamation-circle"></i>
                    ${errorMessage}
                </div>
            </c:if>
            
            <c:if test="${setting != null}">
                <div class="detail-container">
                    <div class="detail-card">
                        
                        <div class="detail-header">
                            <h2 class="detail-title">
                                ${setting.value}
                                <span class="status-badge ${setting.active ? 'status-active' : 'status-inactive'}">
                                    ${setting.active ? 'Active' : 'Inactive'}
                                </span>
                            </h2>
                            
                            <div class="detail-actions">
                                <a href="${pageContext.request.contextPath}/admin/settings?action=edit&id=${setting.settingID}" 
                                   class="btn btn-primary">
                                    <i class="fas fa-edit"></i>
                                    Edit
                                </a>
                                
                                <button onclick="toggleSetting(${setting.settingID})" 
                                        class="btn btn-warning">
                                    <i class="fas fa-toggle-${setting.active ? 'on' : 'off'}"></i>
                                    ${setting.active ? 'Deactivate' : 'Activate'}
                                </button>
                                
                                <button onclick="deleteSetting(${setting.settingID}, '${setting.value}')" 
                                        class="btn btn-danger">
                                    <i class="fas fa-trash"></i>
                                    Delete
                                </button>
                            </div>
                        </div>
                        
                        <div class="detail-grid">
                            <div class="detail-label">
                                <i class="fas fa-hashtag" style="margin-right: 0.5rem;"></i>
                                Setting ID:
                            </div>
                            <div class="detail-value">
                                ${setting.settingID}
                            </div>
                            
                            <div class="detail-label">
                                <i class="fas fa-tag" style="margin-right: 0.5rem;"></i>
                                Type:
                            </div>
                            <div class="detail-value">
                                <span class="type-badge">${setting.type}</span>
                            </div>
                            
                            <div class="detail-label">
                                <i class="fas fa-font" style="margin-right: 0.5rem;"></i>
                                Value:
                            </div>
                            <div class="detail-value">
                                <strong>${setting.value}</strong>
                            </div>
                            
                            <div class="detail-label">
                                <i class="fas fa-info-circle" style="margin-right: 0.5rem;"></i>
                                Description:
                            </div>
                            <div class="detail-value">
                                <c:choose>
                                    <c:when test="${not empty setting.description}">
                                        ${setting.description}
                                    </c:when>
                                    <c:otherwise>
                                        <em style="color: #999;">No description provided</em>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            
                            <div class="detail-label">
                                <i class="fas fa-toggle-on" style="margin-right: 0.5rem;"></i>
                                Status:
                            </div>
                            <div class="detail-value">
                                <span class="status-badge ${setting.active ? 'status-active' : 'status-inactive'}">
                                    <i class="fas fa-${setting.active ? 'check' : 'times'}"></i>
                                    ${setting.active ? 'Active' : 'Inactive'}
                                </span>
                            </div>
                        </div>
                        
                        <!-- Related Information -->
                        <div style="margin-top: 2rem; padding-top: 2rem; border-top: 1px solid #e0e0e0;">
                            <h4 style="margin-bottom: 1rem; color: #333;">
                                <i class="fas fa-info-circle"></i>
                                Information
                            </h4>
                            
                            <div class="admin-alert admin-alert-info">
                                <i class="fas fa-lightbulb"></i>
                                <div>
                                    <strong>Usage:</strong> This setting is used throughout the system to define 
                                    <span class="type-badge">${setting.type}</span> values. 
                                    <c:if test="${not setting.active}">
                                        <br><strong>Note:</strong> This setting is currently inactive and will not be available for selection.
                                    </c:if>
                                </div>
                            </div>
                        </div>
                        
                    </div>
                </div>
            </c:if>
            
            <c:if test="${setting == null}">
                <div class="detail-container">
                    <div class="detail-card">
                        <div class="admin-empty-state" style="text-align: center; padding: 3rem;">
                            <i class="fas fa-exclamation-triangle" style="font-size: 4rem; color: #ffc107; margin-bottom: 1rem;"></i>
                            <h3>Setting Not Found</h3>
                            <p>The requested setting could not be found.</p>
                            <a href="${pageContext.request.contextPath}/admin/settings" class="btn btn-primary">
                                <i class="fas fa-arrow-left"></i>
                                Back to Settings
                            </a>
                        </div>
                    </div>
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
        
        // Auto-hide alerts
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