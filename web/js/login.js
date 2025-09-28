// ===== LOGIN PAGE FUNCTIONALITY =====

class LoginManager {
    constructor() {
        this.form = document.querySelector('#loginForm');
        this.emailInput = document.querySelector('#email');
        this.passwordInput = document.querySelector('#password');
        this.rememberMeCheckbox = document.querySelector('#rememberMe');
        this.submitButton = document.querySelector('#submitButton');
        
        // Animation states
        this.isLoading = false;
        this.originalButtonText = this.submitButton?.textContent || 'Đăng Nhập';
        
        this.init();
    }
    
    init() {
        this.setupEventListeners();
        this.setupFormValidation();
        this.setupAnimations();
        this.loadRememberedCredentials();
        this.initParallax();
        this.initTypingEffect();
    }
    
    setupEventListeners() {
        // Form submission
        if (this.form) {
            this.form.addEventListener('submit', (e) => this.handleSubmit(e));
        }
        
        // Clear error styling on input
        if (this.emailInput) {
            this.emailInput.addEventListener('input', () => this.clearEmailError());
        }
        
        if (this.passwordInput) {
            this.passwordInput.addEventListener('input', () => this.clearPasswordError());
        }
        
        // Remember me functionality
        if (this.rememberMeCheckbox) {
            this.rememberMeCheckbox.addEventListener('change', () => this.handleRememberMe());
        }
        
        // Google login button
        const googleBtn = document.querySelector('#googleLoginBtn');
        if (googleBtn) {
            googleBtn.addEventListener('click', () => this.handleGoogleLogin());
        }
        
        // Enter key handling
        document.addEventListener('keypress', (e) => {
            if (e.key === 'Enter' && this.isFormVisible()) {
                e.preventDefault();
                this.handleSubmit(e);
            }
        });
    }
    
    setupFormValidation() {
        // Email validation patterns
        this.emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
        
        // Password requirements
        this.passwordMinLength = 6;
    }
    
    setupAnimations() {
        // Animate form elements on load
        this.animateFormElements();
        
        // Setup floating labels
        this.setupFloatingLabels();
        
        // Auto-hide alerts
        this.setupAlertAutoHide();
        
        // Setup parallax effects
        this.setupParallaxElements();
    }
    
    initParallax() {
        // Simple parallax effect for hero elements
        window.addEventListener('scroll', () => {
            const scrolled = window.pageYOffset;
            const parallaxElements = document.querySelectorAll('.hero-content, .coffee-icon');
            
            parallaxElements.forEach(element => {
                const speed = element.dataset.speed || 0.5;
                element.style.transform = `translateY(${scrolled * speed}px)`;
            });
        });
    }
    
    initTypingEffect() {
        const heroTitle = document.querySelector('.hero-title');
        if (heroTitle && heroTitle.textContent) {
            const originalText = heroTitle.textContent;
            heroTitle.textContent = '';
            heroTitle.style.borderRight = '3px solid var(--accent-color)';
            
            let i = 0;
            const typeWriter = () => {
                if (i < originalText.length) {
                    heroTitle.textContent += originalText.charAt(i);
                    i++;
                    setTimeout(typeWriter, 100);
                } else {
                    // Remove cursor after typing
                    setTimeout(() => {
                        heroTitle.style.borderRight = 'none';
                    }, 1000);
                }
            };
            
            setTimeout(typeWriter, 1000);
        }
    }
    
    setupParallaxElements() {
        // Add floating animation to icons
        const icons = document.querySelectorAll('.coffee-icon, .feature-icon');
        icons.forEach((icon, index) => {
            icon.style.animation = `float 3s ease-in-out infinite`;
            icon.style.animationDelay = `${index * 0.5}s`;
        });
    }
    
    validateEmail() {
        const email = this.emailInput.value.trim();
        const isValid = this.emailPattern.test(email);
        
        if (!email) {
            this.showFieldError('email', 'Vui lòng nhập email');
            return false;
        }
        
        if (!isValid) {
            this.showFieldError('email', 'Email không hợp lệ');
            return false;
        }
        
        this.clearFieldError('email');
        return true;
    }
    
    validatePassword() {
        const password = this.passwordInput.value;
        
        if (!password) {
            this.showFieldError('password', 'Vui lòng nhập mật khẩu');
            return false;
        }
        
        if (password.length < this.passwordMinLength) {
            this.showFieldError('password', `Mật khẩu phải có ít nhất ${this.passwordMinLength} ký tự`);
            return false;
        }
        
        this.clearFieldError('password');
        return true;
    }
    
