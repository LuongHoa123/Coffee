<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="context-path" content="${pageContext.request.contextPath}">
    <title>Quản Lý Người Dùng - HR Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/hr-common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/user-list.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <div class="hr-container">
        <!-- Include Sidebar -->
        <jsp:include page="sidebar.jsp">
            <jsp:param name="page" value="user-list" />
        </jsp:include>

        <!-- Main Content -->
        <main class="hr-main-content">
            <!-- Header -->
            <div class="content-header">
                <div class="header-left">
                    <h1><i class="fas fa-users"></i> Quản Lý Người Dùng</h1>
                    <p>Danh sách tất cả người dùng trong hệ thống</p>
                </div>
                <div class="header-actions">
                    <a href="${pageContext.request.contextPath}/hr/add-user" class="btn btn-primary">
                        <i class="fas fa-user-plus"></i>
                        Thêm Người Dùng
                    </a>
                    <button class="btn btn-secondary" onclick="location.reload()">
                        <i class="fas fa-sync-alt"></i>
                        Xuất Dữ Liệu
                    </button>
                </div>
            </div>

            <!-- User Statistics -->
            <div class="user-stats-grid">
                <div class="user-stat-card">
                    <div class="user-stat-icon total">
                        <i class="fas fa-users"></i>
                    </div>
                    <div class="user-stat-info">
                        <h3>${fn:length(users)}</h3>
                        <p>Tổng Người Dùng</p>
                    </div>
                </div>
                <div class="user-stat-card">
                    <div class="user-stat-icon active">
                        <i class="fas fa-user-check"></i>
                    </div>
                    <div class="user-stat-info">
                        <h3>
                            <c:set var="activeCount" value="0" />
                            <c:forEach var="user" items="${users}">
                                <c:if test="${user.active}">
                                    <c:set var="activeCount" value="${activeCount + 1}" />
                                </c:if>
                            </c:forEach>
                            ${activeCount}
                        </h3>
                        <p>Đang Hoạt Động</p>
                    </div>
                </div>
                <div class="user-stat-card">
                    <div class="user-stat-icon inactive">
                        <i class="fas fa-user-times"></i>
                    </div>
                    <div class="user-stat-info">
                        <h3>${fn:length(users) - activeCount}</h3>
                        <p>Không Hoạt Động</p>
                    </div>
                </div>
                <div class="user-stat-card">
                    <div class="user-stat-icon inventory">
                        <i class="fas fa-warehouse"></i>
                    </div>
                    <div class="user-stat-info">
                        <h3>
                            <c:set var="inventoryCount" value="0" />
                            <c:forEach var="user" items="${users}">
                                <c:if test="${user.role == 'Inventory'}">
                                    <c:set var="inventoryCount" value="${inventoryCount + 1}" />
                                </c:if>
                            </c:forEach>
                            ${inventoryCount}
                        </h3>
                        <p>Quản Lý Kho</p>
                    </div>
                </div>
            </div>

            <!-- Success/Error Messages -->
            <c:if test="${not empty success}">
                <div class="alert alert-success">
                    <i class="fas fa-check-circle"></i>
                    ${success}
                </div>
            </c:if>
            
            <c:if test="${not empty error}">
                <div class="alert alert-error">
                    <i class="fas fa-exclamation-circle"></i>
                    ${error}
                </div>
            </c:if>

            <!-- Search Section -->
            <div class="search-section">
                <form method="GET" action="${pageContext.request.contextPath}/hr/user-list">
                    <div class="search-row">
                        <div class="search-group">
                            <input type="text" name="search" value="${param.search}" 
                                   placeholder="Tìm kiếm theo tên, email hoặc số điện thoại..." class="search-input">
                        </div>
                        <div class="search-actions">
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-search"></i> Tìm kiếm
                            </button>
                            <a href="${pageContext.request.contextPath}/hr/user-list" class="btn btn-secondary">
                                <i class="fas fa-times"></i> Xóa
                            </a>
                        </div>
                    </div>
                </form>
            </div>

            

            <!-- Users Table -->
            <div class="table-section">
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Họ Tên</th>
                                <th>Email</th>
                                <th>Vai Trò</th>
                                <th>Điện Thoại</th>
                                <th>Trạng Thái</th>
                                <th>Ngày Tạo</th>
                                <th>Hành Động</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty users}">
                                    <c:forEach var="user" items="${users}">
                                        <tr>
                                            <td>${user.userID}</td>
                                            <td>
                                                <div class="user-info">
                                                    <div class="user-avatar">
                                                        <i class="fas fa-user-circle"></i>
                                                    </div>
                                                    <span class="user-name">${user.fullName}</span>
                                                </div>
                                            </td>
                                            <td>${user.email}</td>
                                            <td>
                                                <span class="role-badge role-${fn:toLowerCase(user.role)}">
                                                    ${user.role}
                                                </span>
                                            </td>
                                            <td>${user.phone != null ? user.phone : '-'}</td>
                                            <td>
                                                <span class="status-badge ${user.active ? 'active' : 'inactive'}">
                                                    <i class="fas ${user.active ? 'fa-check-circle' : 'fa-times-circle'}"></i>
                                                    ${user.active ? 'Hoạt động' : 'Không hoạt động'}
                                                </span>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${user.createdDate != null}">
                                                        ${user.getFormattedCreatedDate()}
                                                    </c:when>
                                                    <c:otherwise>
                                                        -
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <div class="action-buttons">
                                                    <button class="btn-action btn-view" 
                                                            onclick="viewUser(${user.userID})"
                                                            title="Xem chi tiết">
                                                        <i class="fas fa-eye"></i>
                                                    </button>
                                                    
                                                    <button class="btn-action btn-edit" 
                                                            onclick="editUser(${user.userID})" 
                                                            title="Chỉnh sửa">
                                                        <i class="fas fa-edit"></i>
                                                    </button>
                                                    
                                                    <c:choose>
                                                        <c:when test="${user.active}">
                                                            <button class="btn-action btn-deactivate" 
                                                                    onclick="toggleUserStatus(${user.userID}, false, '${user.fullName}')"
                                                                    title="Vô hiệu hóa">
                                                                <i class="fas fa-ban"></i>
                                                            </button>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <button class="btn-action btn-activate" 
                                                                    onclick="toggleUserStatus(${user.userID}, true, '${user.fullName}')"
                                                                    title="Kích hoạt">
                                                                <i class="fas fa-check"></i>
                                                            </button>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="8" class="text-center">
                                            <i class="fas fa-users fa-3x text-muted"></i>
                                            <p>Không có người dùng nào được tìm thấy</p>
                                        </td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>
        </main>
    </div>

    <!-- Status Change Confirmation Modal -->
    <div id="statusModal" class="modal" style="display: none;">
        <div class="modal-content">
            <div class="modal-header">
                <h3 id="statusModalTitle">Xác Nhận Thay Đổi Trạng Thái</h3>
                <span class="close" onclick="closeStatusModal()">&times;</span>
            </div>
            <div class="modal-body">
                <p id="statusModalMessage"></p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" onclick="closeStatusModal()">Hủy</button>
                <button type="button" class="btn btn-primary" onclick="confirmStatusChange()">Xác Nhận</button>
            </div>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/js/user-management.js"></script>
    
 
</body>
</html>