/**
 * Barista Dashboard JavaScript
 * Interactive features and chart rendering for CoffeeLux Barista System
 */

// ================================
// Global Variables
// ================================
let orderStatusChart = null;
let refreshInterval = null;
const REFRESH_INTERVAL = 30000; // 30 seconds

// ================================
// DOM Ready Initialization
// ================================
document.addEventListener('DOMContentLoaded', function() {
    initializeDashboard();
    setupEventListeners();
    initializeCharts();
    startAutoRefresh();
});

// ================================
// Dashboard Initialization
// ================================
function initializeDashboard() {
    console.log('🚀 Barista Dashboard initializing...');
    
    // Initialize sidebar toggle for mobile
    initializeSidebarToggle();
    
    // Initialize tooltips
    initializeTooltips();
    
    // Initialize theme
    initializeTheme();
    
    console.log('✅ Dashboard initialized successfully');
}

// ================================
// Event Listeners Setup
// ================================
function setupEventListeners() {
    // Sidebar toggle
    const sidebarToggle = document.getElementById('sidebarToggle');
    if (sidebarToggle) {
        sidebarToggle.addEventListener('click', toggleSidebar);
    }
    
    // Refresh button
    const refreshBtn = document.querySelector('.barista-refresh-btn');
    if (refreshBtn) {
        refreshBtn.addEventListener('click', refreshDashboard);
    }
    
    // View all buttons
    const viewAllBtns = document.querySelectorAll('.view-all-btn');
    viewAllBtns.forEach(btn => {
        btn.addEventListener('click', handleViewAll);
    });
    
    // Order rows click handling
    const orderRows = document.querySelectorAll('.orders-table tbody tr');
    orderRows.forEach(row => {
        row.addEventListener('click', function() {
            const orderId = this.cells[0].textContent;
            showOrderDetails(orderId);
        });
    });
    
    // Product items click handling
    const productItems = document.querySelectorAll('.product-item');
    productItems.forEach(item => {
        item.addEventListener('click', function() {
            const productName = this.querySelector('.product-name').textContent;
            showProductDetails(productName);
        });
    });
    
    // Issue items click handling
    const issueItems = document.querySelectorAll('.issue-item');
    issueItems.forEach(item => {
        item.addEventListener('click', function() {
            const issueId = this.dataset.issueId;
            if (issueId) {
                showIssueDetails(issueId);
            }
        });
    });
}

// ================================
// Sidebar Functions
// ================================
function initializeSidebarToggle() {
    const sidebar = document.querySelector('.barista-sidebar');
    const mainContent = document.querySelector('.barista-main-content');
    
    if (window.innerWidth <= 768) {
        sidebar.classList.add('mobile');
        mainContent.classList.add('mobile');
    }
    
    // Handle window resize
    window.addEventListener('resize', function() {
        if (window.innerWidth <= 768) {
            sidebar.classList.add('mobile');
            mainContent.classList.add('mobile');
        } else {
            sidebar.classList.remove('mobile', 'open');
            mainContent.classList.remove('mobile');
        }
    });
}

function toggleSidebar() {
    const sidebar = document.querySelector('.barista-sidebar');
    sidebar.classList.toggle('open');
}

// ================================
// Chart Initialization
// ================================
function initializeCharts() {
    initializeOrderStatusChart();
}

