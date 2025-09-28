<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thêm Người Dùng Mới - HR Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/hr-common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/user-form.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <div class="hr-container">
        <!-- Include Sidebar -->
        <jsp:include page="sidebar.jsp">
            <jsp:param name="page" value="add-user" />
        </jsp:include>

        <!-- Main Content -->
        <main class="hr-main-content">
            <!-- Header -->
            <div class="content-header">
                <div class="header-left">
                    <h1><i class="fas fa-user-plus"></i> Thêm Người Dùng Mới</h1>
                    <p>Tạo tài khoản người dùng mới trong hệ thống</p>
                </div>
                <div class="header-actions">
                    <a href="${pageContext.request.contextPath}/hr/user-list" class="btn btn-secondary">
                        <i class="fas fa-arrow-left"></i>
                        Quay Lại Danh Sách
                    </a>
                </div>
            </div>

            <!-- Form Section -->
            <div class="user-form-container">
                <!-- Error/Success Messages -->
                <c:if test="${not empty error}">
                    <div class="alert alert-error">
                        <i class="fas fa-exclamation-circle"></i>
                        ${error}
                    </div>
                </c:if>

                <!-- Add User Form -->
                <form method="POST" action="${pageContext.request.contextPath}/hr/add-user" class="user-form">
                    
                    <div class="form-grid">
                        <!-- Basic Information -->
                        <div class="form-section-title">
                            <h3><i class="fas fa-user"></i> Thông Tin Cơ Bản</h3>
                        </div>
                        
                        <div class="form-row">
                            <div class="form-group">
                                <label for="fullName" class="required">Họ và Tên</label>
                                <input type="text" id="fullName" name="fullName" 
                                       class="form-control" required 
                                       value="${not empty formData ? formData.fullName : param.fullName}"
                                       placeholder="Nhập họ và tên đầy đủ">
                            </div>
                            
                            <div class="form-group">
                                <label for="email" class="required">Email</label>
                                <input type="email" id="email" name="email" 
                                       class="form-control" required 
                                       value="${not empty formData ? formData.email : param.email}"
                                       placeholder="user@company.com">
                            </div>
                        </div>
                        
                        <div class="form-row">
                            <div class="form-group">
                                <label for="role">Vai Trò</label>
                                <select id="role" name="role" class="form-control">
                                    <c:forEach var="roleName" items="${availableRoles}">
                                        <c:set var="selectedRole" value="${not empty formData ? formData.role : param.role}" />
                                        <option value="${roleName}" ${selectedRole == roleName ? 'selected' : ''}>
                                            <c:choose>
                                                <c:when test="${roleName == 'Inventory'}">Quản Lý Kho</c:when>
                                                <c:when test="${roleName == 'Barista'}">Pha Chế</c:when>
                                                <c:otherwise>${roleName}</c:otherwise>
                                            </c:choose>
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                            
                            <div class="form-group">
                                <label for="phone">Số Điện Thoại</label>
                                <input type="tel" id="phone" name="phone" 
                                       class="form-control" 
                                       value="${not empty formData ? formData.phone : param.phone}"
                                       placeholder="0123456789">
                            </div>
                        </div>

                        <!-- Security Information -->
                        <div class="form-section-title">
                            <h3><i class="fas fa-lock"></i> Thông Tin Bảo Mật</h3>
                        </div>
                        
                        <div class="form-row">
                            <div class="form-group">
                                <label for="password" class="required">Mật Khẩu</label>
                                <input type="password" id="password" name="password" 
                                       class="form-control" required 
                                       placeholder="Nhập mật khẩu">
                            </div>
                            
                            <div class="form-group">
                                <label for="confirmPassword" class="required">Xác Nhận Mật Khẩu</label>
                                <input type="password" id="confirmPassword" name="confirmPassword" 
                                       class="form-control" required 
                                       placeholder="Nhập lại mật khẩu">
                            </div>
                        </div>

                        <!-- Additional Information -->
                        <div class="form-section-title">
                            <h3><i class="fas fa-info-circle"></i> Thông Tin Bổ Sung</h3>
                        </div>
                        
                        <div class="form-row">
                            <div class="form-group full-width">
                                <label for="address">Địa Chỉ</label>
                                <textarea id="address" name="address" class="form-control" 
                                          rows="3" placeholder="Nhập địa chỉ">${not empty formData ? formData.address : param.address}</textarea>
                            </div>
                        </div>
                    </div>

                    <!-- Form Actions -->
                    <div class="form-actions">
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save"></i>
                            Tạo Người Dùng
                        </button>
                        <button type="reset" class="btn btn-secondary">
                            <i class="fas fa-undo"></i>
                            Làm Mới
                        </button>
                        <a href="${pageContext.request.contextPath}/hr/user-list" class="btn btn-outline">
                            <i class="fas fa-times"></i>
                            Hủy Bỏ
                        </a>
                    </div>
                </form>
            </div>
        </main>
    </div>

    <!-- Scripts -->
    <script>
        // Confirm password validation
        document.getElementById('confirmPassword').addEventListener('input', function() {
            const password = document.getElementById('password').value;
            const confirmPassword = this.value;
            
            if (confirmPassword && password !== confirmPassword) {
                this.setCustomValidity('Mật khẩu không khớp');
            } else {
                this.setCustomValidity('');
            }
        });
    </script>
</body>
</html>