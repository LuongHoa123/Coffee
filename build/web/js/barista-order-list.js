/**
 * Barista Order List JavaScript
 * Interactive functionality for Order Management page
 */

// ================================
// Global Variables
// ================================
let orderListData = {
    currentPage: 1,
    pageSize: 20,
    totalCount: 0,
    totalPages: 0,
    filters: {
        shopID: 0,
        statusID: 0,
        search: '',
        sortBy: 'createdAt',
        sortOrder: 'DESC'
    }
};

// ================================
// DOM Ready Initialization
// ================================
document.addEventListener('DOMContentLoaded', function() {
    initializeOrderList();
    setupEventListeners();
    setupStatusUpdateEvents();
    setupPaginationEvents();
    startAutoRefresh();
});

// ================================
// Initialization Functions
// ================================
function initializeOrderList() {
    console.log('🚀 Order List initializing...');
    
    // Initialize filter form
    initializeFilters();
    
    // Initialize tooltips
    initializeTooltips();
    
    // Initialize status dropdowns
    initializeStatusDropdowns();
    
    // Initialize pagination data from URL
    initializePaginationData();
    
    console.log('✅ Order List initialized successfully');
}

function initializePaginationData() {
    // Get pagination info from URL parameters
    const urlParams = new URLSearchParams(window.location.search);
    const currentPage = parseInt(urlParams.get('page')) || 1;
    
    // Store pagination data globally for debugging
    window.paginationInfo = {
        currentPage: currentPage,
        baseUrl: window.location.pathname,
        urlParams: urlParams
    };
    
    console.log('Pagination initialized:', window.paginationInfo);
}

function initializeFilters() {
    const form = document.getElementById('filtersForm');
    if (form) {
        // Load current filter values from URL or form
        const formData = new FormData(form);
        orderListData.filters.shopID = parseInt(formData.get('shopID') || '0');
        orderListData.filters.statusID = parseInt(formData.get('statusID') || '0');
        orderListData.filters.search = formData.get('search') || '';
        orderListData.filters.sortBy = formData.get('sortBy') || 'createdAt';
        orderListData.filters.sortOrder = formData.get('sortOrder') || 'DESC';
    }
}

function initializeTooltips() {
    // Initialize tooltips for buttons
    const tooltipElements = document.querySelectorAll('[title]');
    tooltipElements.forEach(element => {
        element.addEventListener('mouseenter', showTooltip);
        element.addEventListener('mouseleave', hideTooltip);
    });
}

function initializeStatusDropdowns() {
    // Initialize status update dropdowns
    const statusDropdowns = document.querySelectorAll('.status-update-dropdown');
    statusDropdowns.forEach(dropdown => {
        const button = dropdown.querySelector('.btn-status-update');
        const menu = dropdown.querySelector('.status-dropdown-menu');
        
        if (button && menu) {
            button.addEventListener('click', function(e) {
                e.stopPropagation();
                toggleStatusDropdown(dropdown);
            });
        }
    });
    
    // Close dropdowns when clicking outside
    document.addEventListener('click', function() {
        closeAllStatusDropdowns();
    });
}

// ================================
// Event Listeners Setup
// ================================
function setupEventListeners() {
    // Filter form submission
    const filtersForm = document.getElementById('filtersForm');
    if (filtersForm) {
        filtersForm.addEventListener('submit', handleFilterSubmit);
    }
    
    // Search input with debounce
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        let searchTimeout;
        searchInput.addEventListener('input', function() {
            clearTimeout(searchTimeout);
            searchTimeout = setTimeout(handleSearchInput, 500);
        });
    }
    
    // Filter changes
    const filterSelects = document.querySelectorAll('#shopFilter, #statusFilter, #sortBy, #sortOrder');
    filterSelects.forEach(select => {
        select.addEventListener('change', handleFilterChange);
    });
    
    // Event delegation for dynamic elements
    setupEventDelegation();
    
    // Order row clicks for details
    const orderRows = document.querySelectorAll('.order-row');
    orderRows.forEach(row => {
        row.addEventListener('click', function(e) {
            // Don't trigger if clicking on action buttons
            if (!e.target.closest('.order-actions')) {
                const orderID = this.dataset.orderId;
                if (orderID) {
                    viewOrderDetails(orderID);
                }
            }
        });
    });
}

/**
 * Setup event delegation for dynamically added elements
 */
