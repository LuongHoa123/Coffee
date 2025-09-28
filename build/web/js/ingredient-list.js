// Ingredient List JavaScript functionality
document.addEventListener('DOMContentLoaded', function() {
    console.log('Ingredient List page loaded');
    
    // Initialize page functionality
    initializeFilters();
    initializePagination();
    initializeTableFeatures();
    initializeStockLevelIndicators();
    
    // Auto-submit form when filters change
    const filterForm = document.getElementById('filterForm');
    if (filterForm) {
        const selects = filterForm.querySelectorAll('select');
        const inputs = filterForm.querySelectorAll('input[type="text"]');
        
        selects.forEach(select => {
            select.addEventListener('change', function() {
                console.log('Filter changed:', this.name, '=', this.value);
                submitFilters();
            });
        });
        
        inputs.forEach(input => {
            input.addEventListener('input', debounce(function() {
                console.log('Search input:', this.name, '=', this.value);
                submitFilters();
            }, 500));
        });
    }
});

/**
 * Initialize filter functionality
 */
function initializeFilters() {
    const filterBtn = document.querySelector('.btn-filter');
    const clearBtn = document.querySelector('.btn-clear');
    
    if (filterBtn) {
        filterBtn.addEventListener('click', function(e) {
            e.preventDefault();
            submitFilters();
        });
    }
    
    if (clearBtn) {
        clearBtn.addEventListener('click', function(e) {
            e.preventDefault();
            clearFilters();
        });
    }
}

/**
 * Submit filters
 */
function submitFilters() {
    const form = document.getElementById('filterForm');
    if (form) {
        showLoading();
        form.submit();
    }
}

/**
 * Clear all filters
 */
function clearFilters() {
    const form = document.getElementById('filterForm');
    if (form) {
        // Reset all form fields
        form.reset();
        
        // Reset page to 1
        const pageInput = form.querySelector('input[name="page"]');
        if (pageInput) {
            pageInput.value = '1';
        }
        
        // Submit form to reload with no filters
        showLoading();
        form.submit();
    }
}

/**
 * Initialize pagination functionality
 */
function initializePagination() {
    const pageButtons = document.querySelectorAll('.page-btn');
    
    pageButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            
            const page = this.getAttribute('data-page');
            if (page && !this.classList.contains('active')) {
                goToPage(page);
            }
        });
    });
}

/**
 * Navigate to specific page
 */
function goToPage(page) {
    const form = document.getElementById('filterForm');
    if (form) {
        const pageInput = form.querySelector('input[name="page"]');
        if (pageInput) {
            pageInput.value = page;
        }
        
        showLoading();
        form.submit();
    }
}

/**
 * Initialize table features
 */
function initializeTableFeatures() {
    // Add hover effects and click handlers for table rows
    const tableRows = document.querySelectorAll('.data-table tbody tr');
    
    tableRows.forEach(row => {
        row.addEventListener('mouseenter', function() {
            this.style.backgroundColor = '#e8f5e8';
        });
        
        row.addEventListener('mouseleave', function() {
            this.style.backgroundColor = '';
        });
    });
    
    // Format quantity values
    formatQuantityValues();
    
    // Format dates
    formatDateValues();
}

/**
 * Initialize stock level indicators
 */
function initializeStockLevelIndicators() {
    const stockCells = document.querySelectorAll('[data-stock-level]');
    
    stockCells.forEach(cell => {
        const level = cell.getAttribute('data-stock-level');
        const quantity = parseFloat(cell.getAttribute('data-quantity')) || 0;
        
        // Add visual indicator
        const indicator = document.createElement('span');
        indicator.className = 'stock-indicator';
        
        switch(level) {
            case 'high':
                indicator.classList.add('stock-high');
                break;
            case 'normal':
                indicator.classList.add('stock-normal');
                break;
            case 'low':
                indicator.classList.add('stock-low');
                break;
            case 'out':
                indicator.classList.add('stock-out');
                break;
        }
        
        const stockLevelDiv = cell.querySelector('.stock-level');
        if (stockLevelDiv) {
            stockLevelDiv.prepend(indicator);
        }
    });
    
    // Update statistics
    updateStockStatistics();
}

/**
 * Format quantity values in the table
 */
function formatQuantityValues() {
    const quantityCells = document.querySelectorAll('[data-quantity]');
    
    quantityCells.forEach(cell => {
        const quantity = parseFloat(cell.getAttribute('data-quantity'));
        const unit = cell.getAttribute('data-unit') || '';
        
        if (!isNaN(quantity)) {
            const quantityDiv = cell.querySelector('.quantity-display');
            if (quantityDiv) {
                quantityDiv.innerHTML = `
                    ${formatNumber(quantity)} 
                    <span class="quantity-unit">${unit}</span>
                `;
            }
        }
    });
}

