// ===== RESET PASSWORD PAGE FUNCTIONALITY =====

class ResetPasswordManager {
    constructor() {
        this.form = document.querySelector('#resetPasswordForm');
        this.newPasswordInput = document.querySelector('#newPassword');
        this.confirmPasswordInput = document.querySelector('#confirmPassword');
        this.submitButton = document.querySelector('#submitButton');
        this.showPasswordToggle = document.querySelector('#showPassword');
        this.passwordStrength = document.querySelector('#passwordStrength');
        
        // Password validation elements
        this.strengthBar = document.querySelector('.strength-progress');
        this.strengthText = document.querySelector('.strength-text');
        this.requirements = document.querySelectorAll('.requirement');
        
        // Animation states
        this.isLoading = false;
        
        this.init();
    }
    
    init() {
        this.setupEventListeners();
        this.setupFormValidation();
        this.setupAnimations();
        
        // Show password strength indicator when user starts typing
        if (this.passwordStrength) {
            this.passwordStrength.style.opacity = '0';
        }
    }
    
    setupEventListeners() {
        // Form submission
        if (this.form) {
            this.form.addEventListener('submit', (e) => this.handleSubmit(e));
        }
        
        // Password input events
        if (this.newPasswordInput) {
            this.newPasswordInput.addEventListener('input', () => {
                this.showPasswordStrength();
                this.checkPasswordStrength();
                this.updateRequirements(this.newPasswordInput.value);
                this.clearPasswordError();
            });
            
            this.newPasswordInput.addEventListener('focus', () => {
                this.showPasswordStrength();
            });
        }
        
        if (this.confirmPasswordInput) {
            this.confirmPasswordInput.addEventListener('input', () => {
                this.validatePasswordConfirmation();
                this.clearConfirmPasswordError();
            });
        }
        
        // Show/hide password toggle
        if (this.showPasswordToggle) {
            this.showPasswordToggle.addEventListener('change', (e) => {
                this.togglePasswordVisibility(e.target.checked);
            });
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
    
    setupFormValidation() {
        // Initialize password strength indicator
        if (this.passwordStrength) {
            this.passwordStrength.classList.remove('show');
        }
        
        // Initialize all requirements as not met
        if (this.requirements) {
            this.requirements.forEach(req => {
                req.classList.add('not-met');
                req.classList.remove('met');
            });
        }
    }
    
    setupAnimations() {
        // Add CSS for animations if not present
        if (!document.querySelector('#reset-password-animations')) {
            const style = document.createElement('style');
            style.id = 'reset-password-animations';
            style.textContent = `
                .password-strength {
                    transition: all 0.3s ease;
                    transform: translateY(-10px);
                    opacity: 0;
                }
                .password-strength.show {
                    transform: translateY(0);
                    opacity: 1;
                }
                .requirement {
                    transition: all 0.3s ease;
                }
                .requirement.met {
                    color: var(--success, #28a745);
                }
                .requirement.met i {
                    color: var(--success, #28a745);
                }
                .requirement.not-met {
                    color: var(--text-muted, #6c757d);
                }
                @keyframes shake {
                    0%, 100% { transform: translateX(0); }
                    10%, 30%, 50%, 70%, 90% { transform: translateX(-5px); }
                    20%, 40%, 60%, 80% { transform: translateX(5px); }
                }
            `;
            document.head.appendChild(style);
        }
    }
    
    handleSubmit(e) {
        e.preventDefault();
        
        // Prevent double submission
        if (this.isLoading) return;
        
        // Validate all fields
        const isPasswordValid = this.validatePassword();
        const isConfirmValid = this.validatePasswordConfirmation();
        
        if (!isPasswordValid || !isConfirmValid) {
            this.showNotification('Vui lòng kiểm tra lại thông tin mật khẩu', 'error');
            this.shakeForm();
            return;
        }
        
        // Check password strength
        const strength = this.calculatePasswordStrength(this.newPasswordInput.value);
        if (strength < 3) {
            this.showNotification('Mật khẩu cần mạnh hơn để đảm bảo bảo mật', 'error');
            this.shakeForm();
            return;
        }
        
        // Show loading state with animation
        this.setLoadingState(true);
        
        // Add success animation before submit
        this.showSuccessAnimation();
        
        // Submit form with delay for animation
        setTimeout(() => {
            this.form.submit();
        }, 800);
    }
    
    validatePassword() {
        const password = this.newPasswordInput.value;
        
        this.clearPasswordError();
        
        if (!password) {
            this.showPasswordError('Mật khẩu mới không được để trống');
            return false;
        }
        
        const requirements = this.getPasswordRequirements(password);
        const failedRequirements = Object.values(requirements).filter(req => !req);
        
        if (failedRequirements.length > 0) {
            this.showPasswordError('Mật khẩu chưa đáp ứng đủ yêu cầu bảo mật');
            return false;
        }
        
        return true;
    }
    
    validatePasswordConfirmation() {
        const password = this.newPasswordInput.value;
        const confirmPassword = this.confirmPasswordInput.value;
        
        this.clearConfirmPasswordError();
        
        if (!confirmPassword) {
            this.showConfirmPasswordError('Vui lòng xác nhận mật khẩu');
            return false;
        }
        
        if (password !== confirmPassword) {
            this.showConfirmPasswordError('Mật khẩu xác nhận không khớp');
            return false;
        }
        
        return true;
    }
    
    getPasswordRequirements(password) {
        return {
            length: password.length >= 8,
            lowercase: /[a-z]/.test(password),
            uppercase: /[A-Z]/.test(password),
            number: /\d/.test(password),
            special: /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password)
        };
    }
    
    checkPasswordStrength() {
        const password = this.newPasswordInput.value;
        const strength = this.calculatePasswordStrength(password);
        
        this.updatePasswordStrengthUI(strength);
        this.updateRequirements(password);
    }
    
    calculatePasswordStrength(password) {
        let strength = 0;
        const requirements = this.getPasswordRequirements(password);
        
        // Each requirement adds 1 point
        Object.values(requirements).forEach(met => {
            if (met) strength++;
        });
        
        // Bonus for length
        if (password.length >= 12) strength += 1;
        if (password.length >= 16) strength += 1;
        
        return Math.min(strength, 5);
    }
    
    updatePasswordStrengthUI(strength) {
        if (!this.strengthBar || !this.strengthText) return;
        
        const percentage = (strength / 5) * 100;
        const colors = ['#ef4444', '#f59e0b', '#eab308', '#22c55e', '#10b981'];
        const labels = ['Rất yếu', 'Yếu', 'Trung bình', 'Mạnh', 'Rất mạnh'];
        
        this.strengthBar.style.width = `${percentage}%`;
        this.strengthBar.style.backgroundColor = colors[strength - 1] || '#e5e7eb';
        this.strengthText.textContent = strength > 0 ? labels[strength - 1] : 'Độ mạnh mật khẩu';
        
        // Update password strength container class
        if (this.passwordStrength) {
            this.passwordStrength.className = 'password-strength show';
            if (strength > 0) {
                this.passwordStrength.classList.add(`strength-${['weak', 'fair', 'good', 'strong', 'strong'][strength - 1]}`);
            }
        }
    }
    
    showPasswordStrength() {
        if (this.passwordStrength) {
            this.passwordStrength.style.opacity = '1';
            this.passwordStrength.classList.add('show');
        }
    }
    
    updateRequirements(password) {
        const requirements = this.getPasswordRequirements(password);
        
        this.requirements.forEach(req => {
            const type = req.dataset.requirement;
            const isMet = requirements[type];
            
            if (isMet) {
                req.classList.add('met');
                req.classList.remove('not-met');
            } else {
                req.classList.remove('met');
                req.classList.add('not-met');
            }
        });
    }
    
    togglePasswordVisibility(show) {
        const type = show ? 'text' : 'password';
        this.newPasswordInput.type = type;
        this.confirmPasswordInput.type = type;
    }
    
    showPasswordError(message) {
        this.newPasswordInput.classList.add('error');
        this.showFieldError(this.newPasswordInput, message);
    }
    
    showConfirmPasswordError(message) {
        this.confirmPasswordInput.classList.add('error');
        this.showFieldError(this.confirmPasswordInput, message);
    }
    
    showFieldError(input, message) {
        // Remove existing error message
        const existingError = input.parentNode.parentNode.querySelector('.error-message');
        if (existingError) {
            existingError.remove();
        }
        
        // Add new error message
        const errorDiv = document.createElement('div');
        errorDiv.className = 'error-message mt-2';
        errorDiv.innerHTML = `<i class="fas fa-exclamation-triangle me-1"></i>${message}`;
        input.parentNode.parentNode.appendChild(errorDiv);
    }
    
    clearPasswordError() {
        this.newPasswordInput.classList.remove('error');
        this.clearFieldError(this.newPasswordInput);
    }
    
    clearConfirmPasswordError() {
        this.confirmPasswordInput.classList.remove('error');
        this.clearFieldError(this.confirmPasswordInput);
    }
    
    clearFieldError(input) {
        const errorMessage = input.parentNode.parentNode.querySelector('.error-message');
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
    
    showSuccessAnimation() {
        const successAnimation = document.querySelector('.success-animation');
        if (successAnimation) {
            successAnimation.classList.remove('d-none');
            setTimeout(() => {
                successAnimation.classList.add('d-none');
            }, 2000);
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
        notification.innerHTML = `
            <div class="notification-content">
                <i class="fas ${type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle'}"></i>
                <span>${message}</span>
            </div>
        `;
        
        document.body.appendChild(notification);
        
        // Show notification
        setTimeout(() => notification.classList.add('show'), 100);
        
        // Hide notification after 3 seconds
        setTimeout(() => {
            notification.classList.remove('show');
            setTimeout(() => notification.remove(), 300);
        }, 3000);
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

// Additional CSS for password strength and requirements
const resetPasswordStyles = `
    .password-strength {
        margin-top: 8px;
    }
    
    .strength-bar {
        width: 100%;
        height: 4px;
        background-color: var(--neutral-200);
        border-radius: 2px;
        overflow: hidden;
        margin-bottom: 6px;
    }
    
    .strength-progress {
        height: 100%;
        width: 0%;
        border-radius: 2px;
        transition: all 0.3s ease-out;
        background-color: #e5e7eb;
    }
    
    .strength-progress.strength-animation {
        transform: scaleX(1.02);
        transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
    }
    
    .strength-text {
        font-size: 12px;
        font-weight: 500;
        color: var(--neutral-600);
    }
    
    .password-requirements {
        background: rgba(139, 69, 19, 0.05);
        border-radius: var(--radius-lg);
        padding: 16px;
        border-left: 4px solid var(--accent-500);
    }
    
    .requirements-title {
        font-size: 14px;
        font-weight: 600;
        color: var(--neutral-800);
        margin-bottom: 12px;
    }
    
    .requirements-list {
        list-style: none;
        padding: 0;
        margin: 0;
        display: grid;
        gap: 8px;
    }
    
    .requirement {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 13px;
        color: var(--neutral-600);
        transition: all 0.3s ease-out;
    }
    
    .requirement i {
        width: 16px;
        height: 16px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 10px;
        background: var(--neutral-300);
        color: white;
        transition: all 0.3s ease-out;
    }
    
    .requirement.met i {
        background: var(--success-500);
        color: white;
        transform: scale(1.1);
    }
    
    .requirement.met {
        color: var(--success-600);
        font-weight: 500;
    }
    
    .requirement.not-met i {
        background: var(--danger-400);
        color: white;
    }
    
    .requirement.not-met {
        color: var(--danger-600);
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
        .password-requirements {
            padding: 12px;
        }
        
        .requirements-list {
            font-size: 12px;
        }
        
        .requirement i {
            width: 14px;
            height: 14px;
            font-size: 8px;
        }
    }
`;

// Add styles to document
const styleSheet = document.createElement('style');
styleSheet.textContent = resetPasswordStyles;
document.head.appendChild(styleSheet);

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    new ResetPasswordManager();
});