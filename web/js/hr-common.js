/**
 * HR Dashboard Common JavaScript Functions
 */

// Initialize HR Dashboard
document.addEventListener('DOMContentLoaded', function() {
    initHRDashboard();
});

function initHRDashboard() {
    // Initialize sidebar navigation
    initSidebarNavigation();
    
    // Initialize responsive design
    initResponsiveDesign();
    
    // Initialize common UI components
    initCommonUIComponents();
    
    // Initialize alerts auto-hide
    initAlerts();
}

// Sidebar Navigation
function initSidebarNavigation() {
    const navItems = document.querySelectorAll('.hr-nav-item');
    const currentPath = window.location.pathname;
    
    navItems.forEach(item => {
        // Remove active class from all items
        item.classList.remove('active');
        
        // Add active class to current page
        if (item.getAttribute('href') && currentPath.includes(item.getAttribute('href').split('/').pop())) {
            item.classList.add('active');
        }
        
        // Add click handler
        item.addEventListener('click', function(e) {
            // Show loading state
            showLoading();
        });
    });
}

// Responsive Design
function initResponsiveDesign() {
    const sidebar = document.querySelector('.hr-sidebar');
    const mainContent = document.querySelector('.hr-main-content');
    
    // Mobile menu toggle
    const menuToggle = document.createElement('button');
    menuToggle.className = 'hr-mobile-menu-toggle';
    menuToggle.innerHTML = '<i class="fas fa-bars"></i>';
    menuToggle.style.cssText = `
        display: none;
        position: fixed;
        top: 20px;
        left: 20px;
        z-index: 1001;
        background: #667eea;
        color: white;
        border: none;
        padding: 10px;
        border-radius: 5px;
        cursor: pointer;
    `;
    
    document.body.appendChild(menuToggle);
    
    // Show mobile menu button on small screens
    function checkScreenSize() {
        if (window.innerWidth <= 768) {
            menuToggle.style.display = 'block';
        } else {
            menuToggle.style.display = 'none';
            sidebar.classList.remove('active');
        }
    }
    
    // Toggle mobile menu
    menuToggle.addEventListener('click', function() {
        sidebar.classList.toggle('active');
    });
    
    // Close mobile menu when clicking outside
    document.addEventListener('click', function(e) {
        if (!sidebar.contains(e.target) && !menuToggle.contains(e.target)) {
            sidebar.classList.remove('active');
        }
    });
    
    window.addEventListener('resize', checkScreenSize);
    checkScreenSize();
}

// Common UI Components
function initCommonUIComponents() {
    // Initialize tooltips
    initTooltips();
    
    // Initialize form validation
    initFormValidation();
    
    // Initialize data tables
    initDataTables();
}

// Tooltips
function initTooltips() {
    const tooltipElements = document.querySelectorAll('[data-tooltip]');
    
    tooltipElements.forEach(element => {
        element.addEventListener('mouseenter', showTooltip);
        element.addEventListener('mouseleave', hideTooltip);
    });
}

function showTooltip(e) {
    const tooltip = document.createElement('div');
    tooltip.className = 'hr-tooltip';
    tooltip.textContent = e.target.getAttribute('data-tooltip');
    tooltip.style.cssText = `
        position: absolute;
        background: #333;
        color: white;
        padding: 5px 10px;
        border-radius: 4px;
        font-size: 12px;
        z-index: 1000;
        pointer-events: none;
    `;
    
    document.body.appendChild(tooltip);
    
    const rect = e.target.getBoundingClientRect();
    tooltip.style.left = rect.left + 'px';
    tooltip.style.top = (rect.top - tooltip.offsetHeight - 5) + 'px';
    
    e.target.tooltipElement = tooltip;
}

function hideTooltip(e) {
    if (e.target.tooltipElement) {
        document.body.removeChild(e.target.tooltipElement);
        delete e.target.tooltipElement;
    }
}

// Form Validation
function initFormValidation() {
    const forms = document.querySelectorAll('.hr-form');
    
    forms.forEach(form => {
        form.addEventListener('submit', function(e) {
            if (!validateForm(this)) {
                e.preventDefault();
            }
        });
    });
}

function validateForm(form) {
    let isValid = true;
    const requiredFields = form.querySelectorAll('[required]');
    
    requiredFields.forEach(field => {
        if (!field.value.trim()) {
            showFieldError(field, 'This field is required');
            isValid = false;
        } else {
            clearFieldError(field);
        }
    });
    
    // Email validation
    const emailFields = form.querySelectorAll('input[type="email"]');
    emailFields.forEach(field => {
        if (field.value && !isValidEmail(field.value)) {
            showFieldError(field, 'Please enter a valid email address');
            isValid = false;
        }
    });
    
    return isValid;
}

function showFieldError(field, message) {
    clearFieldError(field);
    
    const error = document.createElement('div');
    error.className = 'hr-field-error';
    error.textContent = message;
    error.style.cssText = `
        color: #dc3545;
        font-size: 12px;
        margin-top: 5px;
    `;
    
    field.parentNode.appendChild(error);
    field.classList.add('error');
}

function clearFieldError(field) {
    const existingError = field.parentNode.querySelector('.hr-field-error');
    if (existingError) {
        existingError.remove();
    }
    field.classList.remove('error');
}

function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

// Data Tables
function initDataTables() {
    const tables = document.querySelectorAll('.hr-table[data-sortable="true"]');
    
    tables.forEach(table => {
        makeSortable(table);
    });
}

