/**
 * HR Dashboard JavaScript
 * Handles dashboard interactions, charts, and data updates
 */

let userGrowthChart = null;
let dashboardInterval = null;

// Initialize dashboard
function initDashboard(data) {
    console.log('Initializing HR Dashboard...', data);
    
    // Initialize chart
    initUserGrowthChart(data.userGrowthData || []);
    
    // Start auto-refresh
    startAutoRefresh();
    
    // Animate stat cards
    animateStatCards();
    
    // Initialize tooltips
    initDashboardTooltips();
}

// Initialize user growth chart
function initUserGrowthChart(data) {
    const ctx = document.getElementById('userGrowthChart');
    if (!ctx) {
        console.log('User growth chart canvas not found');
        return;
    }
    
    console.log('Initializing chart with data:', data);
    
    // Ensure data is an array
    if (!Array.isArray(data)) {
        console.error('Chart data is not an array:', data);
        data = [];
    }
    
    // Prepare chart data
    const labels = data.map(item => {
        if (!item || !item.period) return '';
        try {
            const date = new Date(item.period + '-01');
            return date.toLocaleDateString('en-US', { month: 'short', year: 'numeric' });
        } catch (e) {
            console.error('Error parsing date:', item.period, e);
            return item.period;
        }
    });
    
    const counts = data.map(item => parseInt(item?.count) || 0);
    
    console.log('Chart labels:', labels);
    console.log('Chart data:', counts);
    
    // Create chart
    userGrowthChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: 'New Users',
                data: counts,
                borderColor: '#667eea',
                backgroundColor: 'rgba(102, 126, 234, 0.1)',
                borderWidth: 3,
                fill: true,
                tension: 0.4,
                pointBackgroundColor: '#667eea',
                pointBorderColor: '#fff',
                pointBorderWidth: 2,
                pointRadius: 6,
                pointHoverRadius: 8
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    grid: {
                        color: '#e9ecef'
                    },
                    ticks: {
                        color: '#6c757d',
                        stepSize: 1 // Show whole numbers only
                    }
                },
                x: {
                    grid: {
                        color: '#e9ecef'
                    },
                    ticks: {
                        color: '#6c757d'
                    }
                }
            },
            elements: {
                point: {
                    hoverBackgroundColor: '#667eea',
                    hoverBorderColor: '#fff'
                }
            }
        }
    });
}

// Update growth chart
function updateGrowthChart() {
    const period = document.getElementById('growthPeriod').value;
    const contextPath = getContextPath();
    const requestUrl = `${contextPath}/hr/dashboard`;
    const requestBody = `action=getUserGrowth&period=${encodeURIComponent(period)}`;
    
    console.log('Updating chart with period:', period);
    console.log('Request URL:', requestUrl);
    console.log('Request body:', requestBody);
    
    // Show loading
    const chartContainer = document.querySelector('.chart-container');
    showChartLoading(chartContainer);
    
    // Fetch new data
    fetch(requestUrl, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'X-Requested-With': 'XMLHttpRequest'
        },
        body: requestBody
    })
    .then(response => {
        console.log('Response status:', response.status, response.statusText);
        if (!response.ok) {
            if (response.status === 403) {
                throw new Error('Access denied - HR privileges required');
            } else if (response.status === 401) {
                throw new Error('Session expired - please log in again');
            } else {
                throw new Error(`HTTP error! status: ${response.status} - ${response.statusText}`);
            }
        }
        return response.json();
    })
    .then(data => {
        console.log('Chart update response:', data);
        if (data.success) {
            // Handle both string and object data formats
            let chartData = data.growthData;
            if (typeof chartData === 'string') {
                try {
                    chartData = JSON.parse(chartData);
                } catch (e) {
                    console.error('Error parsing chart data:', e);
                    chartData = [];
                }
            }
            updateChartData(chartData || []);
            // Only show success message if it's a manual update
            if (document.getElementById('growthPeriod').dataset.manualUpdate) {
                showAlert('Chart updated successfully', 'success');
                delete document.getElementById('growthPeriod').dataset.manualUpdate;
            }
        } else {
            console.error('Server returned error:', data.error);
            showAlert('Failed to load chart data: ' + (data.error || 'Unknown error'), 'danger');
        }
    })
    .catch(error => {
        console.error('Error updating chart:', error);
        
        // If it's an access error, suggest reloading the page
        if (error.message.includes('Access denied') || error.message.includes('Session expired')) {
            showAlert(error.message + '. <a href="javascript:window.location.reload()">Click here to reload the page</a>', 'warning');
        } else {
            showAlert('Error updating chart: ' + error.message, 'danger');
        }
    })
    .finally(() => {
        hideChartLoading(chartContainer);
    });
}