    showFieldError(fieldName, message) {
        const field = document.querySelector(`#${fieldName}`);
        if (!field) return;
        
        // Remove existing error
        this.clearFieldError(fieldName);
        
        // Add error class
        field.classList.add('error');
        
        // Create error message element
        const errorElement = document.createElement('div');
        errorElement.className = 'field-error';
        errorElement.textContent = message;
        
        // Insert after the field
        field.parentNode.insertBefore(errorElement, field.nextSibling);
        
        // Focus on field
        field.focus();
    }
    
    clearFieldError(fieldName) {
        const field = document.querySelector(`#${fieldName}`);
        if (!field) return;
        
        field.classList.remove('error');
        
        const errorElement = field.parentNode.querySelector('.field-error');
        if (errorElement) {
            errorElement.remove();
        }
    }
    
    clearEmailError() {
        this.clearFieldError('email');
    }
    
    clearPasswordError() {
        this.clearFieldError('password');
    }
    
    handleSubmit(e) {
        e.preventDefault();
        
        // Prevent double submission
        if (this.isLoading) return;
        
        // Validate all fields
        const isEmailValid = this.validateEmail();
        const isPasswordValid = this.validatePassword();
        
        if (!isEmailValid || !isPasswordValid) {
            this.showNotification('Vui lòng kiểm tra lại thông tin đăng nhập', 'error');
            this.shakeForm();
            return;
        }
        
        // Show loading state with animation
        this.setLoadingState(true);
        
        // Save credentials if remember me is checked
        if (this.rememberMeCheckbox && this.rememberMeCheckbox.checked) {
            this.saveCredentials();
        } else {
            this.clearSavedCredentials();
        }
        
        // Add success animation before submit
        this.showSuccessAnimation();
        
        // Submit form with delay for animation
        setTimeout(() => {
            this.form.submit();
        }, 800);
    }
    
    shakeForm() {
        const loginCard = document.querySelector('.login-card');
        if (loginCard) {
            loginCard.style.animation = 'shake 0.5s ease-in-out';
            setTimeout(() => {
                loginCard.style.animation = '';
            }, 500);
        }
    }
    
    showSuccessAnimation() {
        // Add success class to form controls
        const formControls = document.querySelectorAll('.form-control');
        formControls.forEach(control => {
            control.classList.add('success');
        });
        
        // Show success checkmarks
        const icons = document.querySelectorAll('.input-icon');
        icons.forEach(icon => {
            const originalClass = icon.className;
            icon.className = 'input-icon fas fa-check';
            icon.style.color = '#28a745';
            
            setTimeout(() => {
                icon.className = originalClass;
                icon.style.color = '';
            }, 2000);
        });
    }
    
    handleGoogleLogin() {
        this.showNotification('Tính năng đăng nhập Google đang được phát triển...', 'info');
        
        // TODO: Implement Google OAuth2
        // This is where you would integrate Google Sign-In
        console.log('Google login clicked');
    }
    
    handleRememberMe() {
        if (this.rememberMeCheckbox.checked) {
            this.showNotification('Thông tin đăng nhập sẽ được lưu trên thiết bị này', 'info');
        }
    }
    
    setLoadingState(isLoading) {
        if (!this.submitButton) return;
        
        this.isLoading = isLoading;
        
        if (isLoading) {
            this.submitButton.classList.add('loading');
            this.submitButton.disabled = true;
            this.submitButton.innerHTML = `
                <div class="loading-dots">
                    <span class="dot"></span>
                    <span class="dot"></span>
                    <span class="dot"></span>
                </div>
                <span class="ms-2">Đang đăng nhập...</span>
            `;
            this.submitButton.style.transform = 'scale(0.98)';
        } else {
            this.submitButton.classList.remove('loading');
            this.submitButton.disabled = false;
            this.submitButton.innerHTML = '<i class="fas fa-sign-in-alt me-2"></i>' + this.originalButtonText;
            this.submitButton.style.transform = '';
        }
    }
    
    saveCredentials() {
        if (!this.emailInput || !this.passwordInput) return;
        
        try {
            const credentials = {
                email: this.emailInput.value,
                timestamp: Date.now()
            };
            localStorage.setItem('rememberedEmail', JSON.stringify(credentials));
        } catch (error) {
            console.warn('Could not save credentials:', error);
        }
    }
    
    loadRememberedCredentials() {
        try {
            const saved = localStorage.getItem('rememberedEmail');
            if (saved) {
                const credentials = JSON.parse(saved);
                
                // Check if credentials are not too old (30 days)
                const thirtyDays = 30 * 24 * 60 * 60 * 1000;
                if (Date.now() - credentials.timestamp < thirtyDays) {
                    if (this.emailInput) {
                        this.emailInput.value = credentials.email;
                    }
                    if (this.rememberMeCheckbox) {
                        this.rememberMeCheckbox.checked = true;
                    }
                } else {
                    // Remove old credentials
                    localStorage.removeItem('rememberedEmail');
                }
            }
        } catch (error) {
            console.warn('Could not load saved credentials:', error);
        }
    }
    
