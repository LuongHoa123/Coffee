// ===== VERIFY OTP PAGE FUNCTIONALITY =====

class VerifyOTPManager {
    constructor() {
        this.form = document.querySelector('#verifyOTPForm');
        this.otpInputs = document.querySelectorAll('.otp-digit');
        this.hiddenOtpInput = document.querySelector('#otpCode');
        this.submitButton = document.querySelector('#submitButton');
        this.resendBtn = document.querySelector('#resendBtn');
        this.countdownElement = document.querySelector('#countdownTime');
        this.resendCountdownElement = document.querySelector('#resendCountdown');
        
        // Timers
        this.otpTimer = null;
        this.resendTimer = null;
        this.isLoading = false;
        
        // Get initial data from server
        this.remainingTime = window.otpData ? window.otpData.remainingTime * 60 : 600; // Convert to seconds
        this.resendCooldown = 60; // 60 seconds cooldown for resend
        
        this.init();
    }
    
    init() {
        this.setupEventListeners();
        this.setupOTPInputs();
        this.setupAnimations();
        this.startCountdown();
        this.startResendCooldown();
        this.prefillOTP();
    }
    
    setupEventListeners() {
        // Form submission
        if (this.form) {
            this.form.addEventListener('submit', (e) => this.handleSubmit(e));
        }
        
        // Resend OTP
        if (this.resendBtn) {
            this.resendBtn.addEventListener('click', (e) => this.handleResendOTP(e));
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
    
    setupOTPInputs() {
        this.otpInputs.forEach((input, index) => {
            input.addEventListener('input', (e) => this.handleOTPInput(e, index));
            input.addEventListener('keydown', (e) => this.handleOTPKeydown(e, index));
            input.addEventListener('paste', (e) => this.handleOTPPaste(e, index));
            input.addEventListener('focus', (e) => this.handleOTPFocus(e, index));
        });
    }
    
    handleOTPInput(e, index) {
        const input = e.target;
        const value = input.value;
        
        // Only allow digits
        if (!/^\d$/.test(value)) {
            input.value = '';
            return;
        }
        
        // Move to next input
        if (value && index < this.otpInputs.length - 1) {
            this.otpInputs[index + 1].focus();
        }
        
        // Update hidden input
        this.updateHiddenOTP();
        
        // Auto-submit if all fields filled
        if (this.isOTPComplete()) {
            setTimeout(() => this.handleSubmit(), 500);
        }
    }
    
    handleOTPKeydown(e, index) {
        const input = e.target;
        
        // Handle backspace
        if (e.key === 'Backspace') {
            if (!input.value && index > 0) {
                this.otpInputs[index - 1].focus();
                this.otpInputs[index - 1].value = '';
                this.updateHiddenOTP();
            } else if (input.value) {
                input.value = '';
                this.updateHiddenOTP();
            }
        }
        
        // Handle arrow keys
        if (e.key === 'ArrowLeft' && index > 0) {
            e.preventDefault();
            this.otpInputs[index - 1].focus();
        }
        
        if (e.key === 'ArrowRight' && index < this.otpInputs.length - 1) {
            e.preventDefault();
            this.otpInputs[index + 1].focus();
        }
        
        // Prevent non-digit input
        if (!/^\d$/.test(e.key) && !['Backspace', 'ArrowLeft', 'ArrowRight', 'Tab', 'Delete'].includes(e.key)) {
            e.preventDefault();
        }
    }
    
    handleOTPPaste(e, index) {
        e.preventDefault();
        const pasteData = e.clipboardData.getData('text');
        const digits = pasteData.replace(/\D/g, '').slice(0, 6);
        
        if (digits.length > 0) {
            digits.split('').forEach((digit, i) => {
                if (i < this.otpInputs.length) {
                    this.otpInputs[i].value = digit;
                }
            });
            
            // Focus on the next empty input or last input
            const nextIndex = Math.min(digits.length, this.otpInputs.length - 1);
            this.otpInputs[nextIndex].focus();
            
            this.updateHiddenOTP();
            
            // Auto-submit if complete
            if (this.isOTPComplete()) {
                setTimeout(() => this.handleSubmit(), 500);
            }
        }
    }
    
    handleOTPFocus(e, index) {
        e.target.select();
    }
    
    updateHiddenOTP() {
        const otpValue = Array.from(this.otpInputs).map(input => input.value).join('');
        this.hiddenOtpInput.value = otpValue;
    }
    
    isOTPComplete() {
        return Array.from(this.otpInputs).every(input => input.value !== '');
    }
    
    prefillOTP() {
        const initialOtp = window.otpData?.initialOtpCode || '';
        if (initialOtp && initialOtp.length <= 6) {
            initialOtp.split('').forEach((digit, index) => {
                if (index < this.otpInputs.length && /^\d$/.test(digit)) {
                    this.otpInputs[index].value = digit;
                }
            });
            this.updateHiddenOTP();
        }
    }
    
    handleSubmit(e) {
        if (e) e.preventDefault();
        
        // Prevent double submission
        if (this.isLoading) return;
        
        // Validate OTP
        if (!this.isOTPComplete()) {
            this.showNotification('Vui lòng nhập đầy đủ 6 chữ số mã xác thực', 'error');
            this.shakeOTPInputs();
            return;
        }
        
        // Show loading state
        this.setLoadingState(true);
        
        // Add success animation before submit
        this.showSuccessAnimation();
        
        setTimeout(() => {
            this.form.submit();
        }, 800);
    }
    
    handleResendOTP(e) {
        e.preventDefault();
        
        if (this.resendBtn.disabled) return;
        
        // Show loading state for resend button
        const originalText = this.resendBtn.innerHTML;
        this.resendBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Đang gửi...';
        this.resendBtn.disabled = true;
        
        // Redirect to resend endpoint
        setTimeout(() => {
            window.location.href = `${window.location.origin}${window.location.pathname.replace('/verify-otp', '/forgot-password')}?action=resend`;
        }, 1000);
    }
    
    startCountdown() {
        this.updateCountdownDisplay();
        
        this.otpTimer = setInterval(() => {
            this.remainingTime--;
            this.updateCountdownDisplay();
            
            if (this.remainingTime <= 0) {
                this.handleOTPExpired();
            }
        }, 1000);
    }
    
    updateCountdownDisplay() {
        const minutes = Math.floor(this.remainingTime / 60);
        const seconds = this.remainingTime % 60;
        const timeString = `${minutes}:${seconds.toString().padStart(2, '0')}`;
        
        if (this.countdownElement) {
            this.countdownElement.textContent = timeString;
            
            // Add warning class when time is low
            if (this.remainingTime <= 60) {
                this.countdownElement.classList.add('warning');
            }
            
            if (this.remainingTime <= 30) {
                this.countdownElement.classList.add('danger');
            }
        }
    }
    
    handleOTPExpired() {
        clearInterval(this.otpTimer);
        
        // Disable form
        this.otpInputs.forEach(input => input.disabled = true);
        this.submitButton.disabled = true;
        
        // Show expiration message
        this.showNotification('Mã xác thực đã hết hạn. Vui lòng yêu cầu mã mới.', 'error');
        
        // Auto redirect to forgot password page
        setTimeout(() => {
            window.location.href = `${window.location.origin}${window.location.pathname.replace('/verify-otp', '/forgot-password')}`;
        }, 3000);
    }
    
    startResendCooldown() {
        this.updateResendButton();
        
        this.resendTimer = setInterval(() => {
            this.resendCooldown--;
            this.updateResendButton();
            
            if (this.resendCooldown <= 0) {
                clearInterval(this.resendTimer);
                this.resendBtn.disabled = false;
                this.resendBtn.innerHTML = '<i class="fas fa-redo-alt me-2"></i>Gửi lại mã';
            }
        }, 1000);
    }
    
    updateResendButton() {
        if (this.resendCooldown > 0) {
            this.resendCountdownElement.textContent = this.resendCooldown;
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
    
    shakeOTPInputs() {
        this.otpInputs.forEach(input => {
            input.style.animation = 'shake 0.5s ease-in-out';
            input.classList.add('error');
            setTimeout(() => {
                input.style.animation = '';
                input.classList.remove('error');
            }, 500);
        });
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
        
        // Hide notification after 4 seconds
        setTimeout(() => {
            notification.classList.remove('show');
            setTimeout(() => notification.remove(), 300);
        }, 4000);
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
        
        // Ensure OTP inputs are visible by default (no entrance animation that hides them)
        this.otpInputs.forEach((input) => {
            input.style.opacity = '';
            input.style.transform = '';
            input.style.transition = '';
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

// Additional CSS for OTP inputs and countdown
const otpStyles = `
    .otp-input-container {
        text-align: center;
    }
    
    .otp-inputs {
        display: flex;
        gap: 12px;
        justify-content: center;
        margin-bottom: 20px;
    }
    
    .otp-digit {
        width: 50px;
        height: 56px;
        text-align: center;
        font-size: 24px;
        font-weight: 700;
        border: 2px solid var(--neutral-200);
        border-radius: var(--radius-lg);
        background: rgba(255, 255, 255, 0.8);
        color: var(--neutral-800);
        transition: all var(--duration-300) var(--easing-ease-out);
        outline: none;
        backdrop-filter: blur(20px);
    }
    
    .otp-digit:focus {
        border-color: var(--accent-500);
        box-shadow: 0 0 0 0.2rem rgba(139, 69, 19, 0.15);
        background: rgba(255, 255, 255, 0.95);
        transform: scale(1.05);
    }
    
    .otp-digit.error {
        border-color: var(--danger-500);
        animation: shake 0.5s ease-in-out;
    }
    
    .countdown-container {
        text-align: center;
        margin: 20px 0;
    }
    
    .countdown-circle {
        width: 80px;
        height: 80px;
        border-radius: 50%;
        background: linear-gradient(135deg, var(--accent-500) 0%, var(--accent-600) 100%);
        display: flex;
        align-items: center;
        justify-content: center;
        margin: 0 auto;
        box-shadow: var(--shadow-lg);
        position: relative;
    }
    
    .countdown-text {
        text-align: center;
        color: white;
    }
    
    .countdown-number {
        display: block;
        font-size: 14px;
        font-weight: 700;
        line-height: 1;
    }
    
    .countdown-number.warning {
        color: #f59e0b;
    }
    
    .countdown-number.danger {
        color: #ef4444;
        animation: pulse 1s infinite;
    }
    
    .countdown-label {
        display: block;
        font-size: 10px;
        font-weight: 500;
        opacity: 0.9;
    }
    
    .resend-container {
        text-align: center;
        margin: 20px 0;
    }
    
    .resend-text {
        color: var(--neutral-600);
        font-size: 14px;
        margin-bottom: 10px;
    }
    
    .resend-btn {
        background: none;
        border: none;
        color: var(--accent-500);
        font-weight: 600;
        cursor: pointer;
        transition: all var(--duration-300) var(--easing-ease-out);
        padding: 8px 16px;
        border-radius: var(--radius-md);
        font-size: 14px;
    }
    
    .resend-btn:hover:not(:disabled) {
        background: rgba(139, 69, 19, 0.1);
        transform: translateY(-1px);
    }
    
    .resend-btn:disabled {
        opacity: 0.5;
        cursor: not-allowed;
    }
    
    @keyframes pulse {
        0%, 100% { transform: scale(1); }
        50% { transform: scale(1.1); }
    }
    
    @keyframes shake {
        0%, 100% { transform: translateX(0); }
        10%, 30%, 50%, 70%, 90% { transform: translateX(-3px); }
        20%, 40%, 60%, 80% { transform: translateX(3px); }
    }
    
    @media (max-width: 768px) {
        .otp-digit {
            width: 45px;
            height: 50px;
            font-size: 20px;
        }
        
        .otp-inputs {
            gap: 8px;
        }
        
        .countdown-circle {
            width: 70px;
            height: 70px;
        }
    }
`;

// Add styles to document
const styleSheet = document.createElement('style');
styleSheet.textContent = otpStyles;
document.head.appendChild(styleSheet);

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    new VerifyOTPManager();
});