function setupEventDelegation() {
    // Event delegation for status option buttons
    document.addEventListener('click', function(e) {
        if (e.target.closest('.status-option')) {
            const button = e.target.closest('.status-option');
            const orderID = button.dataset.orderId;
            const statusID = button.dataset.statusId;
            const statusName = button.dataset.statusName;
            
            if (orderID && statusID && statusName) {
                updateOrderStatus(orderID, statusID, statusName);
            }
        }
        
        // Event delegation for pagination buttons
        if (e.target.closest('.pagination-btn')) {
            const button = e.target.closest('.pagination-btn');
            const page = button.dataset.page;
            
            if (page) {
                goToPage(parseInt(page));
            }
        }
        
        // Event delegation for view order buttons
        if (e.target.closest('.btn-view')) {
            const button = e.target.closest('.btn-view');
            const orderID = button.dataset.orderId;
            
            if (orderID) {
                viewOrderDetails(orderID);
            }
        }
    });
}
    
    // Modal close events
    const modal = document.getElementById('orderDetailsModal');
    if (modal) {
        modal.addEventListener('click', function(e) {
            if (e.target === modal) {
                closeOrderDetailsModal();
            }
        });
    }
    
    // Keyboard events
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            closeOrderDetailsModal();
            closeAllStatusDropdowns();
        }
    });


/**
 * Setup status update events using event delegation
 */
function setupStatusUpdateEvents() {
    console.log('Setting up status update events...');
}

/**
 * Setup pagination events using event delegation
 */
function setupPaginationEvents() {
    console.log('Setting up pagination events...');
}

// ================================
// Filter Functions
// ================================
function handleFilterSubmit(e) {
    e.preventDefault();
    applyFilters();
}

function handleFilterChange() {
    // Apply filters immediately when select changes
    setTimeout(applyFilters, 100);
}

function handleSearchInput() {
    applyFilters();
}

function applyFilters() {
    const form = document.getElementById('filtersForm');
    if (!form) return;
    
    // Update filter data
    const formData = new FormData(form);
    orderListData.filters.shopID = parseInt(formData.get('shopID') || '0');
    orderListData.filters.statusID = parseInt(formData.get('statusID') || '0');
    orderListData.filters.search = formData.get('search') || '';
    orderListData.filters.sortBy = formData.get('sortBy') || 'createdAt';
    orderListData.filters.sortOrder = formData.get('sortOrder') || 'DESC';
    
    // Reset to first page when filters change
    orderListData.currentPage = 1;
    
    // Reload page with new filters
    reloadOrderList();
}

function clearFilters() {
    // Reset form
    const form = document.getElementById('filtersForm');
    if (form) {
        form.reset();
    }
    
    // Reset filter data
    orderListData.filters = {
        shopID: 0,
        statusID: 0,
        search: '',
        sortBy: 'createdAt',
        sortOrder: 'DESC'
    };
    
    orderListData.currentPage = 1;
    
    // Reload page
    reloadOrderList();
}

function reloadOrderList() {
    const params = new URLSearchParams();
    
    // Add filters to URL
    Object.keys(orderListData.filters).forEach(key => {
        const value = orderListData.filters[key];
        if (value && value !== '0' && value !== '') {
            params.append(key, value);
        }
    });
    
    // Add pagination
    if (orderListData.currentPage > 1) {
        params.append('page', orderListData.currentPage);
    }
    
    if (orderListData.pageSize !== 20) {
        params.append('pageSize', orderListData.pageSize);
    }
    
    // Reload page with new parameters
    const url = window.location.pathname + (params.toString() ? '?' + params.toString() : '');
    window.location.href = url;
}

// ================================
// Pagination Functions
// ================================
function goToPage(page) {
    console.log('Going to page:', page);
    
    if (page < 1) {
        page = 1;
    }
    
    // Get current URL params
    const urlParams = new URLSearchParams(window.location.search);
    
    // Update page parameter
    if (page > 1) {
        urlParams.set('page', page);
    } else {
        urlParams.delete('page');
    }
    
    // Construct new URL
    const baseUrl = window.location.pathname;
    const queryString = urlParams.toString();
    const newUrl = baseUrl + (queryString ? '?' + queryString : '');
    
    // Navigate to new URL
    window.location.href = newUrl;
}

function nextPage() {
    if (orderListData.currentPage < orderListData.totalPages) {
        goToPage(orderListData.currentPage + 1);
    }
}

function previousPage() {
    if (orderListData.currentPage > 1) {
        goToPage(orderListData.currentPage - 1);
    }
}

