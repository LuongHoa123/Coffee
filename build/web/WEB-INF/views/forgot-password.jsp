<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quên Mật Khẩu - CoffeeLux</title>
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
    <!-- Custom CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/forgot-password.css">
   
</head>
<body class="page-forgot-password">
        <div class="container-fluid h-100">
            <div class="row h-100 g-0">
                                    <div class="login-card">
                        <div class="card-header">
                            <div class="header-icon">
                                <i class="fas fa-key"></i>
                            </div>
                            <h2 class="card-title">Quên Mật Khẩu</h2>
                            <p class="card-subtitle">
                                Nhập email để nhận mã xác thực
                            </p>
                        </div>
                        
                        <div class="card-body">
                            
                            <!-- Success Message -->
                            <c:if test="${not empty successMessage}">
                                <div class="alert alert-success alert-custom mb-4" role="alert">
                                    <i class="fas fa-check-circle me-2"></i>
                                    ${successMessage}
                                </div>
                            </c:if>
                            
                            <!-- Error Message -->
                            <c:if test="${not empty errorMessage}">
                                <div class="alert alert-danger alert-custom mb-4" role="alert">
                                    <i class="fas fa-exclamation-circle me-2"></i>
                                    ${errorMessage}
                                </div>
                            </c:if>
                            
                            <form id="forgotPasswordForm" method="post" action="${pageContext.request.contextPath}/forgot-password" novalidate>
                                
                                <!-- Email Field -->
                                <div class="form-group">
                                    <div class="form-floating">
                                  <div style="margin-top:  -25px" class="form-label">Email</div>

                                        <input type="email" 
                                               class="form-control" 
                                               id="email" 
                                               name="email" 
                                               placeholder=" "
                                               required 
                                               autocomplete="email"
                                               value="${param.email != null ? param.email : ''}">
                                    </div>
                                </div>
                                
                                <!-- Submit Button -->
                                <button type="submit" class="btn btn-primary" id="submitButton">
                                    <span class="btn-text">
                                        <i class="fas fa-paper-plane me-2"></i>
                                        Gửi Mã Xác Thực
                                    </span>
                                    <div class="btn-loading d-none">
                                        <div class="spinner-border spinner-border-sm me-2"></div>
                                        Đang gửi...
                                    </div>
                                </button>
                                
                            </form>
                            
                            <!-- Back to Login -->
                            <div class="form-links">
                                <a href="${pageContext.request.contextPath}/login" class="back-link">
                                    <i class="fas fa-arrow-left me-2"></i>
                                    Quay lại đăng nhập
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    
    <!-- Success Animation -->
    <div class="success-animation d-none">
        <div class="success-checkmark">
            <div class="check-icon">
                <span class="icon-line line-tip"></span>
                <span class="icon-line line-long"></span>
                <div class="icon-circle"></div>
                <div class="icon-fix"></div>
            </div>
        </div>
    </div>
    
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <!-- Custom JS -->
    <script src="${pageContext.request.contextPath}/js/forgot-password.js"></script>
    
</body>
</html>