// Update chart data
function updateChartData(data) {
    if (!userGrowthChart || !data) return;
    
    const labels = data.map(item => {
        const date = new Date(item.period + '-01');
        return date.toLocaleDateString('en-US', { month: 'short', year: 'numeric' });
    });
    
    const counts = data.map(item => item.count || 0);
    
    userGrowthChart.data.labels = labels;
    userGrowthChart.data.datasets[0].data = counts;
    userGrowthChart.update('active');
    
    // Add a subtle flash effect to indicate update
    const chartContainer = document.querySelector('.chart-container');
    chartContainer.style.boxShadow = '0 0 20px rgba(102, 126, 234, 0.3)';
    setTimeout(() => {
        chartContainer.style.boxShadow = '';
    }, 1000);
}

// Show chart loading
function showChartLoading(container) {
    const loadingDiv = document.createElement('div');
    loadingDiv.className = 'loading-overlay';
    loadingDiv.innerHTML = `
        <div>
            <div class="hr-loading"></div>
            <p style="margin-top: 10px;">Updating chart...</p>
        </div>
    `;
    container.appendChild(loadingDiv);
}

// Hide chart loading
function hideChartLoading(container) {
    const loadingDiv = container.querySelector('.loading-overlay');
    if (loadingDiv) {
        loadingDiv.remove();
    }
}

// Refresh recent users
function refreshRecentUsers() {
    const recentUsersList = document.getElementById('recentUsersList');
    if (!recentUsersList) return;
    
    // Show loading
    recentUsersList.innerHTML = '<div class="loading-overlay"><div class="hr-loading"></div></div>';
    
    // Refresh page to get updated data
    window.location.reload();
}

// Animate stat cards
function animateStatCards() {
    const statNumbers = document.querySelectorAll('.hr-stat-number');
    
    statNumbers.forEach((element, index) => {
        const finalValue = parseInt(element.textContent) || 0;
        element.textContent = '0';
        
        // Animate number counting up
        setTimeout(() => {
            animateNumber(element, 0, finalValue, 1000);
        }, index * 200);
    });
}

// Animate number counting
function animateNumber(element, start, end, duration) {
    if (start === end) return;
    
    const range = end - start;
    const increment = range / (duration / 16);
    let current = start;
    
    const timer = setInterval(() => {
        current += increment;
        
        if (current >= end) {
            current = end;
            clearInterval(timer);
        }
        
        element.textContent = Math.floor(current);
    }, 16);
}

// Start auto refresh
function startAutoRefresh() {
    // Refresh dashboard stats every 5 minutes
    dashboardInterval = setInterval(() => {
        refreshDashboardStats();
    }, 300000); // 5 minutes
}

// Stop auto refresh
function stopAutoRefresh() {
    if (dashboardInterval) {
        clearInterval(dashboardInterval);
        dashboardInterval = null;
    }
}

// Refresh dashboard statistics
function refreshDashboardStats() {
    const contextPath = getContextPath();
    
    fetch(`${contextPath}/hr/dashboard`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'X-Requested-With': 'XMLHttpRequest'
        },
        body: 'action=refreshStats'
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    })
    .then(data => {
        if (data.success) {
            updateStatCards(data);
            updateSystemStatus();
        } else {
            console.error('Error refreshing stats:', data.error);
        }
    })
    .catch(error => {
        console.error('Error refreshing stats:', error);
    });
}

// Update stat cards
function updateStatCards(data) {
    const elements = {
        totalUsers: document.getElementById('totalUsers'),
        activeUsers: document.getElementById('activeUsers'),
        inactiveUsers: document.getElementById('inactiveUsers'),
        newUsersThisMonth: document.getElementById('newUsersThisMonth')
    };
    
    Object.keys(elements).forEach(key => {
        if (elements[key] && data[key] !== undefined) {
            const currentValue = parseInt(elements[key].textContent) || 0;
            const newValue = data[key];
            
            if (currentValue !== newValue) {
                animateNumber(elements[key], currentValue, newValue, 500);
            }
        }
    });
}

// Update system status
function updateSystemStatus() {
    const totalUsers = parseInt(document.getElementById('totalUsers').textContent) || 0;
    const activeUsers = parseInt(document.getElementById('activeUsers').textContent) || 0;
    
    const activeRate = totalUsers > 0 ? (activeUsers / totalUsers * 100) : 0;
    const statusIndicator = document.getElementById('systemStatusIndicator');
    const activeUserRate = document.getElementById('activeUserRate');
    const lastUpdate = document.getElementById('lastUpdate');
    
    // Update active user rate
    if (activeUserRate) {
        activeUserRate.textContent = activeRate.toFixed(1) + '%';
    }
    
    // Update system status
    if (statusIndicator) {
        statusIndicator.classList.remove('healthy', 'warning', 'critical');
        
        if (activeRate >= 80) {
            statusIndicator.classList.add('healthy');
            statusIndicator.innerHTML = '<i class="fas fa-check-circle"></i> Healthy';
        } else if (activeRate >= 50) {
            statusIndicator.classList.add('warning');
            statusIndicator.innerHTML = '<i class="fas fa-exclamation-triangle"></i> Warning';
        } else {
            statusIndicator.classList.add('critical');
            statusIndicator.innerHTML = '<i class="fas fa-times-circle"></i> Critical';
        }
    }
    
    // Update last update time
    if (lastUpdate) {
        lastUpdate.textContent = new Date().toLocaleString();
    }
}