function initializeOrderStatusChart() {
    const ctx = document.getElementById('orderStatusChart');
    if (!ctx) {
        console.error('Order Status Chart canvas not found!');
        return;
    }
    
    if (!window.dashboardData) {
        console.error('Dashboard data not available!');
        return;
    }
    
    console.log('Dashboard data:', window.dashboardData);
    const data = window.dashboardData.orderStatus;
    
    if (!data || !data.labels || !data.data) {
        console.error('Invalid chart data:', data);
        return;
    }
    
    console.log('Chart data:', data);
    
    orderStatusChart = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: data.labels,
            datasets: [{
                data: data.data,
                backgroundColor: [
                    '#4682B4', // New - Steel Blue
                    '#FF8C00', // Preparing - Dark Orange
                    '#F4A460', // Ready - Sandy Brown
                    '#228B22'  // Completed - Forest Green
                ],
                borderWidth: 2,
                borderColor: '#FFFFFF',
                hoverBorderWidth: 3
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        padding: 20,
                        usePointStyle: true,
                        font: {
                            size: 12,
                            family: 'Inter'
                        }
                    }
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            const label = context.label || '';
                            const value = context.raw;
                            const total = context.dataset.data.reduce((a, b) => a + b, 0);
                            const percentage = ((value / total) * 100).toFixed(1);
                            return `${label}: ${value} orders (${percentage}%)`;
                        }
                    }
                }
            },
            animation: {
                animateScale: true,
                animateRotate: true
            }
        }
    });
}

// ================================
// Data Refresh Functions
// ================================
function startAutoRefresh() {
    refreshInterval = setInterval(() => {
        refreshDashboardData();
    }, REFRESH_INTERVAL);
}

function stopAutoRefresh() {
    if (refreshInterval) {
        clearInterval(refreshInterval);
        refreshInterval = null;
    }
}

function refreshDashboard() {
    showRefreshIndicator();
    location.reload();
}

function refreshDashboardData() {
    fetch('/barista/dashboard', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: 'action=refreshStats'
    })
    .then(response => response.json())
    .then(data => {
        updateDashboardStats(data);
        updateLastUpdatedTime();
    })
    .catch(error => {
        console.error('Error refreshing dashboard data:', error);
    });
}

function showRefreshIndicator() {
    const refreshBtn = document.querySelector('.barista-refresh-btn');
    if (refreshBtn) {
        const icon = refreshBtn.querySelector('i');
        icon.classList.add('fa-spin');
        
        setTimeout(() => {
            icon.classList.remove('fa-spin');
        }, 1000);
    }
}

function updateDashboardStats(data) {
    // Update stat cards
    updateStatCard('.orders-card .card-value', data.todayOrders);
    updateStatCard('.completed-card .card-value', data.completedOrders);
    updateStatCard('.preparing-card .card-value', data.preparingOrders);
    updateStatCard('.revenue-card .card-value', formatCurrency(data.todayRevenue));
    
    // Update status counts
    updateStatusCount('.status-item.new .status-count', data.pendingOrders);
    updateStatusCount('.status-item.preparing .status-count', data.preparingOrders);
    updateStatusCount('.status-item.ready .status-count', data.readyOrders);
    updateStatusCount('.status-item.completed .status-count', data.completedOrders);
    
    // Update chart if data changed
    if (orderStatusChart && data.orderStatus) {
        orderStatusChart.data.datasets[0].data = [
            data.pendingOrders,
            data.preparingOrders,
            data.readyOrders,
            data.completedOrders
        ];
        orderStatusChart.update('none');
    }
}

function updateStatCard(selector, value) {
    const element = document.querySelector(selector);
    if (element) {
        element.textContent = value;
        element.classList.add('updated');
        setTimeout(() => element.classList.remove('updated'), 500);
    }
}

function updateStatusCount(selector, value) {
    const element = document.querySelector(selector);
    if (element) {
        element.textContent = value;
        element.classList.add('pulse');
        setTimeout(() => element.classList.remove('pulse'), 500);
    }
}

function updateLastUpdatedTime() {
    const lastUpdated = document.getElementById('lastUpdated');
    if (lastUpdated) {
        const now = new Date();
        const timeString = now.toLocaleTimeString('vi-VN', { 
            hour: '2-digit', 
            minute: '2-digit' 
        });
        lastUpdated.textContent = timeString;
    }
}

// ================================
// Utility Functions
// ================================
function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(amount);
}

function formatNumber(num) {
    return new Intl.NumberFormat('vi-VN').format(num);
}

