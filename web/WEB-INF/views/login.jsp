<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập - Coffee Shop Management</title>
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    
    <!-- External Libraries -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    
    <!-- Custom Styles -->
    <link href="${pageContext.request.contextPath}/css/login.css" rel="stylesheet">
</head>
<body>
    <div class="container-fluid p-0">
        <div class="row min-vh-100 g-0">
            <!-- Left Side - Hero Section (6/12) -->
            <div class="col-lg-8 hero-section">
                <div class="hero-content">
                <div class="hero-brand">
                    <i class="coffee-icon fas fa-coffee"></i>
                    <span class="brand-text">CoffeeLux</span>
                </div>
                
                <h1 class="hero-title">
                    Premium Coffee
                </h1>
                
                <p class="hero-subtitle">
                    Premium coffee experience crafted with passion. From bean to cup, 
                    we deliver perfection in every sip for coffee lovers everywhere.
                </p>
                
                <!-- Modern Feature Cards -->
                <div class="customer-features">
                    <div class="features-grid">
                        <div class="feature-item">
                            <i class="feature-icon fas fa-leaf"></i>
                            <h3 class="feature-title">100% Organic</h3>
                            <p class="feature-description">Premium beans sourced from sustainable farms</p>
                        </div>
                        
                        <div class="feature-item">
                            <i class="feature-icon fas fa-shipping-fast"></i>
                            <h3 class="feature-title">Express Delivery</h3>
                            <p class="feature-description">Fresh coffee delivered to your doorstep</p>
                        </div>
                        
                        <div class="feature-item">
                            <i class="feature-icon fas fa-award"></i>
                            <h3 class="feature-title">Award Winning</h3>
                            <p class="feature-description">Recognized excellence in coffee craftsmanship</p>
                        </div>
                        
                        <div class="feature-item">
                            <i class="feature-icon fas fa-clock"></i>
                            <h3 class="feature-title">24/7 Service</h3>
                            <p class="feature-description">Order anytime, anywhere with our app</p>
                        </div>
                    </div>
                </div>
                
                <!-- Modern CTA Buttons -->
                <div class="customer-actions">
                    <a href="#" class="btn-primary-hero">
                        <i class="fas fa-shopping-cart"></i>
                        Order Now
                    </a>
                    <a href="#" class="btn-secondary-hero">
                        <i class="fas fa-book-open"></i>
                        View Menu
                    </a>
                </div>
                    
                </div>
            </div>
            
            <!-- Right Side - Login Section (4/12) -->
            <div class="col-lg-4 login-section">
                <div class="login-container">
                <div class="login-card">
                    <div class="login-header">
                        <div class="login-brand">
                            <i class="login-brand-icon fas fa-coffee"></i>
                            <span class="login-brand-text">CoffeeLux</span>
                        </div>
                        <h1 class="login-title">Welcome Back</h1>
                    </div>
                        
                    <div class="login-body">
                            <!-- Display Messages -->
                            <c:if test="${not empty errorMessage}">
                                <div class="alert alert-danger" role="alert">
                                    <i class="fas fa-exclamation-triangle me-2"></i>
                                    ${errorMessage}
                                </div>
                            </c:if>
                            
                            <c:if test="${not empty param.message}">
                                <div class="alert alert-success" role="alert">
                                    <i class="fas fa-check-circle me-2"></i>
                                    ${param.message}
                                </div>
                            </c:if>
                            
                            <!-- Login Form -->
                            <form id="loginForm" action="${pageContext.request.contextPath}/login" method="post">
                                <div class="form-group">
                                    <div class="form-floating">
                                        <div class="form-label" style="margin-top:  -25px" for="email">Work Email</div>

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
                                
                                <div class="form-group">
                                    <div class="form-floating">
                                    <div class="form-label" style="margin-top:  -25px" for="password">Password</div>

                                        <input type="password" 
                                               class="form-control" 
                                               id="password" 
                                               name="password" 
                                               placeholder=" "
                                               required
                                               autocomplete="current-password">
                                    </div>
                                </div>
                                
                                <div class="form-check">
                                    <input class="form-check-input" 
                                           type="checkbox" 
                                           name="rememberMe" 
                                           id="rememberMe">
                                    <label class="form-check-label" for="rememberMe">
                                        Remember me on this device
                                    </label>
                                </div>
                                
                                <button type="submit" 
                                        id="submitButton" 
                                        class="btn btn-primary">
                                    <i class="fas fa-arrow-right"></i>
                                    Sign In
                                </button>
                            </form>
                            
                            <!-- Divider -->
                            <div class="divider">
                                <span>or continue with</span>
                            </div>
                            
                            <!-- Google Login -->
                            <button type="button" 
                                    id="googleLoginBtn" 
                                    class="btn btn-google">
                                <i class="fab fa-google"></i>
                                Sign in with Google
                            </button>
                            
                            <!-- Additional Links -->
                            <div class="text-center mt-4">
                                <a href="${pageContext.request.contextPath}/forgot-password" class="forgot-password">
                                    <i class="fas fa-key me-1"></i>
                                    Quên mật khẩu?
                                </a>
                            </div>
                            
                            <!-- System Info -->
                            <div class="text-center mt-4">
                                <small class="text-muted">
                                    <i class="fas fa-shield-alt me-1"></i>
                                    Hệ thống bảo mật SSL 256-bit
                                </small>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <!-- Scripts -->
    <script src="${pageContext.request.contextPath}/js/login.js"></script>
</body>
</html>