/**
 * User Management JavaScript
 * Handles frontend interactions for user management functionality
 */

// Global variables
let statusChangeUserId = null;
let statusChangeAction = null;

// DOM Content Loaded Event
document.addEventListener('DOMContentLoaded', function() {
    initializeUserManagement();
});

/**
 * Initialize user management functionality
 */
function initializeUserManagement() {
    // Initialize tooltips
    initializeTooltips();
    
    // Initialize search functionality
    initializeSearch();
    
    // Initialize table interactions
    initializeTableInteractions();
    
    // Initialize modal events
    initializeModalEvents();
    
    // Initialize auto-hide alerts
    initializeAlerts();
}

/**
 * Initialize tooltips for action buttons
 */
function initializeTooltips() {
    const buttons = document.querySelectorAll('[title]');
    buttons.forEach(button => {
        button.addEventListener('mouseenter', showTooltip);
        button.addEventListener('mouseleave', hideTooltip);
    });
}

/**
 * Initialize search functionality
 */
function initializeSearch() {
    const searchForm = document.querySelector('.user-search-form');
    if (!searchForm) return;
    
    // Auto-submit on enter key
    const searchInput = searchForm.querySelector('input[name="searchTerm"]');
    if (searchInput) {
        searchInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                searchForm.submit();
            }
        });
    }
    
    // Real-time search (with debounce)
    let searchTimeout;
    if (searchInput) {
        searchInput.addEventListener('input', function() {
            clearTimeout(searchTimeout);
            searchTimeout = setTimeout(() => {
                if (this.value.length >= 3 || this.value.length === 0) {
                    // Implement real-time search if needed
                    // performRealTimeSearch(this.value);
                }
            }, 300);
        });
    }
}

/**
 * Initialize table interactions
 */
function initializeTableInteractions() {
    // Row hover effects
    const tableRows = document.querySelectorAll('.user-table tbody tr');
    tableRows.forEach(row => {
        row.addEventListener('mouseenter', function() {
            this.style.backgroundColor = '#f8f9fa';
        });
        
        row.addEventListener('mouseleave', function() {
            this.style.backgroundColor = '';
        });
    });
    
    // Make rows clickable (optional)
    tableRows.forEach(row => {
        row.addEventListener('click', function(e) {
            if (e.target.closest('.action-buttons')) return;
            
            const userId = this.dataset.userId;
            if (userId) {
                viewUser(userId);
            }
        });
    });
}

/**
 * Initialize modal events
 */
function initializeModalEvents() {
    // Close modals when clicking outside
    window.addEventListener('click', function(event) {
        const deleteModal = document.getElementById('deleteModal');
        const statusModal = document.getElementById('statusModal');
        
        if (statusModal && event.target === statusModal) {
            closeStatusModal();
        }
    });
    
    // Close modals with Escape key
    document.addEventListener('keydown', function(event) {
        if (event.key === 'Escape') {
            closeStatusModal();
        }
    });
}

/**
 * Initialize auto-hide alerts
 */
function initializeAlerts() {
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        // Auto-hide success alerts after 5 seconds
        if (alert.classList.contains('alert-success')) {
            setTimeout(() => {
                fadeOutElement(alert);
            }, 5000);
        }
        
        // Add close button to alerts
        const closeBtn = document.createElement('button');
        closeBtn.innerHTML = '&times;';
        closeBtn.className = 'alert-close';
        closeBtn.onclick = () => fadeOutElement(alert);
        alert.appendChild(closeBtn);
    });
}

/**
 * User Management Functions
 */

/**
 * View user details
 */
function viewUser(userId) {
    const contextPath = getContextPath();
    window.location.href = `${contextPath}/hr/user-details?id=${userId}`;
}

/**
 * Edit user
 */
function editUser(userId) {
    const contextPath = getContextPath();
    window.location.href = `${contextPath}/hr/edit-user?id=${userId}`;
}

/**
 * Toggle user status (activate/deactivate)
 */
function toggleUserStatus(userId, isActivate, userName) {
    statusChangeUserId = userId;
    statusChangeAction = isActivate ? 'activate' : 'deactivate';
    
    const title = isActivate ? 'Kích Hoạt Tài Khoản' : 'Vô Hiệu Hóa Tài Khoản';
    const message = isActivate 
        ? `Bạn có chắc chắn muốn kích hoạt tài khoản của người dùng "${userName}"?`
        : `Bạn có chắc chắn muốn vô hiệu hóa tài khoản của người dùng "${userName}"? Người dùng sẽ không thể đăng nhập sau khi bị vô hiệu hóa.`;
    
    showStatusModal(title, message);
}

/**
 * Show status change modal
 */
function showStatusModal(title, message) {
    document.getElementById('statusModalTitle').textContent = title;
    document.getElementById('statusModalMessage').textContent = message;
    document.getElementById('statusModal').style.display = 'block';
}

/**
 * Confirm status change
 */
