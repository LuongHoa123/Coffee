// ===== FORGOT PASSWORD PAGE FUNCTIONALITY =====

class ForgotPasswordManager {
    constructor() {
        this.form = document.querySelector('#forgotPasswordForm');
        this.emailInput = document.querySelector('#email');
        this.submitButton = document.querySelector('#submitButton');
        
        // Animation states
        this.isLoading = false;
        
        this.init();
    }
    
    init() {
        this.setupEventListeners();
        this.setupFormValidation();
        this.setupAnimations();
        this.initParallax();
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
        
        // Auto-hide alerts after 5 seconds
        setTimeout(() => {
            const alerts = document.querySelectorAll('.alert');
            alerts.forEach(alert => {
                if (alert.classList.contains('alert-success')) {
                    this.fadeOut(alert);
                }
            });
        }, 5000);
    }
    
    handleSubmit(e) {
        e.preventDefault();
        
        // Prevent double submission
        if (this.isLoading) return;
        
        // Validate email
        const isEmailValid = this.validateEmail();
        
        if (!isEmailValid) {
            this.showNotification('Vui lòng nhập địa chỉ email hợp lệ', 'error');
            this.shakeForm();
            return;
        }
        
        // Show loading state
        this.setLoadingState(true);
        
        // Show validation message
        this.showNotification('Đang kiểm tra email...', 'info');
        
        // Add success animation before submit
        setTimeout(() => {
            this.form.submit();
        }, 800);
    }
    
    validateEmail() {
        const email = this.emailInput.value.trim();
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        
        this.clearEmailError();
        
        if (!email) {
            this.showEmailError('Email không được để trống');
            return false;
        }
        
        if (!emailRegex.test(email)) {
            this.showEmailError('Định dạng email không hợp lệ');
            return false;
        }
        
        return true;
    }
    
    showEmailError(message) {
        this.emailInput.classList.add('error');
        
        // Remove existing error message
        const existingError = this.emailInput.parentNode.parentNode.querySelector('.error-message');
        if (existingError) {
            existingError.remove();
        }
        
        // Add new error message
        const errorDiv = document.createElement('div');
        errorDiv.className = 'error-message mt-2';
        errorDiv.innerHTML = `<i class="fas fa-exclamation-triangle me-1"></i>${message}`;
        this.emailInput.parentNode.parentNode.appendChild(errorDiv);
    }
    
    clearEmailError() {
        this.emailInput.classList.remove('error');
        const errorMessage = this.emailInput.parentNode.parentNode.querySelector('.error-message');
        if (errorMessage) {
            errorMessage.remove();
        }
    }
    
