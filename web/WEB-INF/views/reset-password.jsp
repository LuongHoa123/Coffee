<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đặt Lại Mật Khẩu - CoffeeLux</title>
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
    <!-- Custom CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/forgot-password.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/reset-password.css">
</head>
<body class="page--password">
        <div class="container-fluid h-100">
           
                
                <!-- Reset Password Form Section - 4 columns -->
                    <div class="login-card">
                        <div class="card-header">
                            <div class="header-icon">
                                <i class="fas fa-lock-open"></i>
                            </div>
                            <h2 class="card-title">Mật Khẩu Mới</h2>
                            <p class="card-subtitle">
                                Nhập mật khẩu mới cho tài khoản
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
                            
                            <form id="resetPasswordForm" method="post" action="${pageContext.request.contextPath}/reset-password" novalidate>
                                
                                <!-- New Password Field -->
                                <div class="form-group">
                                    
                                    <div class="form-floating">
                                        <div class="form-label"> Mật khẩu mới :  </div>
                                        <input type="password" 
                                               class="form-control" 
                                               id="newPassword" 
                                               name="newPassword" 
                                               placeholder=" "
                                               required 
                                               autocomplete="new-password">
                                    </div>
                                    <div class="password-strength" id="passwordStrength">
                                        <div class="strength-bar">
                                            <div class="strength-progress"></div>
                                        </div>
                                        <div class="strength-text">Độ mạnh mật khẩu</div>
                                    </div>
                                </div>
                                
                                <!-- Confirm Password Field -->
                                <div class="form-group">
                                    <div class="form-floating">
                                   <div class="form-label"> Xác nhận mật khẩu :  </div>

                                        <input type="password" 
                                               class="form-control" 
                                               id="confirmPassword" 
                                               name="confirmPassword" 
                                               placeholder=" "
                                               required 
                                               autocomplete="new-password">
                                    </div>
                                </div>
                                
                                <!-- Password Requirements -->
                                <div class="password-requirements mb-3">
                                    <h6 class="requirements-title">Yêu cầu mật khẩu:</h6>
                                    <ul class="requirements-list">
                                        <li class="requirement" data-requirement="length">
                                            <i class="fas fa-circle-check"></i>
                                            Ít nhất 8 ký tự
                                        </li>
                                        <li class="requirement" data-requirement="lowercase">
                                            <i class="fas fa-circle-check"></i>
                                            Chứa chữ thường (a-z)
                                        </li>
                                        <li class="requirement" data-requirement="uppercase">
                                            <i class="fas fa-circle-check"></i>
                                            Chứa chữ hoa (A-Z)
                                        </li>
                                        <li class="requirement" data-requirement="number">
                                            <i class="fas fa-circle-check"></i>
                                            Chứa số (0-9)
                                        </li>
                                        <li class="requirement" data-requirement="special">
                                            <i class="fas fa-circle-check"></i>
                                            Chứa ký tự đặc biệt (!@#$...)
                                        </li>
                                    </ul>
                                </div>
                                
                                <!-- Show Password Toggle -->
                                                                
                                <!-- Submit Button -->
                                <button type="submit" class="btn btn-primary" id="submitButton">
                                    <span class="btn-text">
                                        <i class="fas fa-save me-2"></i>
                                        Đặt Lại Mật Khẩu
                                    </span>
                                    <div class="btn-loading d-none">
                                        <div class="spinner-border spinner-border-sm me-2"></div>
                                        Đang cập nhật...
                                    </div>
                                </button>
                                
                            </form>
                            
                            
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
    <script src="${pageContext.request.contextPath}/js/reset-password.js"></script>
    
</body>
</html>