// ================================
// Order Management Functions
// ================================
function viewOrderDetails(orderID) {
    if (!orderID) return;
    
    const modal = document.getElementById('orderDetailsModal');
    const content = document.getElementById('orderDetailsContent');
    
    if (!modal || !content) return;
    
    // Show modal with loading
    modal.style.display = 'block';
    content.innerHTML = `
        <div class="loading-spinner">
            <i class="fas fa-spinner fa-spin"></i>
            <p>Loading order details...</p>
        </div>
    `;
    
    // Fetch order details via AJAX
    fetchOrderDetails(orderID)
        .then(orderData => {
            displayOrderDetails(orderData, content);
        })
        .catch(error => {
            content.innerHTML = `
                <div class="error-message">
                    <i class="fas fa-exclamation-triangle"></i>
                    <p>Error loading order details: ${error.message}</p>
                    <button onclick="viewOrderDetails(${orderID})" class="retry-btn">
                        <i class="fas fa-redo"></i>
                        Retry
                    </button>
                </div>
            `;
        });
}

function fetchOrderDetails(orderID) {
    return new Promise((resolve, reject) => {
        // Mock data for now - replace with actual AJAX call
        setTimeout(() => {
            const mockOrderData = {
                orderID: orderID,
                shopName: 'CoffeeLux Central',
                status: 'Preparing',
                statusID: 22,
                customerName: 'Nguyen Van A',
                customerPhone: '0123456789',
                totalAmount: 67500,
                createdAt: '2024-09-21 10:30:00',
                items: [
                    {
                        productID: 1,
                        productName: 'Espresso',
                        quantity: 2,
                        price: 25000,
                        subtotal: 50000
                    },
                    {
                        productID: 2,
                        productName: 'Cappuccino',
                        quantity: 1,
                        price: 17500,
                        subtotal: 17500
                    }
                ]
            };
            
            resolve(mockOrderData);
        }, 1000);
    });
}

function displayOrderDetails(orderData, container) {
    const itemsHtml = orderData.items.map(item => `
        <tr>
            <td>${item.productName}</td>
            <td>${item.quantity}</td>
            <td>${formatCurrency(item.price)}</td>
            <td>${formatCurrency(item.subtotal)}</td>
        </tr>
    `).join('');
    
    container.innerHTML = `
        <div class="order-details">
            <div class="order-header">
                <div class="order-info">
                    <h4>Order #${orderData.orderID}</h4>
                    <p><i class="fas fa-store"></i> ${orderData.shopName}</p>
                    <p><i class="fas fa-user"></i> ${orderData.customerName}</p>
                    <p><i class="fas fa-phone"></i> ${orderData.customerPhone}</p>
                    <p><i class="fas fa-clock"></i> ${orderData.createdAt}</p>
                </div>
                <div class="order-status">
                    <span class="status-badge status-${orderData.status.toLowerCase()}">
                        ${orderData.status}
                    </span>
                </div>
            </div>
            
            <div class="order-items">
                <h5>Order Items</h5>
                <table class="items-table">
                    <thead>
                        <tr>
                            <th>Product</th>
                            <th>Quantity</th>
                            <th>Price</th>
                            <th>Subtotal</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${itemsHtml}
                    </tbody>
                </table>
            </div>
            
            <div class="order-total">
                <div class="total-row">
                    <span>Total Amount:</span>
                    <span class="total-amount">${formatCurrency(orderData.totalAmount)}</span>
                </div>
            </div>
            
            <div class="order-actions">
                <button class="btn-print" onclick="printOrderDetails()">
                    <i class="fas fa-print"></i>
                    Print
                </button>
                <button class="btn-close" onclick="closeOrderDetailsModal()">
                    <i class="fas fa-times"></i>
                    Close
                </button>
            </div>
        </div>
    `;
}

function closeOrderDetailsModal() {
    const modal = document.getElementById('orderDetailsModal');
    if (modal) {
        modal.style.display = 'none';
    }
}

// Valid status transitions mapping
const validStatusTransitions = {
    'Pending': ['Preparing', 'Cancelled'],
    'Preparing': ['Ready', 'Cancelled'],
    'Ready': ['Completed', 'Cancelled'],
    'Completed': [],
    'Cancelled': []
};

function isValidStatusTransition(currentStatus, newStatus) {
    const allowedTransitions = validStatusTransitions[currentStatus];
    return allowedTransitions && allowedTransitions.includes(newStatus);
}

