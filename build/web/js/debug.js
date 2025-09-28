// Debug script để test context path
console.log('Debug Context Path:');
console.log('Current URL:', window.location.href);
console.log('Pathname:', window.location.pathname);
console.log('Context Path:', getContextPath());

// Test button clicks
function debugViewUser(userId) {
    const contextPath = getContextPath();
    const url = `${contextPath}/hr/user-details?id=${userId}`;
    console.log('ViewUser URL:', url);
    // window.location.href = url;
}

function debugEditUser(userId) {
    const contextPath = getContextPath();
    const url = `${contextPath}/hr/edit-user?id=${userId}`;
    console.log('EditUser URL:', url);
    // window.location.href = url;
}