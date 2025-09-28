<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="context-path" content="${pageContext.request.contextPath}">
    <title>Chi Tiết Người Dùng - HR Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/hr-common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/user-details.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <div class="hr-container">
        <!-- Include Sidebar -->
        <jsp:include page="sidebar.jsp">
            <jsp:param name="page" value="user-details" />
        </jsp:include>

        <!-- Main Content -->
        <main class="hr-main-content">
            <!-- Header -->
            <div class="content-header">
                <h1><i class="fas fa-user"></i> Chi Tiết Người Dùng</h1>
                <p>Thông tin chi tiết về tài khoản nhân viên</p>
                <div class="header-actions">
                    <a href="${pageContext.request.contextPath}/hr/user-list" class="btn btn-secondary">
                        <i class="fas fa-arrow-left"></i>
                        Quay Lại Danh Sách
                    </a>
                </div>
            </div>

            <!-- Error Message -->
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-error">
                    <i class="fas fa-exclamation-triangle"></i>
                    ${errorMessage}
                </div>
            </c:if>

            <!-- User Details -->
            <c:if test="${not empty user}">
                <div class="user-details-container">
                    <!-- Profile Overview -->
                    <div class="profile-overview">
                        <div class="profile-avatar">
                            <div class="avatar-circle">
                                <i class="fas fa-user"></i>
                            </div>
                            <div class="status-indicator ${user.active ? 'online' : 'offline'}"></div>
                        </div>
                        <div class="profile-info">
                            <h2>${user.fullName}</h2>
                            <p class="user-email">${user.email}</p>
                            <div class="user-badges">
                                <span class="role-badge ${user.roleID == 3 ? 'inventory' : 'barista'}">
                                    <c:choose>
                                        <c:when test="${user.roleID == 3}">
                                            <i class="fas fa-boxes"></i> Quản Lý Kho
                                        </c:when>
                                        <c:otherwise>
                                            <i class="fas fa-coffee"></i> Pha Chế
                                        </c:otherwise>
                                    </c:choose>
                                </span>
                                <span class="status-badge ${user.active ? 'active' : 'inactive'}">
                                    <i class="fas ${user.active ? 'fa-check-circle' : 'fa-times-circle'}"></i>
                                    ${user.active ? 'Hoạt động' : 'Không hoạt động'}
                                </span>
                            </div>
                        </div>
                    </div>

                    <!-- Details Grid -->
                    <div class="details-grid">
                        <!-- Basic Information -->
                        <div class="detail-card">
                            <div class="card-header">
                                <h3><i class="fas fa-info-circle"></i> Thông Tin Cơ Bản</h3>
                            </div>
                            <div class="card-content">
                                <div class="detail-row">
                                    <span class="detail-label">ID Người Dùng:</span>
                                    <span class="detail-value">#${user.userID}</span>
                                </div>
                                <div class="detail-row">
                                    <span class="detail-label">Họ và Tên:</span>
                                    <span class="detail-value">${user.fullName}</span>
                                </div>
                                <div class="detail-row">
                                    <span class="detail-label">Email:</span>
                                    <span class="detail-value">
                                        <a href="mailto:${user.email}" class="email-link">
                                            <i class="fas fa-envelope"></i>
                                            ${user.email}
                                        </a>
                                    </span>
                                </div>
                                <div class="detail-row">
                                    <span class="detail-label">Vai Trò:</span>
                                    <span class="detail-value">
                                        <span class="role-badge ${user.roleID == 3 ? 'inventory' : 'barista'}">
                                            <c:choose>
                                                <c:when test="${user.roleID == 3}">
                                                    <i class="fas fa-boxes"></i> Quản Lý Kho
                                                </c:when>
                                                <c:otherwise>
                                                    <i class="fas fa-coffee"></i> Pha Chế
                                                </c:otherwise>
                                            </c:choose>
                                        </span>
                                    </span>
                                </div>
                            </div>
                        </div>

                        <!-- Account Status -->
                        <div class="detail-card">
                            <div class="card-header">
                                <h3><i class="fas fa-user-check"></i> Trạng Thái Tài Khoản</h3>
                            </div>
                            <div class="card-content">
                                <div class="detail-row">
                                    <span class="detail-label">Trạng Thái:</span>
                                    <span class="detail-value">
                                        <span class="status-badge ${user.active ? 'active' : 'inactive'}">
                                            <i class="fas ${user.active ? 'fa-check-circle' : 'fa-times-circle'}"></i>
                                            ${user.active ? 'Hoạt động' : 'Không hoạt động'}
                                        </span>
                                    </span>
                                </div>
                                <div class="detail-row">
                                    <span class="detail-label">Ngày Tạo:</span>
                                    <span class="detail-value">
                                        <c:choose>
                                            <c:when test="${user.createdDate != null}">
                                                ${user.getFormattedCreatedDate()}
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">Chưa có thông tin</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>
                                <div class="detail-row">
                                    <span class="detail-label">Lần Đăng Nhập Cuối:</span>
                                    <span class="detail-value">
                                        <span class="text-muted">Chưa có thông tin</span>
                                    </span>
                                </div>
                            </div>
                        </div>

                        <!-- Contact Information -->
                        <div class="detail-card">
                            <div class="card-header">
                                <h3><i class="fas fa-address-book"></i> Thông Tin Liên Lạc</h3>
                            </div>
                            <div class="card-content">
                                <div class="detail-row">
                                    <span class="detail-label">Số Điện Thoại:</span>
                                    <span class="detail-value">
                                        <c:choose>
                                            <c:when test="${not empty user.phone}">
                                                <a href="tel:${user.phone}" class="phone-link">
                                                    <i class="fas fa-phone"></i>
                                                    ${user.phone}
                                                </a>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">Chưa có số điện thoại</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>
                                <div class="detail-row">
                                    <span class="detail-label">Địa Chỉ:</span>
                                    <span class="detail-value">
                                        <c:choose>
                                            <c:when test="${not empty user.address}">
                                                ${user.address}
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">Chưa có địa chỉ</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>
                            </div>
                        </div>

                        <!-- Quick Actions -->
                        <div class="detail-card">
                            <div class="card-header">
                                <h3><i class="fas fa-tools"></i> Hành Động</h3>
                            </div>
                            <div class="card-content">
                                <div class="quick-actions">
                                    <a href="${pageContext.request.contextPath}/hr/edit-user?id=${user.userID}" 
                                       class="action-btn edit">
                                        <i class="fas fa-edit"></i>
                                        Chỉnh Sửa Thông Tin
                                    </a>
                                    
                                    <c:choose>
                                        <c:when test="${user.active}">
                                            <button class="action-btn deactivate" 
                                                    onclick="toggleUserStatus(${user.userID}, false, '${user.fullName}')">
                                                <i class="fas fa-user-slash"></i>
                                                Vô Hiệu Hóa
                                            </button>
                                        </c:when>
                                        <c:otherwise>
                                            <button class="action-btn activate" 
                                                    onclick="toggleUserStatus(${user.userID}, true, '${user.fullName}')">
                                                <i class="fas fa-user-check"></i>
                                                Kích Hoạt
                                            </button>
                                        </c:otherwise>
                                    </c:choose>
                                    
                                    <button class="action-btn delete" 
                                            onclick="deleteUser(${user.userID}, '${user.fullName}')">
                                        <i class="fas fa-trash-alt"></i>
                                        Xóa Người Dùng
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </c:if>

            <c:if test="${empty user}">
                <div class="empty-state">
                    <i class="fas fa-user-slash"></i>
                    <h3>Không tìm thấy người dùng</h3>
                    <p>Người dùng bạn tìm kiếm không tồn tại hoặc đã bị xóa.</p>
                    <a href="${pageContext.request.contextPath}/hr/user-list" class="btn btn-primary">
                        <i class="fas fa-arrow-left"></i>
                        Quay Lại Danh Sách
                    </a>
                </div>
            </c:if>
        </main>
    </div>

    <!-- Include user-management.js for action functions -->
    <script src="${pageContext.request.contextPath}/js/user-management.js"></script>
</body>
</html>