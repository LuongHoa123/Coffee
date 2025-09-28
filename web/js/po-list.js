// PO List JavaScript functionality
document.addEventListener('DOMContentLoaded', function() {
    console.log('PO List page loaded');
    
    // Initialize page functionality
    initializeFilters();
    initializePagination();
    initializeTableFeatures();
    
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
            this.style.backgroundColor = '#e3f2fd';
        });
        
        row.addEventListener('mouseleave', function() {
            this.style.backgroundColor = '';
        });
    });
    
    // Format currency values
    formatCurrencyValues();
    
    // Format dates
    formatDateValues();
}

/**
 * Format currency values in the table
 */
function formatCurrencyValues() {
    const currencyCells = document.querySelectorAll('[data-currency]');
    
    currencyCells.forEach(cell => {
        const value = parseFloat(cell.textContent.replace(/[^0-9.-]/g, ''));
        if (!isNaN(value)) {
            cell.textContent = new Intl.NumberFormat('vi-VN', {
                style: 'currency',
                currency: 'VND'
            }).format(value);
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

// Global error handler for AJAX requests
window.addEventListener('error', function(e) {
    console.error('JavaScript error:', e.error);
    hideLoading();
});

// Handle page visibility change to refresh data if needed
document.addEventListener('visibilitychange', function() {
    if (!document.hidden) {
        console.log('Page became visible - could refresh data here');
    }
});