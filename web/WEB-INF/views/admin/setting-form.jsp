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
    
    <!-- Form specific CSS -->
    <style>
        .form-container {
            max-width: 800px;
            margin: 0 auto;
        }
        
        .form-card {
            background: white;
            padding: 2rem;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        
        .form-group {
            margin-bottom: 1.5rem;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 0.5rem;
            font-weight: 600;
            color: #333;
        }
        
        .form-group label.required::after {
            content: " *";
            color: #dc3545;
        }
        
        .form-control {
            width: 100%;
            padding: 0.75rem;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 1rem;
            transition: border-color 0.3s ease;
        }
        
        .form-control:focus {
            outline: none;
            border-color: #2a5298;
            box-shadow: 0 0 0 2px rgba(42, 82, 152, 0.1);
        }
        
        .form-control.error {
            border-color: #dc3545;
        }
        
        .form-text {
            font-size: 0.875rem;
            color: #666;
            margin-top: 0.25rem;
        }
        
        .form-check {
            display: flex;
            align-items: center;
            margin-bottom: 1rem;
        }
        
        .form-check input[type="checkbox"] {
            margin-right: 0.5rem;
            width: auto;
        }
        
        .form-actions {
            display: flex;
            gap: 1rem;
            justify-content: flex-end;
            padding-top: 2rem;
            border-top: 1px solid #e0e0e0;
        }
        
        .btn {
            padding: 0.75rem 1.5rem;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 1rem;
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
        
        .btn-secondary {
            background: #6c757d;
            color: white;
        }
        
        .btn-secondary:hover {
            background: #5a6268;
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
        
        .form-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 1rem;
        }
        
        @media (max-width: 768px) {
            .form-row {
                grid-template-columns: 1fr;
            }
            
            .form-actions {
                flex-direction: column-reverse;
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
                    <i class="fas fa-${action == 'add' ? 'plus' : 'edit'}"></i>
                    ${pageTitle}
                </h1>
            </div>
            
            <div class="admin-header-right">
                <a href="${pageContext.request.contextPath}/admin/settings" class="admin-btn admin-btn-outline-secondary">
                    <i class="fas fa-arrow-left"></i>
                    Back to Settings
                </a>
            </div>
        </header>
        
        <!-- Form Content -->
        <div class="admin-dashboard-content">
            
            <!-- Error Messages -->
            <c:if test="${not empty errorMessage}">
                <div class="admin-alert admin-alert-error">
                    <i class="fas fa-exclamation-circle"></i>
                    ${errorMessage}
                </div>
            </c:if>
            
            <div class="form-container">
                <div class="form-card">
                    <form method="POST" action="${pageContext.request.contextPath}/admin/settings" id="settingForm">
                        
                        <!-- Hidden fields for edit -->
                        <c:if test="${action == 'edit'}">
                            <input type="hidden" name="action" value="update">
                            <input type="hidden" name="id" value="${setting.settingID}">
                        </c:if>
                        
                        <c:if test="${action == 'add'}">
                            <input type="hidden" name="action" value="create">
                        </c:if>
                        
                        <!-- Form Fields -->
                        <div class="form-row">
                            <div class="form-group">
                                <label for="type" class="required">Setting Type</label>
                                <c:choose>
                                    <c:when test="${action == 'add'}">
                                        <select name="type" id="type" class="form-control" required>
                                            <option value="">Select Type</option>
                                            <c:forEach var="existingType" items="${settingTypes}">
                                                <option value="${existingType}" ${param.type == existingType ? 'selected' : ''}>${existingType}</option>
                                            </c:forEach>
                                            <option value="custom">+ Add New Type</option>
                                        </select>
                                        <input type="text" name="customType" id="customType" class="form-control" 
                                               style="display: none; margin-top: 0.5rem;" 
                                               placeholder="Enter new type name">
                                    </c:when>
                                    <c:otherwise>
                                        <input type="text" name="type" id="type" class="form-control" 
                                               value="${setting.type}" required>
                                    </c:otherwise>
                                </c:choose>
                                <div class="form-text">
                                    Categories: Role, Category, Unit, Status, etc.
                                </div>
                            </div>
                            
                            <div class="form-group">
                                <label for="value" class="required">Setting Value</label>
                                <input type="text" name="value" id="value" class="form-control" 
                                       value="${setting != null ? setting.value : param.value}" 
                                       required maxlength="100">
                                <div class="form-text">
                                    The actual value for this setting
                                </div>
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label for="description">Description</label>
                            <textarea name="description" id="description" class="form-control" 
                                      rows="3" maxlength="255">${setting != null ? setting.description : param.description}</textarea>
                            <div class="form-text">
                                Optional description explaining what this setting is used for
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <div class="form-check">
                                <input type="checkbox" name="isActive" id="isActive" value="true" 
                                       ${setting == null || setting.active ? 'checked' : ''}>
                                <label for="isActive">Active</label>
                            </div>
                            <div class="form-text">
                                Inactive settings will not be available for use in the system
                            </div>
                        </div>
                        
                        <!-- Form Actions -->
                        <div class="form-actions">
                            <a href="${pageContext.request.contextPath}/admin/settings" class="btn btn-outline-secondary">
                                <i class="fas fa-times"></i>
                                Cancel
                            </a>
                            
                            <button type="button" onclick="resetForm()" class="btn btn-secondary">
                                <i class="fas fa-undo"></i>
                                Reset
                            </button>
                            
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-save"></i>
                                ${action == 'add' ? 'Create Setting' : 'Update Setting'}
                            </button>
                        </div>
                        
                    </form>
                </div>
            </div>
            
        </div>
        
    </main>
    
    <!-- JavaScript -->
    <script src="${pageContext.request.contextPath}/js/admin-dashboard.js"></script>
    
    <script>
        // Handle custom type input
        document.getElementById('type').addEventListener('change', function() {
            const customTypeInput = document.getElementById('customType');
            if (this.value === 'custom') {
                customTypeInput.style.display = 'block';
                customTypeInput.required = true;
                customTypeInput.focus();
            } else {
                customTypeInput.style.display = 'none';
                customTypeInput.required = false;
                customTypeInput.value = '';
            }
        });
        
        // Form validation
        document.getElementById('settingForm').addEventListener('submit', function(e) {
            const typeSelect = document.getElementById('type');
            const customTypeInput = document.getElementById('customType');
            const valueInput = document.getElementById('value');
            
            // Clear previous errors
            document.querySelectorAll('.form-control.error').forEach(el => {
                el.classList.remove('error');
            });
            
            let hasError = false;
            
            // Validate type
            if (typeSelect.tagName === 'SELECT' && typeSelect.value === 'custom' && !customTypeInput.value.trim()) {
                customTypeInput.classList.add('error');
                hasError = true;
            }
            
            // Validate value
            if (!valueInput.value.trim()) {
                valueInput.classList.add('error');
                hasError = true;
            }
            
            if (hasError) {
                e.preventDefault();
                alert('Please fill in all required fields');
                return false;
            }
            
            // Update form data for custom type
            if (typeSelect.tagName === 'SELECT' && typeSelect.value === 'custom') {
                typeSelect.name = '';
                customTypeInput.name = 'type';
            }
        });
        
        // Reset form function
        function resetForm() {
            if (confirm('Are you sure you want to reset the form? All changes will be lost.')) {
                document.getElementById('settingForm').reset();
                
                // Reset custom type visibility
                const customTypeInput = document.getElementById('customType');
                if (customTypeInput) {
                    customTypeInput.style.display = 'none';
                    customTypeInput.required = false;
                }
                
                // Clear error states
                document.querySelectorAll('.form-control.error').forEach(el => {
                    el.classList.remove('error');
                });
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