    clearSavedCredentials() {
        try {
            localStorage.removeItem('rememberedEmail');
        } catch (error) {
            console.warn('Could not clear saved credentials:', error);
        }
    }
    
    animateFormElements() {
        // Animate form fields with stagger effect
        const formGroups = document.querySelectorAll('.form-group');
        formGroups.forEach((group, index) => {
            group.style.opacity = '0';
            group.style.transform = 'translateY(20px)';
            
            setTimeout(() => {
                group.style.transition = 'all 0.5s ease';
                group.style.opacity = '1';
                group.style.transform = 'translateY(0)';
            }, index * 100);
        });
        
        // Animate buttons
        const buttons = document.querySelectorAll('.btn');
        buttons.forEach((button, index) => {
            button.style.opacity = '0';
            button.style.transform = 'scale(0.9)';
            
            setTimeout(() => {
                button.style.transition = 'all 0.3s ease';
                button.style.opacity = '1';
                button.style.transform = 'scale(1)';
            }, (formGroups.length + index) * 100);
        });
    }
    
    setupFloatingLabels() {
        const inputs = document.querySelectorAll('.form-control');
        
        inputs.forEach(input => {
            // Check initial state
            this.updateLabelState(input);
            
            // Listen for changes
            input.addEventListener('focus', () => this.updateLabelState(input));
            input.addEventListener('blur', () => this.updateLabelState(input));
            input.addEventListener('input', () => this.updateLabelState(input));
        });
    }
    
    updateLabelState(input) {
        const label = input.parentNode.querySelector('.form-label');
        if (!label) return;
        
        const hasValue = input.value.trim() !== '';
        const isFocused = document.activeElement === input;
        
        if (hasValue || isFocused) {
            label.classList.add('active');
        } else {
            label.classList.remove('active');
        }
    }
    
    setupAlertAutoHide() {
        const alerts = document.querySelectorAll('.alert');
        alerts.forEach(alert => {
            // Add close button
            const closeBtn = document.createElement('button');
            closeBtn.innerHTML = '&times;';
            closeBtn.className = 'alert-close';
            closeBtn.addEventListener('click', () => this.hideAlert(alert));
            alert.appendChild(closeBtn);
            
            // Auto hide after 5 seconds
            setTimeout(() => {
                this.hideAlert(alert);
            }, 5000);
        });
    }
    
    hideAlert(alert) {
        if (alert) {
            alert.style.transition = 'all 0.3s ease';
            alert.style.opacity = '0';
            alert.style.transform = 'translateY(-10px)';
            
            setTimeout(() => {
                if (alert.parentNode) {
                    alert.parentNode.removeChild(alert);
                }
            }, 300);
        }
    }
    
    showNotification(message, type = 'info') {
        // Remove existing notifications
        const existingNotifications = document.querySelectorAll('.notification');
        existingNotifications.forEach(notif => notif.remove());
        
        // Create notification element
        const notification = document.createElement('div');
        notification.className = `notification notification-${type}`;
        notification.innerHTML = `
            <div class="notification-content">
                <i class="fas fa-${this.getNotificationIcon(type)} me-2"></i>
                ${message}
            </div>
        `;
        
        // Add to page
        document.body.appendChild(notification);
        
        // Animate in
        setTimeout(() => {
            notification.classList.add('show');
        }, 10);
        
        // Auto hide
        setTimeout(() => {
            this.hideNotification(notification);
        }, 3000);
    }
    
    hideNotification(notification) {
        notification.classList.remove('show');
        setTimeout(() => {
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
        }, 300);
    }
    
    getNotificationIcon(type) {
        const icons = {
            'success': 'check-circle',
            'error': 'exclamation-triangle',
            'warning': 'exclamation-circle',
            'info': 'info-circle'
        };
        return icons[type] || 'info-circle';
    }
    
    isFormVisible() {
        return this.form && this.form.offsetParent !== null;
    }
}

// ===== FEATURE CARDS ANIMATION =====
class FeatureAnimator {
    constructor() {
        this.setupIntersectionObserver();
        this.setupHoverEffects();
    }
    
    setupIntersectionObserver() {
        const options = {
            threshold: 0.1,
            rootMargin: '0px 0px -50px 0px'
        };
        
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('animate-in');
                }
            });
        }, options);
        
        document.querySelectorAll('.feature-card').forEach(card => {
            observer.observe(card);
        });
    }
    
    setupHoverEffects() {
        document.querySelectorAll('.feature-card').forEach(card => {
            card.addEventListener('mouseenter', () => {
                const icon = card.querySelector('.feature-icon');
                if (icon) {
                    icon.style.transform = 'scale(1.1) rotate(5deg)';
                }
            });
            
            card.addEventListener('mouseleave', () => {
                const icon = card.querySelector('.feature-icon');
                if (icon) {
                    icon.style.transform = 'scale(1) rotate(0deg)';
                }
            });
        });
    }
}

