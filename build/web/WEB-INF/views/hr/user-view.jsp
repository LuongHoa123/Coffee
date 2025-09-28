<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thông Tin Người Dùng - HR Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/hr-common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/user-view.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <div class="hr-container">
        <!-- Include Sidebar -->
        <jsp:include page="sidebar.jsp">
            <jsp:param name="page" value="view-user" />
        </jsp:include>

        <!-- Main Content -->
        <main class="hr-main-content">
            <div class="user-view-container">
                <!-- View Card -->
                <div class="user-view-card">
                    <!-- Card Header -->
                    <div class="view-card-header">
                        <h1><i class="fas fa-user"></i> Thông Tin Người Dùng</h1>
                        <p>Chi tiết tài khoản nhân viên</p>
                    </div>

                    <!-- User Avatar Section -->
                    <div class="user-avatar-section">
                        <div class="user-avatar">
                            <i class="fas fa-user-circle"></i>
                        </div>
                        <h2 class="user-name">${user.fullName}</h2>
                        <p class="user-email">${user.email}</p>
                    </div>

                    <!-- User Details Section - Chỉ hiển thị 1 trường chính -->
                    <div class="user-details-section">
                        <div class="detail-item">
                            <div class="detail-icon role">
                                <i class="fas fa-user-tag"></i>
                            </div>
                            <div class="detail-content">
                                <div class="detail-label">Chức vụ</div>
                                <div class="detail-value">
                                    <span class="role-badge ${user.role.toLowerCase()}">
                                        <c:choose>
                                            <c:when test="${user.role eq 'Inventory'}">
                                                <i class="fas fa-boxes"></i> Quản lý kho
                                            </c:when>
                                            <c:when test="${user.role eq 'Barista'}">
                                                <i class="fas fa-coffee"></i> Pha chế
                                            </c:when>
                                            <c:otherwise>
                                                <i class="fas fa-user"></i> ${user.role}
                                            </c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Action Buttons -->
                    <div class="view-actions">
                        <a href="${pageContext.request.contextPath}/hr/edit-user?id=${user.userID}" class="btn btn-edit">
                            <i class="fas fa-edit"></i>
                            Chỉnh Sửa
                        </a>
                        <a href="${pageContext.request.contextPath}/hr/user-list" class="btn btn-back">
                            <i class="fas fa-arrow-left"></i>
                            Quay Lại
                        </a>
                    </div>
                </div>
            </div>
        </main>
    </div>
</body>
</html>