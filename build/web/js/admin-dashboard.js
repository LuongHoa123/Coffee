/**
 * Admin Dashboard JavaScript
 * Handles interactivity for the admin dashboard including sidebar toggle,
 * chart rendering, and dynamic content updates
 */

// Global variables
let sidebarCollapsed = false;
let charts = {};

// Initialize dashboard when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    initializeDashboard();
});

/**
 * Initialize all dashboard components
 */
function initializeDashboard() {
    initializeSidebar();
    initializeCharts();
    initializeRealTimeUpdates();
    initializeEventHandlers();
    updateCurrentTime();
    
    // Update time every second
    setInterval(updateCurrentTime, 1000);
}

/**
 * Initialize sidebar functionality
 */
function initializeSidebar() {
    const sidebarToggle = document.getElementById('sidebarToggle');
    const sidebar = document.querySelector('.admin-sidebar');
    const mainContent = document.querySelector('.admin-main-content');
    
    if (sidebarToggle) {
        sidebarToggle.addEventListener('click', function() {
            toggleSidebar();
        });
    }
    
    // Handle responsive sidebar for mobile
    if (window.innerWidth <= 768) {
        sidebar?.classList.add('collapsed');
        mainContent?.classList.add('expanded');
        sidebarCollapsed = true;
    }
}

/**
 * Toggle sidebar visibility
 */
function toggleSidebar() {
    const sidebar = document.querySelector('.admin-sidebar');
    const mainContent = document.querySelector('.admin-main-content');
    const toggleIcon = document.querySelector('#sidebarToggle i');
    
    if (sidebar && mainContent) {
        sidebarCollapsed = !sidebarCollapsed;
        
        if (sidebarCollapsed) {
            sidebar.classList.add('collapsed');
            mainContent.classList.add('expanded');
            if (toggleIcon) {
                toggleIcon.className = 'fas fa-chevron-right';
            }
        } else {
            sidebar.classList.remove('collapsed');
            mainContent.classList.remove('expanded');
            if (toggleIcon) {
                toggleIcon.className = 'fas fa-bars';
            }
        }
        
        // Resize charts after sidebar toggle
        setTimeout(() => {
            Object.values(charts).forEach(chart => {
                if (chart && typeof chart.resize === 'function') {
                    chart.resize();
                }
            });
        }, 300);
    }
}

/**
 * Initialize all charts
 */
function initializeCharts() {
    initializeRoleDistributionChart();
    initializeStatusChart();
}

/**
 * Initialize role distribution pie chart
 */
function initializeRoleDistributionChart() {
    const canvas = document.getElementById('roleDistributionChart');
    if (!canvas) return;
    
    const ctx = canvas.getContext('2d');
    const data = window.dashboardData?.usersByRole || {};
    
    const labels = Object.keys(data);
    const values = Object.values(data);
    const colors = [
        '#2a5298', '#28a745', '#ffc107', '#dc3545', 
        '#17a2b8', '#6f42c1', '#fd7e14', '#20c997'
    ];
    
    charts.roleChart = new Chart(ctx, {
        type: 'pie',
        data: {
            labels: labels,
            datasets: [{
                data: values,
                backgroundColor: colors.slice(0, labels.length),
                borderWidth: 2,
                borderColor: '#fff'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        padding: 15,
                        usePointStyle: true
                    }
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            const total = context.dataset.data.reduce((a, b) => a + b, 0);
                            const percentage = ((context.parsed / total) * 100).toFixed(1);
                            return `${context.label}: ${context.parsed} users (${percentage}%)`;
                        }
                    }
                }
            }
        }
    });
}

/**
 * Initialize user status doughnut chart
 */
function initializeStatusChart() {
    const canvas = document.getElementById('statusChart');
    if (!canvas) return;
    
    const ctx = canvas.getContext('2d');
    const data = window.dashboardData?.usersByStatus || {};
    
    charts.statusChart = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: ['Active Users', 'Inactive Users'],
            datasets: [{
                data: [data.active || 0, data.inactive || 0],
                backgroundColor: ['#28a745', '#dc3545'],
                borderWidth: 2,
                borderColor: '#fff'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        padding: 15,
                        usePointStyle: true
                    }
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            const total = context.dataset.data.reduce((a, b) => a + b, 0);
                            const percentage = total > 0 ? ((context.parsed / total) * 100).toFixed(1) : 0;
                            return `${context.label}: ${context.parsed} users (${percentage}%)`;
                        }
                    }
                }
            },
            cutout: '60%'
        }
    });
}

/**
 * Initialize real-time updates
 */
function initializeRealTimeUpdates() {
    // Update dashboard stats every 30 seconds
    setInterval(function() {
        if (document.visibilityState === 'visible') {
            updateDashboardStats();
        }
    }, 30000);
}

/**
 * Initialize event handlers
 */
function initializeEventHandlers() {
    // Handle window resize
    window.addEventListener('resize', function() {
        handleWindowResize();
    });
    
    // Handle visibility change to pause/resume updates
    document.addEventListener('visibilitychange', function() {
        if (document.visibilityState === 'visible') {
            updateCurrentTime();
        }
    });
    
    // Add click handlers for alert dismissal
    const alerts = document.querySelectorAll('.admin-alert');
    alerts.forEach(alert => {
        const closeBtn = document.createElement('button');
        closeBtn.innerHTML = '<i class="fas fa-times"></i>';
        closeBtn.className = 'alert-close-btn';
        closeBtn.style.cssText = `
            background: none; 
            border: none; 
            color: currentColor; 
            float: right; 
            cursor: pointer;
            font-size: 1.1rem;
            margin-left: 1rem;
        `;
        closeBtn.onclick = () => alert.remove();
        alert.appendChild(closeBtn);
    });
}

