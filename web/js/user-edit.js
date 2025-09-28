/**
 * User Edit Form JavaScript - CoffeeLux HR Dashboard
 * Handles password visibility toggle and form validation
 */

/**
 * Toggle password visibility
 * @param {string} inputId - The ID of the password input field
 */
function togglePassword(inputId) {
    const input = document.getElementById(inputId);
    const icon = document.getElementById(inputId + 'ToggleIcon');
    
    if (!input || !icon) {
        console.warn('Password input or icon not found:', inputId);
        return;
    }
    
    if (input.type === 'password') {
        input.type = 'text';
        icon.classList.remove('fa-eye');
        icon.classList.add('fa-eye-slash');
    } else {
        input.type = 'password';
        icon.classList.remove('fa-eye-slash');
        icon.classList.add('fa-eye');
    }
}

/**
 * Check password strength
 * @param {string} password - The password to check
 * @returns {object} - Object containing strength level and score
 */
function checkPasswordStrength(password) {
    let score = 0;
    let feedback = [];
    
    // Length check
    if (password.length >= 8) score += 1;
    else feedback.push('Ít nhất 8 ký tự');
    
    // Uppercase check
    if (/[A-Z]/.test(password)) score += 1;
    else feedback.push('Có chữ hoa');
    
    // Lowercase check
    if (/[a-z]/.test(password)) score += 1;
    else feedback.push('Có chữ thường');
    
    // Number check
    if (/\d/.test(password)) score += 1;
    else feedback.push('Có số');
    
    // Special character check
    if (/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password)) score += 1;
    else feedback.push('Có ký tự đặc biệt');
    
    const levels = ['very-weak', 'weak', 'medium', 'strong', 'very-strong'];
    const level = levels[Math.min(score, 4)];
    
    return { level, score, feedback };
}

/**
 * Update password strength indicator
 * @param {string} password - The password to evaluate
 */
function updatePasswordStrength(password) {
    const strengthDiv = document.getElementById('passwordStrength');
    
    if (!strengthDiv) return;
    
    if (!password || password.length === 0) {
        strengthDiv.className = 'password-strength';
        strengthDiv.style.width = '0%';
        return;
    }
    
    const strength = checkPasswordStrength(password);
    strengthDiv.className = `password-strength ${strength.level}`;
    
    // Add tooltip with feedback
    strengthDiv.title = strength.feedback.length > 0 ? 
        'Cần: ' + strength.feedback.join(', ') : 
        'Mật khẩu mạnh';
}

/**
 * Validate form before submission
 * @param {Event} event - The form submit event
 */
function validateForm(event) {
    const form = event.target;
    const fullName = form.fullName.value.trim();
    const email = form.email.value.trim();
    const newPassword = form.newPassword ? form.newPassword.value : '';
    const confirmPassword = form.confirmPassword ? form.confirmPassword.value : '';
    
    let isValid = true;
    let errorMessage = '';
    
    // Validate full name
    if (!fullName) {
        errorMessage = 'Họ tên không được để trống';
        isValid = false;
    }
    
    // Validate email
    else if (!email) {
        errorMessage = 'Email không được để trống';
        isValid = false;
    }
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
        errorMessage = 'Email không hợp lệ';
        isValid = false;
    }
    
    // Validate password if provided
    else if (newPassword && newPassword !== confirmPassword) {
        errorMessage = 'Xác nhận mật khẩu không khớp';
        isValid = false;
    }
    
    if (!isValid) {
        alert(errorMessage);
        event.preventDefault();
        return false;
    }
    
    return true;
}

/**
 * Initialize form when DOM is loaded
 */
document.addEventListener('DOMContentLoaded', function() {
    // Add password strength checker
    const passwordInput = document.getElementById('password');
    if (passwordInput) {
        passwordInput.addEventListener('input', function() {
            updatePasswordStrength(this.value);
        });
    }
    
    // Add form validation
    const editForm = document.querySelector('form.user-form');
    if (editForm) {
        editForm.addEventListener('submit', validateForm);
    }
    
    // Add confirmation for form reset
    const resetBtn = document.querySelector('button[type="reset"]');
    if (resetBtn) {
        resetBtn.addEventListener('click', function(event) {
            if (!confirm('Bạn có chắc muốn hủy bỏ tất cả thay đổi?')) {
                event.preventDefault();
            }
        });
    }
    
    console.log('User edit form initialized');
});