function confirmStatusChange() {
    if (!statusChangeUserId || !statusChangeAction) return;
    
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = getContextPath() + '/hr/user-list';
    
    // Add action input
    const actionInput = document.createElement('input');
    actionInput.type = 'hidden';
    actionInput.name = 'action';
    actionInput.value = statusChangeAction;
    form.appendChild(actionInput);
    
    // Add userId input
    const userIdInput = document.createElement('input');
    userIdInput.type = 'hidden';
    userIdInput.name = 'userId';
    userIdInput.value = statusChangeUserId;
    form.appendChild(userIdInput);
    
    document.body.appendChild(form);
    
    // Show loading state
    showLoadingState('Đang xử lý...');
    
    form.submit();
}

/**
 * Close status modal
 */
function closeStatusModal() {
    document.getElementById('statusModal').style.display = 'none';
    statusChangeUserId = null;
    statusChangeAction = null;
}

/**
 * Export users
 */
function exportUsers() {
    const contextPath = getContextPath();
    const currentParams = new URLSearchParams(window.location.search);
    currentParams.set('action', 'export');
    
    // Show loading state
    showLoadingState('Đang xuất dữ liệu...');
    
    window.location.href = `${contextPath}/hr/users?${currentParams.toString()}`;
    
    // Hide loading state after a delay
    setTimeout(() => {
        hideLoadingState();
    }, 2000);
}

/**
 * Clear search form
 */
function clearSearch() {
    const form = document.querySelector('.user-search-form');
    if (!form) return;
    
    // Reset all form inputs
    const inputs = form.querySelectorAll('input, select');
    inputs.forEach(input => {
        if (input.type === 'text' || input.type === 'email' || input.type === 'date') {
            input.value = '';
        } else if (input.type === 'select-one') {
            input.selectedIndex = 0;
        }
    });
    
    // Remove search parameters from URL and reload
    const baseUrl = window.location.pathname;
    window.location.href = baseUrl;
}

/**
 * Change page size
 */
function changePageSize() {
    const pageSize = document.getElementById('pageSize').value;
    const currentParams = new URLSearchParams(window.location.search);
    currentParams.set('pageSize', pageSize);
    currentParams.set('page', '1'); // Reset to first page
    
    window.location.href = `${window.location.pathname}?${currentParams.toString()}`;
}

/**
 * Utility Functions
 */

/**
 * Get context path
 */
function getContextPath() {
    // Thử lấy từ meta tag trước
    const metaContextPath = document.querySelector('meta[name="context-path"]');
    if (metaContextPath) {
        return metaContextPath.getAttribute('content');
    }
    
    // Nếu không có meta tag, lấy từ window.location
    const path = window.location.pathname;
    const segments = path.split('/');
    
    // Nếu có ISP392 trong path thì đó là context path
    const contextIndex = segments.indexOf('ISP392');
    if (contextIndex > 0) {
        return '/' + segments.slice(1, contextIndex + 1).join('/');
    }
    
    // Fallback: return ISP392 nếu đang ở localhost
    if (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1') {
        return '/ISP392';
    }
    
    return '';
}

/**
 * Show loading state
 */
function showLoadingState(message = 'Đang xử lý...') {
    // Remove existing loading overlay
    hideLoadingState();
    
    const overlay = document.createElement('div');
    overlay.id = 'loadingOverlay';
    overlay.innerHTML = `
        <div class="loading-content">
            <div class="loading-spinner"></div>
            <div class="loading-message">${message}</div>
        </div>
    `;
    
    // Add styles
    overlay.style.cssText = `
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: rgba(0, 0, 0, 0.5);
        display: flex;
        align-items: center;
        justify-content: center;
        z-index: 9999;
    `;
    
    const style = document.createElement('style');
    style.textContent = `
        .loading-content {
            background: white;
            padding: 30px;
            border-radius: 10px;
            text-align: center;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
        }
        
        .loading-spinner {
            width: 40px;
            height: 40px;
            border: 4px solid #f3f3f3;
            border-top: 4px solid #3498db;
            border-radius: 50%;
            animation: spin 1s linear infinite;
            margin: 0 auto 15px;
        }
        
        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }
        
        .loading-message {
            color: #333;
            font-weight: 500;
        }
    `;
    
    document.head.appendChild(style);
    document.body.appendChild(overlay);
}

/**
 * Hide loading state
 */
function hideLoadingState() {
    const overlay = document.getElementById('loadingOverlay');
    if (overlay) {
        overlay.remove();
    }
}

/**
 * Fade out element
 */
function fadeOutElement(element) {
    element.style.opacity = '0';
    element.style.transition = 'opacity 0.5s ease';
    
    setTimeout(() => {
        element.remove();
    }, 500);
}

/**
 * Show tooltip
 */