/**
 * Handle window resize events
 */
function handleWindowResize() {
    // Resize charts
    Object.values(charts).forEach(chart => {
        if (chart && typeof chart.resize === 'function') {
            chart.resize();
        }
    });
    
    // Handle mobile sidebar behavior
    const sidebar = document.querySelector('.admin-sidebar');
    const mainContent = document.querySelector('.admin-main-content');
    
    if (window.innerWidth <= 768) {
        if (!sidebarCollapsed) {
            sidebar?.classList.add('collapsed');
            mainContent?.classList.add('expanded');
            sidebarCollapsed = true;
        }
    }
}

/**
 * Update current time display
 */
function updateCurrentTime() {
    const timeElement = document.getElementById('currentTime');
    if (timeElement) {
        const now = new Date();
        const timeString = now.toLocaleString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit'
        });
        timeElement.textContent = timeString;
    }
}

/**
 * Refresh dashboard data
 */
function refreshDashboard() {
    showLoading();
    
    // Reload the page to get fresh data
    window.location.reload();
}

/**
 * Update dashboard statistics via AJAX
 */
function updateDashboardStats() {
    const refreshBtn = document.querySelector('.admin-refresh-btn');
    const originalText = refreshBtn?.innerHTML;
    
    if (refreshBtn) {
        refreshBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Updating...';
        refreshBtn.disabled = true;
    }
    
    // Simulate API call - in real implementation, use fetch() to get updated data
    setTimeout(() => {
        if (refreshBtn && originalText) {
            refreshBtn.innerHTML = originalText;
            refreshBtn.disabled = false;
        }
        
        // Show success message
        showNotification('Dashboard updated successfully', 'success');
    }, 2000);
}

/**
 * Show loading indicator
 */
function showLoading() {
    const refreshBtn = document.querySelector('.admin-refresh-btn');
    if (refreshBtn) {
        refreshBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Loading...';
        refreshBtn.disabled = true;
    }
}

/**
 * Show notification message
 */
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `admin-alert admin-alert-${type}`;
    notification.innerHTML = `
        <i class="fas fa-${getNotificationIcon(type)}"></i>
        ${message}
        <button onclick="this.parentElement.remove()" style="background:none;border:none;color:currentColor;float:right;cursor:pointer;font-size:1.1rem;margin-left:1rem;">
            <i class="fas fa-times"></i>
        </button>
    `;
    
    const content = document.querySelector('.admin-dashboard-content');
    if (content) {
        content.insertBefore(notification, content.firstChild);
        
        // Auto-remove after 5 seconds
        setTimeout(() => {
            if (notification.parentElement) {
                notification.remove();
            }
        }, 5000);
    }
}

/**
 * Get appropriate icon for notification type
 */
function getNotificationIcon(type) {
    switch (type) {
        case 'success': return 'check-circle';
        case 'error': return 'exclamation-circle';
        case 'warning': return 'exclamation-triangle';
        default: return 'info-circle';
    }
}

/**
 * User management functions
 */
function viewUser(userId) {
    if (userId) {
        window.location.href = `${getContextPath()}/admin/view-user?id=${userId}`;
    }
}

function editUser(userId) {
    if (userId) {
        window.location.href = `${getContextPath()}/admin/edit-user?id=${userId}`;
    }
}

/**
 * Quick action functions
 */
function clearCache() {
    if (confirm('Are you sure you want to clear the system cache?')) {
        showLoading();
        
        // Simulate cache clearing
        setTimeout(() => {
            showNotification('Cache cleared successfully', 'success');
            const refreshBtn = document.querySelector('.admin-refresh-btn');
            if (refreshBtn) {
                refreshBtn.innerHTML = '<i class="fas fa-sync-alt"></i> Refresh';
                refreshBtn.disabled = false;
            }
        }, 2000);
    }
}

/**
 * Get context path for URL construction
 */
function getContextPath() {
    return window.location.pathname.substring(0, window.location.pathname.indexOf('/', 2));
}

/**
 * Utility function to format numbers
 */
function formatNumber(num) {
    return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
}

/**
 * Utility function to animate counters
 */
function animateCounter(element, target, duration = 1000) {
    const start = parseInt(element.textContent) || 0;
    const increment = (target - start) / (duration / 16);
    let current = start;
    
    const timer = setInterval(() => {
        current += increment;
        if (
            (increment > 0 && current >= target) ||
            (increment < 0 && current <= target)
        ) {
            current = target;
            clearInterval(timer);
        }
        element.textContent = Math.floor(current);
    }, 16);
}

/**
 * Initialize counter animations on page load
 */
function initializeCounterAnimations() {
    const counters = document.querySelectorAll('.admin-stat-number');
    counters.forEach(counter => {
        const target = parseInt(counter.textContent);
        counter.textContent = '0';
        animateCounter(counter, target, 1500);
    });
}

// Initialize counter animations when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    setTimeout(initializeCounterAnimations, 500);
});

/**
 * Export functions for global access
 */
window.AdminDashboard = {
    refreshDashboard,
    toggleSidebar,
    viewUser,
    editUser,
    clearCache,
    showNotification,
    updateDashboardStats
};