    setLoadingState(loading) {
        this.isLoading = loading;
        const btnText = this.submitButton.querySelector('.btn-text');
        const btnLoading = this.submitButton.querySelector('.btn-loading');
        
        if (loading) {
            btnText.classList.add('d-none');
            btnLoading.classList.remove('d-none');
            this.submitButton.disabled = true;
            this.submitButton.classList.add('loading');
        } else {
            btnText.classList.remove('d-none');
            btnLoading.classList.add('d-none');
            this.submitButton.disabled = false;
            this.submitButton.classList.remove('loading');
        }
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
    
    showNotification(message, type) {
        // Remove existing notification
        const existingNotification = document.querySelector('.notification-toast');
        if (existingNotification) {
            existingNotification.remove();
        }
        
        // Create notification
        const notification = document.createElement('div');
        notification.className = `notification-toast ${type}`;
        
        let icon = 'fa-info-circle';
        if (type === 'success') icon = 'fa-check-circle';
        else if (type === 'error') icon = 'fa-exclamation-circle';
        else if (type === 'warning') icon = 'fa-exclamation-triangle';
        
        notification.innerHTML = `
            <div class="notification-content">
                <i class="fas ${icon}"></i>
                <span>${message}</span>
            </div>
        `;
        
        document.body.appendChild(notification);
        
        // Show notification
        setTimeout(() => notification.classList.add('show'), 100);
        
        // Hide notification after 3 seconds (or 5 for error messages)
        const hideDelay = type === 'error' ? 5000 : 3000;
        setTimeout(() => {
            notification.classList.remove('show');
            setTimeout(() => notification.remove(), 300);
        }, hideDelay);
    }
    
    setupFormValidation() {
        // Real-time form styling updates
        const inputs = document.querySelectorAll('.form-control');
        inputs.forEach(input => {
            input.addEventListener('focus', () => this.updateLabelState(input));
            input.addEventListener('blur', () => this.updateLabelState(input));
            input.addEventListener('input', () => this.updateLabelState(input));
            
            // Initial state
            this.updateLabelState(input);
        });
    }
    
    updateLabelState(input) {
        const formGroup = input.closest('.form-floating');
        if (!formGroup) return;
        
        const label = formGroup.querySelector('.form-label');
        if (!label) return;
        
        if (input.value.length > 0 || document.activeElement === input) {
            label.classList.add('active');
        } else {
            label.classList.remove('active');
        }
    }
    
    setupAnimations() {
        // Animate elements on page load
        const animatedElements = document.querySelectorAll('.hero-content, .login-card');
        
        animatedElements.forEach((element, index) => {
            element.style.opacity = '0';
            element.style.transform = 'translateY(30px)';
            
            setTimeout(() => {
                element.style.transition = 'all 0.8s cubic-bezier(0.4, 0.0, 0.2, 1)';
                element.style.opacity = '1';
                element.style.transform = 'translateY(0)';
            }, index * 200);
        });
        
        // Feature cards animation
        const featureCards = document.querySelectorAll('.feature-card');
        featureCards.forEach((card, index) => {
            card.style.opacity = '0';
            card.style.transform = 'translateX(-30px)';
            
            setTimeout(() => {
                card.style.transition = 'all 0.6s cubic-bezier(0.4, 0.0, 0.2, 1)';
                card.style.opacity = '1';
                card.style.transform = 'translateX(0)';
            }, 800 + (index * 150));
        });
    }
    
    initParallax() {
        // Simple parallax effect for hero content
        window.addEventListener('scroll', () => {
            const scrolled = window.pageYOffset;
            const heroContent = document.querySelector('.hero-content');
            
            if (heroContent) {
                const speed = scrolled * 0.5;
                heroContent.style.transform = `translateY(${speed}px)`;
            }
        });
    }
    
    fadeOut(element) {
        element.style.transition = 'opacity 0.5s ease-out';
        element.style.opacity = '0';
        
        setTimeout(() => {
            if (element.parentNode) {
                element.parentNode.removeChild(element);
            }
        }, 500);
    }
}

// Additional CSS for notifications (will be added dynamically)
const notificationStyles = `
    .notification-toast {
        position: fixed;
        top: 20px;
        right: 20px;
        z-index: 9999;
        padding: 16px 24px;
        border-radius: 12px;
        color: white;
        font-weight: 500;
        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
        backdrop-filter: blur(16px);
        transform: translateX(400px);
        transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
        max-width: 400px;
    }
    
    .notification-toast.success {
        background: linear-gradient(135deg, #059669 0%, #10b981 100%);
    }
    
    .notification-toast.error {
        background: linear-gradient(135deg, #dc2626 0%, #ef4444 100%);
    }
    
    .notification-toast.show {
        transform: translateX(0);
    }
    
    .notification-content {
        display: flex;
        align-items: center;
        gap: 12px;
    }
    
    .notification-content i {
        font-size: 18px;
    }
    
    .error-message {
        color: var(--danger-500);
        font-size: 0.875rem;
        font-weight: 500;
        display: flex;
        align-items: center;
        gap: 6px;
    }
    
    .form-control.error {
        border-color: var(--danger-500) !important;
        box-shadow: 0 0 0 0.2rem rgba(220, 38, 38, 0.15) !important;
    }
    
    .btn.loading {
        pointer-events: none;
        opacity: 0.8;
    }
    
    @keyframes shake {
        0%, 100% { transform: translateX(0); }
        10%, 30%, 50%, 70%, 90% { transform: translateX(-5px); }
        20%, 40%, 60%, 80% { transform: translateX(5px); }
    }
    
    @media (max-width: 768px) {
        .notification-toast {
            top: 10px;
            right: 10px;
            left: 10px;
            max-width: none;
            transform: translateY(-100px);
        }
        
        .notification-toast.show {
            transform: translateY(0);
        }
    }
`;

// Add styles to document
const styleSheet = document.createElement('style');
styleSheet.textContent = notificationStyles;
document.head.appendChild(styleSheet);

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    new ForgotPasswordManager();
});