// ===== COFFEE STEAM ANIMATION =====
class CoffeeAnimator {
    constructor() {
        this.setupSteamEffect();
        this.setupParticles();
    }
    
    setupSteamEffect() {
        const coffeeIcon = document.querySelector('.coffee-icon');
        if (!coffeeIcon) return;
        
        // Add steam particles
        for (let i = 0; i < 3; i++) {
            const steam = document.createElement('div');
            steam.className = 'steam-particle';
            steam.style.cssText = `
                position: absolute;
                width: 4px;
                height: 20px;
                background: rgba(255,255,255,0.3);
                border-radius: 2px;
                left: ${45 + i * 5}%;
                top: -20px;
                animation: steam-rise ${2 + i * 0.5}s ease-in-out infinite;
                animation-delay: ${i * 0.3}s;
            `;
            
            coffeeIcon.style.position = 'relative';
            coffeeIcon.appendChild(steam);
        }
    }
    
    setupParticles() {
        // Add floating coffee bean particles in background
        const hero = document.querySelector('.hero-section');
        if (!hero) return;
        
        for (let i = 0; i < 5; i++) {
            setTimeout(() => {
                this.createFloatingParticle(hero);
            }, i * 1000);
        }
    }
    
    createFloatingParticle(container) {
        const particle = document.createElement('div');
        particle.innerHTML = '☕';
        particle.style.cssText = `
            position: absolute;
            font-size: ${Math.random() * 20 + 10}px;
            opacity: ${Math.random() * 0.3 + 0.1};
            left: ${Math.random() * 100}%;
            top: 100%;
            pointer-events: none;
            animation: float-up ${Math.random() * 10 + 15}s linear infinite;
        `;
        
        container.appendChild(particle);
        
        // Remove particle after animation
        setTimeout(() => {
            if (particle.parentNode) {
                particle.parentNode.removeChild(particle);
            }
        }, 25000);
    }
}

// ===== INITIALIZE ON DOM READY =====
document.addEventListener('DOMContentLoaded', () => {
    // Initialize login functionality
    new LoginManager();
    
    // Initialize feature animations
    new FeatureAnimator();
    
    // Initialize coffee animations
    new CoffeeAnimator();
    
    // Add custom CSS animations
    addCustomAnimations();
});

// ===== CUSTOM CSS ANIMATIONS =====
function addCustomAnimations() {
    const style = document.createElement('style');
    style.textContent = `
        @keyframes steam-rise {
            0% { 
                transform: translateY(0) scale(1);
                opacity: 0.7;
            }
            50% {
                transform: translateY(-15px) scale(1.1);
                opacity: 1;
            }
            100% { 
                transform: translateY(-30px) scale(0.8);
                opacity: 0;
            }
        }
        
        @keyframes float-up {
            0% {
                transform: translateY(0) rotate(0deg);
                opacity: 0;
            }
            10% {
                opacity: 0.3;
            }
            90% {
                opacity: 0.1;
            }
            100% {
                transform: translateY(-100vh) rotate(360deg);
                opacity: 0;
            }
        }
        
        .feature-card.animate-in {
            animation: slideInUp 0.6s ease-out;
        }
        
        @keyframes slideInUp {
            from {
                opacity: 0;
                transform: translateY(30px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
        
        .field-error {
            color: #e53e3e;
            font-size: 0.8rem;
            margin-top: 0.25rem;
            display: flex;
            align-items: center;
        }
        
        .field-error::before {
            content: '⚠';
            margin-right: 0.25rem;
        }
        
        .form-control.error {
            border-color: #e53e3e;
            box-shadow: 0 0 0 3px rgba(229, 62, 62, 0.1);
        }
        
        .notification {
            position: fixed;
            top: 20px;
            right: 20px;
            background: white;
            border-radius: 8px;
            padding: 1rem;
            box-shadow: 0 4px 20px rgba(0,0,0,0.15);
            transform: translateX(400px);
            transition: all 0.3s ease;
            z-index: 9999;
            max-width: 300px;
        }
        
        .notification.show {
            transform: translateX(0);
        }
        
        .notification-success { border-left: 4px solid #4caf50; }
        .notification-error { border-left: 4px solid #f44336; }
        .notification-warning { border-left: 4px solid #ff9800; }
        .notification-info { border-left: 4px solid #2196f3; }
        
        .alert-close {
            position: absolute;
            top: 0.5rem;
            right: 0.75rem;
            background: none;
            border: none;
            font-size: 1.25rem;
            cursor: pointer;
            opacity: 0.6;
            transition: opacity 0.2s;
        }
        
        .alert-close:hover {
            opacity: 1;
        }
    `;
    document.head.appendChild(style);
}