function formatDateTime(dateString) {
    const date = new Date(dateString);
    return date.toLocaleString('vi-VN', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// ================================
// Modal and Detail Functions
// ================================
function showOrderDetails(orderId) {
    console.log('Show order details for:', orderId);
    // TODO: Implement order details modal
    showNotification(`Order details for ${orderId} - Feature coming soon!`, 'info');
}

function showProductDetails(productName) {
    console.log('Show product details for:', productName);
    // TODO: Implement product details modal
    showNotification(`Product details for ${productName} - Feature coming soon!`, 'info');
}

function showIssueDetails(issueId) {
    console.log('Show issue details for:', issueId);
    // TODO: Implement issue details modal
    showNotification(`Issue details for #${issueId} - Feature coming soon!`, 'info');
}

function handleViewAll(event) {
    const button = event.target;
    const cardType = button.closest('.data-card').classList.contains('recent-orders') 
        ? 'orders' : 'products';
    
    console.log('View all:', cardType);
    // TODO: Implement navigation to full view
    showNotification(`View all ${cardType} - Feature coming soon!`, 'info');
}

// ================================
// Notification System
// ================================
function showNotification(message, type = 'info', duration = 3000) {
    const notification = createNotificationElement(message, type);
    document.body.appendChild(notification);
    
    // Show notification
    setTimeout(() => {
        notification.classList.add('show');
    }, 100);
    
    // Hide and remove notification
    setTimeout(() => {
        notification.classList.add('hide');
        setTimeout(() => {
            document.body.removeChild(notification);
        }, 300);
    }, duration);
}

function createNotificationElement(message, type) {
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    
    const icons = {
        success: 'fa-check-circle',
        error: 'fa-exclamation-circle',
        warning: 'fa-exclamation-triangle',
        info: 'fa-info-circle'
    };
    
    notification.innerHTML = `
        <i class="fas ${icons[type] || icons.info}"></i>
        <span>${message}</span>
        <button class="notification-close" onclick="this.parentElement.remove()">
            <i class="fas fa-times"></i>
        </button>
    `;
    
    return notification;
}

// ================================
// Theme Functions
// ================================
function initializeTheme() {
    // Check for saved theme preference
    const savedTheme = localStorage.getItem('baristaDarkTheme');
    if (savedTheme === 'true') {
        document.body.classList.add('dark-theme');
        updateThemeToggleIcon();
    }
}

function toggleTheme() {
    document.body.classList.toggle('dark-theme');
    
    // Save preference
    const isDark = document.body.classList.contains('dark-theme');
    localStorage.setItem('baristaDarkTheme', isDark);
    
    // Update icon
    updateThemeToggleIcon();
    
    // Show notification
    showNotification(
        `Switched to ${isDark ? 'dark' : 'light'} theme`,
        'success',
        2000
    );
}

function updateThemeToggleIcon() {
    const themeBtn = document.querySelector('.quick-action-btn i.fa-moon');
    if (themeBtn) {
        const isDark = document.body.classList.contains('dark-theme');
        themeBtn.className = isDark ? 'fas fa-sun' : 'fas fa-moon';
    }
}

// ================================
// Tooltip Functions
// ================================
function initializeTooltips() {
    const tooltipElements = document.querySelectorAll('[title]');
    tooltipElements.forEach(element => {
        element.addEventListener('mouseenter', showTooltip);
        element.addEventListener('mouseleave', hideTooltip);
    });
}

function showTooltip(event) {
    const element = event.target;
    const title = element.getAttribute('title');
    if (!title) return;
    
    // Create tooltip
    const tooltip = document.createElement('div');
    tooltip.className = 'barista-tooltip';
    tooltip.textContent = title;
    document.body.appendChild(tooltip);
    
    // Position tooltip
    const rect = element.getBoundingClientRect();
    tooltip.style.left = `${rect.left + rect.width / 2}px`;
    tooltip.style.top = `${rect.top - tooltip.offsetHeight - 8}px`;
    
    // Store tooltip reference
    element._tooltip = tooltip;
    
    // Remove title to prevent default tooltip
    element.removeAttribute('title');
    element._originalTitle = title;
}

function hideTooltip(event) {
    const element = event.target;
    if (element._tooltip) {
        document.body.removeChild(element._tooltip);
        element._tooltip = null;
    }
    
    // Restore original title
    if (element._originalTitle) {
        element.setAttribute('title', element._originalTitle);
    }
}

// ================================
// Performance Monitoring
// ================================
function trackPerformance() {
    // Track page load time
    window.addEventListener('load', function() {
        const loadTime = performance.now();
        console.log(`📊 Dashboard loaded in ${Math.round(loadTime)}ms`);
        
        // Track to analytics (if available)
        if (typeof gtag !== 'undefined') {
            gtag('event', 'page_load_time', {
                value: Math.round(loadTime),
                page: 'barista_dashboard'
            });
        }
    });
}

// ================================
// Error Handling
// ================================
window.addEventListener('error', function(event) {
    console.error('❌ Dashboard Error:', event.error);
    
    // Show user-friendly error message
    showNotification(
        'An error occurred. Please refresh the page.',
        'error',
        5000
    );
});

// ================================
// Cleanup on Page Unload
// ================================
window.addEventListener('beforeunload', function() {
    stopAutoRefresh();
    
    // Cleanup chart instances
    if (orderStatusChart) {
        orderStatusChart.destroy();
    }
});

// ================================
// CSS Classes for Animations
// ================================
// Add CSS styles for animations
const style = document.createElement('style');
style.textContent = `
    .updated {
        animation: highlight 0.5s ease-in-out;
    }
    
    .pulse {
        animation: pulse 0.5s ease-in-out;
    }
    
    @keyframes highlight {
        0%, 100% { background-color: transparent; }
        50% { background-color: var(--barista-accent); }
    }
    
    @keyframes pulse {
        0%, 100% { transform: scale(1); }
        50% { transform: scale(1.1); }
    }
    
    .notification {
        position: fixed;
        top: 20px;
        right: 20px;
        background: var(--barista-bg-card);
        border: 1px solid var(--barista-border);
        border-radius: 8px;
        padding: 1rem 1.5rem;
        box-shadow: var(--barista-shadow);
        display: flex;
        align-items: center;
        gap: 0.5rem;
        z-index: 10000;
        transform: translateX(100%);
        transition: transform 0.3s ease;
        min-width: 300px;
        max-width: 500px;
    }
    
    .notification.show {
        transform: translateX(0);
    }
    
    .notification.hide {
        transform: translateX(100%);
    }
    
    .notification-success { border-left: 4px solid var(--barista-success); }
    .notification-error { border-left: 4px solid var(--barista-danger); }
    .notification-warning { border-left: 4px solid var(--barista-warning); }
    .notification-info { border-left: 4px solid var(--barista-info); }
    
    .notification-close {
        background: none;
        border: none;
        color: var(--barista-text-muted);
        cursor: pointer;
        padding: 0.25rem;
        margin-left: auto;
    }
    
    .barista-tooltip {
        position: absolute;
        background: var(--barista-text-primary);
        color: var(--barista-text-white);
        padding: 0.5rem;
        border-radius: 4px;
        font-size: 0.875rem;
        z-index: 10000;
        pointer-events: none;
        white-space: nowrap;
        transform: translateX(-50%);
    }
    
    .barista-tooltip:before {
        content: '';
        position: absolute;
        top: 100%;
        left: 50%;
        transform: translateX(-50%);
        border: 4px solid transparent;
        border-top-color: var(--barista-text-primary);
    }
`;
document.head.appendChild(style);

// Initialize performance tracking
trackPerformance();

console.log('☕ Barista Dashboard JavaScript loaded successfully!');