function makeSortable(table) {
    const headers = table.querySelectorAll('th[data-sortable="true"]');
    
    headers.forEach((header, index) => {
        header.style.cursor = 'pointer';
        header.innerHTML += ' <i class="fas fa-sort"></i>';
        
        header.addEventListener('click', function() {
            sortTable(table, index, this);
        });
    });
}

function sortTable(table, columnIndex, header) {
    const tbody = table.querySelector('tbody');
    const rows = Array.from(tbody.querySelectorAll('tr'));
    const isAscending = !header.classList.contains('sort-asc');
    
    // Clear all sort classes
    table.querySelectorAll('th').forEach(th => {
        th.classList.remove('sort-asc', 'sort-desc');
        const icon = th.querySelector('i');
        if (icon) icon.className = 'fas fa-sort';
    });
    
    // Set current sort class and icon
    header.classList.add(isAscending ? 'sort-asc' : 'sort-desc');
    const icon = header.querySelector('i');
    icon.className = isAscending ? 'fas fa-sort-up' : 'fas fa-sort-down';
    
    rows.sort((a, b) => {
        const aVal = a.cells[columnIndex].textContent.trim();
        const bVal = b.cells[columnIndex].textContent.trim();
        
        if (!isNaN(aVal) && !isNaN(bVal)) {
            return isAscending ? aVal - bVal : bVal - aVal;
        }
        
        return isAscending ? 
            aVal.localeCompare(bVal) : 
            bVal.localeCompare(aVal);
    });
    
    rows.forEach(row => tbody.appendChild(row));
}

// Alerts
function initAlerts() {
    const alerts = document.querySelectorAll('.hr-alert[data-auto-hide]');
    
    alerts.forEach(alert => {
        const timeout = parseInt(alert.getAttribute('data-auto-hide')) || 5000;
        
        setTimeout(() => {
            hideAlert(alert);
        }, timeout);
    });
}

function showAlert(message, type = 'info', duration = 5000) {
    const alertContainer = document.querySelector('.hr-alerts') || createAlertContainer();
    
    const alert = document.createElement('div');
    alert.className = `hr-alert hr-alert-${type}`;
    alert.innerHTML = `
        ${message}
        <button type="button" class="hr-alert-close" onclick="hideAlert(this.parentNode)">
            <i class="fas fa-times"></i>
        </button>
    `;
    
    // Add close button styles
    const closeBtn = alert.querySelector('.hr-alert-close');
    closeBtn.style.cssText = `
        float: right;
        background: none;
        border: none;
        font-size: 16px;
        cursor: pointer;
        color: inherit;
        opacity: 0.7;
        margin-left: 10px;
    `;
    
    alertContainer.appendChild(alert);
    
    if (duration > 0) {
        setTimeout(() => hideAlert(alert), duration);
    }
    
    return alert;
}

function hideAlert(alert) {
    if (alert && alert.parentNode) {
        alert.style.opacity = '0';
        alert.style.transform = 'translateY(-20px)';
        
        setTimeout(() => {
            if (alert.parentNode) {
                alert.parentNode.removeChild(alert);
            }
        }, 300);
    }
}

function createAlertContainer() {
    const container = document.createElement('div');
    container.className = 'hr-alerts';
    container.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        z-index: 1050;
        max-width: 400px;
    `;
    
    document.body.appendChild(container);
    return container;
}

// Loading States
function showLoading(element = null) {
    if (element) {
        const originalContent = element.innerHTML;
        element.setAttribute('data-original-content', originalContent);
        element.innerHTML = '<span class="hr-loading"></span> Loading...';
        element.disabled = true;
    } else {
        // Show global loading overlay
        const overlay = document.createElement('div');
        overlay.id = 'hr-loading-overlay';
        overlay.style.cssText = `
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0,0,0,0.5);
            display: flex;
            justify-content: center;
            align-items: center;
            z-index: 2000;
        `;
        
        overlay.innerHTML = `
            <div style="background: white; padding: 20px; border-radius: 8px; text-align: center;">
                <div class="hr-loading" style="width: 40px; height: 40px; margin: 0 auto 15px;"></div>
                <p>Loading...</p>
            </div>
        `;
        
        document.body.appendChild(overlay);
    }
}

function hideLoading(element = null) {
    if (element) {
        const originalContent = element.getAttribute('data-original-content');
        if (originalContent) {
            element.innerHTML = originalContent;
            element.removeAttribute('data-original-content');
        }
        element.disabled = false;
    } else {
        // Hide global loading overlay
        const overlay = document.getElementById('hr-loading-overlay');
        if (overlay) {
            document.body.removeChild(overlay);
        }
    }
}

// AJAX Helpers
function hrAjaxRequest(url, options = {}) {
    const defaultOptions = {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
        }
    };
    
    const config = { ...defaultOptions, ...options };
    
    return fetch(url, config)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
            return response.json();
        })
        .catch(error => {
            console.error('HR AJAX Request Error:', error);
            showAlert('An error occurred while processing your request.', 'danger');
            throw error;
        });
}

// Confirmation Dialogs
function confirmAction(message, callback) {
    if (confirm(message)) {
        callback();
    }
}

// Utility Functions
function formatDate(date) {
    return new Date(date).toLocaleDateString();
}

function formatDateTime(date) {
    return new Date(date).toLocaleString();
}

function capitalizeFirst(str) {
    return str.charAt(0).toUpperCase() + str.slice(1);
}

// Export common functions for use in other HR modules
window.HRCommon = {
    showAlert,
    hideAlert,
    showLoading,
    hideLoading,
    hrAjaxRequest,
    confirmAction,
    formatDate,
    formatDateTime,
    capitalizeFirst,
    validateForm
};