// Initialize dashboard tooltips
function initDashboardTooltips() {
    // Add tooltips to stat cards
    const statCards = document.querySelectorAll('.hr-stat-card');
    
    statCards.forEach(card => {
        const label = card.querySelector('.hr-stat-label').textContent;
        
        let tooltipText = '';
        switch (label.toLowerCase()) {
            case 'total users':
                tooltipText = 'Total number of users in the system';
                break;
            case 'active users':
                tooltipText = 'Users with active status';
                break;
            case 'inactive users':
                tooltipText = 'Users with inactive status';
                break;
            case 'new this month':
                tooltipText = 'New users registered this month';
                break;
        }
        
        if (tooltipText) {
            card.setAttribute('data-tooltip', tooltipText);
        }
    });
}

// Test connection to server
function testConnection() {
    const contextPath = getContextPath();
    const testBtn = document.querySelector('button[onclick="Dashboard.testConnection()"]');
    const originalText = testBtn.innerHTML;
    
    console.log('Testing connection...');
    
    // Show loading state
    testBtn.disabled = true;
    testBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Testing...';
    
    fetch(`${contextPath}/hr/dashboard`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'X-Requested-With': 'XMLHttpRequest'
        },
        body: 'action=test'
    })
    .then(response => {
        console.log('Test response status:', response.status);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    })
    .then(data => {
        console.log('Test response:', data);
        if (data.success) {
            showAlert('Connection test successful: ' + data.message, 'success');
        } else {
            showAlert('Connection test failed: ' + (data.error || 'Unknown error'), 'warning');
        }
    })
    .catch(error => {
        console.error('Connection test error:', error);
        showAlert('Connection test error: ' + error.message, 'danger');
    })
    .finally(() => {
        // Restore button state
        testBtn.disabled = false;
        testBtn.innerHTML = originalText;
    });
}

// Export functions for global use
window.Dashboard = {
    init: initDashboard,
    updateGrowthChart: updateGrowthChart,
    refreshRecentUsers: refreshRecentUsers,
    refreshStats: refreshDashboardStats,
    testConnection: testConnection
};

// Get context path helper function
function getContextPath() {
    // Try multiple methods to get context path
    const metaContextPath = document.querySelector('meta[name="context-path"]')?.content;
    const scriptContextPath = window.contextPath;
    const pathFromLocation = window.location.pathname.split('/')[1];
    
    return metaContextPath || scriptContextPath || (pathFromLocation !== 'hr' ? `/${pathFromLocation}` : '');
}

// Show alert helper function
function showAlert(message, type) {
    // Remove any existing alerts with the same message to avoid duplicates
    const existingAlerts = document.querySelectorAll('.alert');
    existingAlerts.forEach(alert => {
        if (alert.textContent.includes(message.replace(/<[^>]*>/g, ''))) {
            alert.remove();
        }
    });
    
    // Create alert element
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" onclick="this.parentElement.remove()" aria-label="Close">&times;</button>
    `;
    
    // Insert at top of dashboard
    const dashboardContainer = document.querySelector('.dashboard-container');
    if (dashboardContainer) {
        dashboardContainer.insertBefore(alertDiv, dashboardContainer.firstChild);
        
        // Auto-remove after 8 seconds (longer for error messages)
        const autoRemoveTime = type === 'danger' ? 8000 : 5000;
        setTimeout(() => {
            if (alertDiv.parentNode) {
                alertDiv.remove();
            }
        }, autoRemoveTime);
    } else {
        // Fallback to console log if no dashboard container
        console.log(`Alert (${type}):`, message);
    }
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    // Set context path for AJAX requests
    window.contextPath = getContextPath();
    console.log('Context path set to:', window.contextPath);
    
    // Check if we're on the dashboard page
    if (document.querySelector('.dashboard-container')) {
        console.log('Dashboard page detected, waiting for data...');
    }
});

// Cleanup on page unload
window.addEventListener('beforeunload', function() {
    stopAutoRefresh();
    if (userGrowthChart) {
        userGrowthChart.destroy();
    }
});