function showTooltip(event) {
    const element = event.target.closest('[title]');
    if (!element) return;
    
    const title = element.getAttribute('title');
    if (!title) return;
    
    // Remove title to prevent browser tooltip
    element.setAttribute('data-tooltip', title);
    element.removeAttribute('title');
    
    // Create tooltip element
    const tooltip = document.createElement('div');
    tooltip.className = 'custom-tooltip';
    tooltip.textContent = title;
    tooltip.style.cssText = `
        position: absolute;
        background: #333;
        color: white;
        padding: 5px 10px;
        border-radius: 4px;
        font-size: 12px;
        white-space: nowrap;
        z-index: 1000;
        pointer-events: none;
    `;
    
    document.body.appendChild(tooltip);
    
    // Position tooltip
    const rect = element.getBoundingClientRect();
    const tooltipRect = tooltip.getBoundingClientRect();
    
    tooltip.style.left = rect.left + (rect.width - tooltipRect.width) / 2 + 'px';
    tooltip.style.top = rect.top - tooltipRect.height - 5 + 'px';
    
    // Store tooltip reference
    element._tooltip = tooltip;
}

/**
 * Hide tooltip
 */
function hideTooltip(event) {
    const element = event.target.closest('[data-tooltip]');
    if (!element) return;
    
    // Restore title
    const title = element.getAttribute('data-tooltip');
    element.setAttribute('title', title);
    element.removeAttribute('data-tooltip');
    
    // Remove tooltip
    if (element._tooltip) {
        element._tooltip.remove();
        delete element._tooltip;
    }
}

/**
 * Format date for display
 */
function formatDate(dateString) {
    if (!dateString) return '-';
    
    const date = new Date(dateString);
    const options = {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    };
    
    return date.toLocaleDateString('vi-VN', options);
}

/**
 * Format phone number
 */
function formatPhoneNumber(phone) {
    if (!phone) return '-';
    
    // Simple Vietnamese phone number formatting
    const cleaned = phone.replace(/\D/g, '');
    
    if (cleaned.length === 10) {
        return cleaned.replace(/(\d{4})(\d{3})(\d{3})/, '$1 $2 $3');
    } else if (cleaned.length === 11) {
        return cleaned.replace(/(\d{4})(\d{3})(\d{4})/, '$1 $2 $3');
    }
    
    return phone;
}

/**
 * Validate email format
 */
function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

/**
 * Validate phone number format (Vietnamese)
 */
function isValidPhone(phone) {
    const phoneRegex = /^0[0-9]{9,10}$/;
    return phoneRegex.test(phone.replace(/\s/g, ''));
}

/**
 * Debounce function
 */
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

/**
 * Show notification
 */
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.innerHTML = `
        <div class="notification-content">
            <span class="notification-message">${message}</span>
            <button class="notification-close" onclick="this.parentElement.parentElement.remove()">&times;</button>
        </div>
    `;
    
    // Add styles
    const style = document.createElement('style');
    style.textContent = `
        .notification {
            position: fixed;
            top: 20px;
            right: 20px;
            padding: 15px 20px;
            border-radius: 5px;
            color: white;
            z-index: 1000;
            animation: slideInRight 0.3s ease;
        }
        
        .notification-info { background: #17a2b8; }
        .notification-success { background: #28a745; }
        .notification-warning { background: #ffc107; color: #212529; }
        .notification-error { background: #dc3545; }
        
        .notification-content {
            display: flex;
            align-items: center;
            gap: 15px;
        }
        
        .notification-close {
            background: none;
            border: none;
            color: inherit;
            font-size: 18px;
            cursor: pointer;
            padding: 0;
            line-height: 1;
        }
        
        @keyframes slideInRight {
            from {
                opacity: 0;
                transform: translateX(100%);
            }
            to {
                opacity: 1;
                transform: translateX(0);
            }
        }
    `;
    
    if (!document.getElementById('notificationStyles')) {
        style.id = 'notificationStyles';
        document.head.appendChild(style);
    }
    
    document.body.appendChild(notification);
    
    // Auto remove after 5 seconds
    setTimeout(() => {
        if (notification.parentElement) {
            fadeOutElement(notification);
        }
    }, 5000);
}

/**
 * Handle AJAX errors
 */
function handleAjaxError(xhr, status, error) {
    console.error('AJAX Error:', status, error);
    
    let message = 'Đã xảy ra lỗi khi xử lý yêu cầu.';
    
    if (xhr.status === 403) {
        message = 'Bạn không có quyền thực hiện hành động này.';
    } else if (xhr.status === 404) {
        message = 'Không tìm thấy tài nguyên yêu cầu.';
    } else if (xhr.status === 500) {
        message = 'Lỗi máy chủ nội bộ. Vui lòng thử lại sau.';
    } else if (xhr.status === 0) {
        message = 'Không thể kết nối đến máy chủ. Vui lòng kiểm tra kết nối mạng.';
    }
    
    showNotification(message, 'error');
    hideLoadingState();
}

// Export functions for global use
window.UserManagement = {
    viewUser,
    editUser,
    toggleUserStatus,
    exportUsers,
    clearSearch,
    changePageSize,
    showNotification,
    formatDate,
    formatPhoneNumber,
    isValidEmail,
    isValidPhone
};