/**
 * Format date values in the table
 */
function formatDateValues() {
    const dateCells = document.querySelectorAll('[data-date]');
    
    dateCells.forEach(cell => {
        const dateStr = cell.textContent.trim();
        if (dateStr) {
            const date = new Date(dateStr);
            if (!isNaN(date.getTime())) {
                cell.textContent = date.toLocaleDateString('vi-VN', {
                    year: 'numeric',
                    month: '2-digit',
                    day: '2-digit'
                });
            }
        }
    });
}

/**
 * Update stock statistics in the stats cards
 */
function updateStockStatistics() {
    const stockLevels = document.querySelectorAll('[data-stock-level]');
    const stats = {
        total: stockLevels.length,
        high: 0,
        normal: 0,
        low: 0,
        out: 0
    };
    
    stockLevels.forEach(cell => {
        const level = cell.getAttribute('data-stock-level');
        if (stats.hasOwnProperty(level)) {
            stats[level]++;
        }
    });
    
    // Update stat cards if they exist
    updateStatCard('total', stats.total);
    updateStatCard('high', stats.high);
    updateStatCard('normal', stats.normal);
    updateStatCard('low', stats.low);
}

/**
 * Update individual stat card
 */
function updateStatCard(type, value) {
    const card = document.querySelector(`[data-stat="${type}"]`);
    if (card) {
        const numberElement = card.querySelector('.stat-number');
        if (numberElement) {
            numberElement.textContent = value;
        }
    }
}

/**
 * Show loading indicator
 */
function showLoading() {
    const container = document.querySelector('.data-table-container');
    if (container) {
        const loadingDiv = document.createElement('div');
        loadingDiv.className = 'loading';
        loadingDiv.innerHTML = `
            <div class="spinner"></div>
            <p>Đang tải dữ liệu...</p>
        `;
        
        // Store original content
        container.setAttribute('data-original-content', container.innerHTML);
        container.innerHTML = '';
        container.appendChild(loadingDiv);
    }
}

/**
 * Hide loading indicator
 */
function hideLoading() {
    const container = document.querySelector('.data-table-container');
    if (container) {
        const originalContent = container.getAttribute('data-original-content');
        if (originalContent) {
            container.innerHTML = originalContent;
            container.removeAttribute('data-original-content');
        }
    }
}

/**
 * Format numbers with proper thousand separators
 */
function formatNumber(number) {
    return new Intl.NumberFormat('vi-VN').format(number);
}

/**
 * Check stock level alerts
 */
function checkStockAlerts() {
    const lowStockItems = document.querySelectorAll('[data-stock-level="low"]');
    const outOfStockItems = document.querySelectorAll('[data-stock-level="out"]');
    
    if (outOfStockItems.length > 0) {
        console.warn(`${outOfStockItems.length} ingredients are out of stock`);
        // Could show notification here
    }
    
    if (lowStockItems.length > 0) {
        console.warn(`${lowStockItems.length} ingredients have low stock`);
        // Could show notification here
    }
}

/**
 * Debounce function to limit API calls
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
 * Utility function to get URL parameters
 */
function getUrlParameter(name) {
    name = name.replace(/[\[]/, '\\[').replace(/[\]]/, '\\]');
    const regex = new RegExp('[\\?&]' + name + '=([^&#]*)');
    const results = regex.exec(location.search);
    return results === null ? '' : decodeURIComponent(results[1].replace(/\+/g, ' '));
}

/**
 * Export table data to CSV (future enhancement)
 */
function exportToCSV() {
    console.log('Export to CSV functionality - to be implemented');
    // This could be enhanced to export the current filtered data
}

/**
 * Print table (future enhancement)
 */
function printTable() {
    console.log('Print table functionality - to be implemented');
    // This could be enhanced to print only the table content
}

/**
 * Show ingredient details in modal (future enhancement)
 */
function showIngredientDetails(ingredientId) {
    console.log('Show ingredient details for ID:', ingredientId);
    // This could be enhanced to show detailed ingredient information
}

// Global error handler for AJAX requests
window.addEventListener('error', function(e) {
    console.error('JavaScript error:', e.error);
    hideLoading();
});

// Handle page visibility change to refresh data if needed
document.addEventListener('visibilitychange', function() {
    if (!document.hidden) {
        console.log('Page became visible - could refresh data here');
        // Check for stock alerts when page becomes visible
        setTimeout(checkStockAlerts, 1000);
    }
});

// Initialize stock alerts on page load
setTimeout(checkStockAlerts, 2000);