function updateOrderStatus(orderID, statusID, statusName) {
    if (!orderID || !statusID) return;
    
    // Get current status from DOM
    const orderRow = document.querySelector(`tr[data-order-id="${orderID}"]`);
    if (!orderRow) {
        showNotification('Order not found', 'error');
        return;
    }
    
    const currentStatusBadge = orderRow.querySelector('.status-badge');
    const currentStatus = currentStatusBadge ? currentStatusBadge.textContent.trim() : '';
    
    // Validate status transition
    if (!isValidStatusTransition(currentStatus, statusName)) {
        showNotification(`Invalid status transition: Cannot change from "${currentStatus}" to "${statusName}"`, 'error');
        console.error('Invalid status transition:', { currentStatus, statusName });
        return;
    }
    
    if (!confirm(`Are you sure you want to change order #${orderID} status from "${currentStatus}" to "${statusName}"?`)) {
        return;
    }
    
    // Show loading state
    showStatusUpdateLoading(orderID);
    
    // Send AJAX request
    updateOrderStatusAjax(orderID, statusID)
        .then(result => {
            if (result.success) {
                showNotification('Order status updated successfully!', 'success');
                // Refresh the page after a short delay
                setTimeout(() => {
                    window.location.reload();
                }, 1000);
            } else {
                showNotification(result.message || 'Failed to update order status', 'error');
                hideStatusUpdateLoading(orderID);
            }
        })
        .catch(error => {
            showNotification('Error updating order status: ' + error.message, 'error');
            hideStatusUpdateLoading(orderID);
        });
    
    closeAllStatusDropdowns();
}

function updateOrderStatusAjax(orderID, statusID) {
    return new Promise((resolve, reject) => {
        // Mock AJAX call - replace with actual implementation
        setTimeout(() => {
            // Mock success response
            resolve({
                success: true,
                message: 'Order status updated successfully'
            });
        }, 1000);
    });
}

// ================================
// Status Dropdown Functions
// ================================
function toggleStatusDropdown(dropdown) {
    const menu = dropdown.querySelector('.status-dropdown-menu');
    const isVisible = menu.style.display === 'block';
    
    // Close all other dropdowns first
    closeAllStatusDropdowns();
    
    // Toggle current dropdown
    if (!isVisible) {
        menu.style.display = 'block';
    }
}

function closeAllStatusDropdowns() {
    const dropdownMenus = document.querySelectorAll('.status-dropdown-menu');
    dropdownMenus.forEach(menu => {
        menu.style.display = 'none';
    });
}

// ================================
// UI Helper Functions
// ================================
function showStatusUpdateLoading(orderID) {
    const row = document.querySelector(`[data-order-id="${orderID}"]`);
    if (row) {
        const actions = row.querySelector('.order-actions');
        if (actions) {
            actions.innerHTML = `
                <div class="status-updating">
                    <i class="fas fa-spinner fa-spin"></i>
                    Updating...
                </div>
            `;
        }
    }
}

function hideStatusUpdateLoading(orderID) {
    // Reload the row or restore original actions
    window.location.reload();
}

function showNotification(message, type = 'info') {
    // Create notification element
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.innerHTML = `
        <div class="notification-content">
            <i class="fas fa-${getNotificationIcon(type)}"></i>
            <span>${message}</span>
        </div>
        <button class="notification-close" onclick="this.parentElement.remove()">
            <i class="fas fa-times"></i>
        </button>
    `;
    
    // Add to page
    document.body.appendChild(notification);
    
    // Animate in
    setTimeout(() => {
        notification.classList.add('show');
    }, 100);
    
    // Auto remove after 5 seconds
    setTimeout(() => {
        if (notification.parentElement) {
            notification.classList.remove('show');
            setTimeout(() => {
                notification.remove();
            }, 300);
        }
    }, 5000);
}

function getNotificationIcon(type) {
    const icons = {
        'success': 'check-circle',
        'error': 'exclamation-circle',
        'warning': 'exclamation-triangle',
        'info': 'info-circle'
    };
    return icons[type] || 'info-circle';
}

