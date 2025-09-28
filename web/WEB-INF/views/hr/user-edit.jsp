<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="context-path" content="${pageContext.request.contextPath}">
    <title>Chỉnh Sửa Người Dùng - HR Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/hr-common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/user-form.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/user-edit.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <div class="hr-container">
        <!-- Include Sidebar -->
        <jsp:include page="sidebar.jsp">
            <jsp:param name="page" value="edit-user" />
        </jsp:include>

        <!-- Main Content -->
        <main class="hr-main-content">
            <!-- Page Header -->
            <div class="content-header">
                <h1><i class="fas fa-user-edit"></i> Chỉnh Sửa Người Dùng</h1>
                <p>Cập nhật thông tin tài khoản nhân viên</p>
                <div class="header-actions">
                    <a href="${pageContext.request.contextPath}/hr/user-list" class="btn btn-secondary">
                        <i class="fas fa-arrow-left"></i>
                        Quay Lại Danh Sách
                    </a>
                </div>
            </div>

            <div class="user-form-container">
                <div class="form-section">

    <!-- Error Messages -->
    <c:if test="${not empty error}">
        <div class="alert alert-danger">
            <i class="fas fa-exclamation-circle"></i>
            ${error}
        </div>
    </c:if>

    <form method="POST" action="${pageContext.request.contextPath}/hr/edit-user" class="user-form" id="editUserForm">
        <input type="hidden" name="action" value="update">
        <input type="hidden" name="userId" value="${user.userID}">
        <input type="hidden" name="email" value="${user.email}">
        
        <div class="form-section">
            <h3>Thông Tin Cơ Bản</h3>
            
            <div class="user-info-header">
                <div class="user-avatar-large">
                    <i class="fas fa-user"></i>
                </div>
                <div class="user-basic-info">
                    <p><strong>ID:</strong> #${user.userID}</p>
                    <p><strong>Ngày tạo:</strong> 
                        <c:choose>
                            <c:when test="${user.createdDate != null}">
                                ${user.getFormattedCreatedDate()}
                            </c:when>
                            <c:otherwise>
                                Không có thông tin
                            </c:otherwise>
                        </c:choose>
                    </p>
                    <p><strong>Trạng thái hiện tại:</strong> 
                        <span class="status-badge ${user.active ? 'active' : 'inactive'}">
                            <i class="fas ${user.active ? 'fa-check-circle' : 'fa-times-circle'}"></i>
                            ${user.active ? 'Hoạt động' : 'Không hoạt động'}
                        </span>
                    </p>
                </div>
            </div>
            
            <div class="form-row">
                <div class="form-group">
                    <label for="fullName" class="required">Họ Tên</label>
                    <input type="text" id="fullName" name="fullName" class="form-control" 
                           value="${user.fullName}" required maxlength="100"
                           placeholder="Nhập họ và tên đầy đủ">
                    <div class="field-error" id="fullNameError"></div>
                </div>
                
                <div class="form-group">
                    <label for="emailDisplay">Email <span class="text-muted">(Không thể chỉnh sửa)</span></label>
                    <input type="email" id="emailDisplay" class="form-control" 
                           value="${user.email}" readonly
                           style="background-color: #f8f9fa; cursor: not-allowed;">
                    <div class="field-help">Email không thể thay đổi sau khi tạo tài khoản</div>
                </div>
            </div>
            
            <div class="form-row">
                <div class="form-group">
                    <label for="role" class="required">Vai Trò</label>
                    <select id="role" name="role" class="form-control" required>
                        <option value="">-- Chọn vai trò --</option>
                        <option value="Inventory" ${user.role == 'Inventory' ? 'selected' : ''}>
                            Quản Lý Kho
                        </option>
                        <option value="Barista" ${user.role == 'Barista' ? 'selected' : ''}>
                            Pha Chế
                        </option>
                    </select>
                    <div class="field-error" id="roleError"></div>
                </div>
                
                <div class="form-group">
                    <label for="phone">Điện Thoại</label>
                    <input type="tel" id="phone" name="phone" class="form-control" 
                           value="${user.phone}" maxlength="20"
                           placeholder="0123456789" pattern="[0-9]{10,11}">
                    <div class="field-error" id="phoneError"></div>
                </div>
            </div>
        </div>

        <div class="form-section">
            <h3>Đổi Mật Khẩu</h3>
            <p class="form-section-description">
                <i class="fas fa-info-circle"></i>
                Để trống nếu không muốn thay đổi mật khẩu
            </p>
            
            <div class="form-row">
                <div class="form-group">
                    <label for="newPassword">Mật Khẩu Mới</label>
                    <div class="password-input-group">
                        <input type="password" id="newPassword" name="newPassword" class="form-control" 
                               minlength="6" maxlength="50"
                               placeholder="Nhập mật khẩu mới (tùy chọn)">
                        <button type="button" class="password-toggle" onclick="togglePassword('newPassword')">
                            <i class="fas fa-eye" id="newPasswordToggleIcon"></i>
                        </button>
                    </div>
                    <div class="field-error" id="passwordError"></div>
                    <div class="password-strength" id="passwordStrength"></div>
                </div>
                
                <div class="form-group">
                    <label for="confirmPassword">Xác Nhận Mật Khẩu Mới</label>
                    <div class="password-input-group">
                        <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" 
                               minlength="6" maxlength="50"
                               placeholder="Nhập lại mật khẩu mới">
                        <button type="button" class="password-toggle" onclick="togglePassword('confirmPassword')">
                            <i class="fas fa-eye" id="confirmPasswordToggleIcon"></i>
                        </button>
                    </div>
                    <div class="field-error" id="confirmPasswordError"></div>
                </div>
            </div>
        </div>

        <div class="form-section">
            <h3>Thông Tin Bổ Sung</h3>
            
            <div class="form-row">
                <div class="form-group full-width">
                    <label for="address">Địa Chỉ</label>
                    <textarea id="address" name="address" class="form-control" 
                              maxlength="255" rows="3"
                              placeholder="Nhập địa chỉ (tùy chọn)">${user.address}</textarea>
                    <div class="field-error" id="addressError"></div>
                </div>
            </div>
        </div>

        <div class="form-actions">
            <button type="submit" class="btn btn-primary" id="submitBtn">
                <i class="fas fa-save"></i>
                Cập Nhật
            </button>
            <button type="reset" class="btn btn-secondary" onclick="resetForm()">
                <i class="fas fa-undo"></i>
                Khôi Phục
            </button>
            <a href="${pageContext.request.contextPath}/hr/user-details?id=${user.userID}" class="btn btn-info">
                <i class="fas fa-eye"></i>
                Xem Chi Tiết
            </a>
            <a href="${pageContext.request.contextPath}/hr/user-list" class="btn btn-outline">
                <i class="fas fa-times"></i>
                Hủy
            </a>
        </div>
    </form>
                </div>
            </div>
        </main>
    </div>

    <script src="${pageContext.request.contextPath}/js/user-edit.js"></script>
</body>
</html>