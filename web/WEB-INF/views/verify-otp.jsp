<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Xác Thực OTP - CoffeeLux</title>
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
    <!-- Custom CSS -->
    <link rel="stylesheet" href="<c:url value='/css/login.css'/>">
    <link rel="stylesheet" href="<c:url value='/css/forgot-password.css'/>">
    <link rel="stylesheet" href="<c:url value='/css/verify-otp.css'/>">
    
</head>
<body>
        <div class="container-fluid h-100">
            <div class="row h-100 g-0">
                
               
                
                <!-- OTP Form Section - 4 columns -->
                <div class="col-lg-4 login-section">
                    <div class="login-card">
                        <div class="card-header">
                            <div class="header-icon">
                                <i class="fas fa-mobile-alt"></i>
                            </div>
                            <h2 class="card-title">Nhập Mã OTP</h2>
                            <p class="card-subtitle">
                                Mã xác thực 6 chữ số
                            </p>
                            
                            <!-- Countdown Timer -->
                            <div class="countdown-container">
                                <div class="countdown-circle">
                                    <div class="countdown-text">
                                        <span class="countdown-number" id="countdownTime">10:00</span>
                                        <span class="countdown-label">Còn lại</span>
                                    </div>
                                </div>
                            </div>
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
                            
                            <form id="verifyOTPForm" method="post" action="${pageContext.request.contextPath}/verify-otp" novalidate>
                                
                                <!-- OTP Input Fields -->
                                <div class="otp-input-container mb-4">
                                    <div class="otp-inputs">
                                        <input type="text" class="otp-digit" maxlength="1" data-index="0">
                                        <input type="text" class="otp-digit" maxlength="1" data-index="1">
                                        <input type="text" class="otp-digit" maxlength="1" data-index="2">
                                        <input type="text" class="otp-digit" maxlength="1" data-index="3">
                                        <input type="text" class="otp-digit" maxlength="1" data-index="4">
                                        <input type="text" class="otp-digit" maxlength="1" data-index="5">
                                    </div>
                                    <input type="hidden" name="otpCode" id="otpCode" value="${param.otpCode != null ? param.otpCode : ''}">
                                </div>
                                
                                <!-- Submit Button -->
                                <button type="submit" class="btn btn-primary" id="submitButton">
                                    <span class="btn-text">
                                        <i class="fas fa-check me-2"></i>
                                        Xác Thực OTP
                                    </span>
                                    <div class="btn-loading d-none">
                                        <div class="spinner-border spinner-border-sm me-2"></div>
                                        Đang xác thực...
                                    </div>
                                </button>
                                
                            </form>
                            
                            <!-- Resend OTP -->
                            <div class="form-links">
                                <div class="resend-container">
                                    <p class="resend-text">Không nhận được mã?</p>
                                    <button class="resend-btn" id="resendBtn" disabled>
                                        <i class="fas fa-redo-alt me-2"></i>
                                        <span class="resend-text">Gửi lại (<span id="resendCountdown">60</span>s)</span>
                                    </button>
                                </div>
                                
                                <a href="${pageContext.request.contextPath}/forgot-password" class="back-link">
                                    <i class="fas fa-arrow-left me-2"></i>
                                    Thay đổi email
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
    <script>
        // Pass server data to JavaScript
        window.otpData = {
            remainingTime: 10, // Default 10 minutes
            initialOtpCode: ''
        };
    </script>
    <script src="<c:url value='/js/verify-otp.js'/>"></script>
    
</body>
</html>