function showTooltip(e) {
    const element = e.target;
    const title = element.getAttribute('title');
    
    if (!title) return;
    
    // Create tooltip
    const tooltip = document.createElement('div');
    tooltip.className = 'custom-tooltip';
    tooltip.textContent = title;
    document.body.appendChild(tooltip);
    
    // Position tooltip
    const rect = element.getBoundingClientRect();
    tooltip.style.left = rect.left + (rect.width / 2) + 'px';
    tooltip.style.top = (rect.top - tooltip.offsetHeight - 8) + 'px';
    
    // Store reference for cleanup
    element._tooltip = tooltip;
    
    // Remove original title to prevent browser tooltip
    element.removeAttribute('title');
    element._originalTitle = title;
}

function hideTooltip(e) {
    const element = e.target;
    
    if (element._tooltip) {
        element._tooltip.remove();
        element._tooltip = null;
    }
    
    if (element._originalTitle) {
        element.setAttribute('title', element._originalTitle);
        element._originalTitle = null;
    }
}

function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND',
        minimumFractionDigits: 0
    }).format(amount);
}

function printOrderDetails() {
    window.print();
}

// ================================
// Auto Refresh Functions
// ================================
function startAutoRefresh() {
    // Auto refresh every 30 seconds
    setInterval(refreshOrderList, 30000);
}

function refreshOrderList() {
    // Simple page reload for now
    // In production, you might want to use AJAX to refresh data
    console.log('Auto-refreshing order list...');
    
    // Add visual indicator
    const refreshBtn = document.querySelector('.barista-refresh-btn');
    if (refreshBtn) {
        refreshBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Refreshing...';
        
        setTimeout(() => {
            refreshBtn.innerHTML = '<i class="fas fa-sync-alt"></i> Refresh';
        }, 2000);
    }
}

// ================================
// Theme Functions
// ================================
function toggleTheme() {
    const body = document.body;
    const currentTheme = body.getAttribute('data-theme');
    const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
    
    body.setAttribute('data-theme', newTheme);
    localStorage.setItem('barista-theme', newTheme);
    
    // Update theme toggle icon
    const themeToggle = document.querySelector('.barista-theme-toggle i');
    if (themeToggle) {
        themeToggle.className = newTheme === 'dark' ? 'fas fa-sun' : 'fas fa-moon';
    }
}

// ================================
// Initialize Theme on Load
// ================================
(function initTheme() {
    const savedTheme = localStorage.getItem('barista-theme') || 'light';
    document.body.setAttribute('data-theme', savedTheme);
    
    const themeToggle = document.querySelector('.barista-theme-toggle i');
    if (themeToggle) {
        themeToggle.className = savedTheme === 'dark' ? 'fas fa-sun' : 'fas fa-moon';
    }
})();

// ================================
// Enhanced Pagination Display
// ================================
(function enhancePagination() {
    // Add order count info to table header
    const tableHeader = document.querySelector('.table-header h3');
    const tableCount = document.querySelector('.table-count');
    
    if (tableHeader && !tableCount) {
        // If no count exists, add it
        const ordersData = window.ordersData || { totalCount: 0 };
        const countSpan = document.createElement('span');
        countSpan.className = 'table-count';
        countSpan.textContent = `(${ordersData.totalCount} orders)`;
        tableHeader.appendChild(countSpan);
    }
    
    // Ensure pagination section is always visible for better UX
    const paginationSection = document.querySelector('.pagination-section');
    if (paginationSection) {
        paginationSection.style.display = 'flex';
    }
    
    // Add click handlers to pagination buttons to ensure they work
    const paginationBtns = document.querySelectorAll('.pagination-btn');
    paginationBtns.forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            
            // Get page number from onclick attribute or button text
            const onclickAttr = this.getAttribute('onclick');
            if (onclickAttr) {
                const pageMatch = onclickAttr.match(/goToPage\((\d+)\)/);
                if (pageMatch) {
                    const page = parseInt(pageMatch[1]);
                    console.log('Button clicked, going to page:', page);
                    goToPage(page);
                }
            }
        });
    });
    
    console.log('Found', paginationBtns.length, 'pagination buttons');
})();

// ================================
// Refresh Function for Header Button
// ================================
function refreshOrderList() {
    console.log('Refreshing order list...');
    
    // Show loading state
    const refreshBtn = document.querySelector('.barista-refresh-btn');
    if (refreshBtn) {
        const originalHtml = refreshBtn.innerHTML;
        refreshBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Refreshing...';
        refreshBtn.disabled = true;
        
        // Reload page after delay
        setTimeout(() => {
            window.location.reload();
        }, 1000);
    } else {
        // Fallback: just reload
        window.location.reload();
    }
}