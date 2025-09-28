<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="context-path" content="${pageContext.request.contextPath}">
    <title>HR Dashboard - Management System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/hr-common.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <div class="hr-container">
        <!-- Sidebar -->
        <jsp:include page="/WEB-INF/views/hr/sidebar.jsp">
            <jsp:param name="page" value="${page}" />
        </jsp:include>

        <!-- Main Content -->
        <main class="hr-main-content">
            <header class="hr-header">
                <h1>HR Dashboard</h1>
                <div class="hr-user-info">
                    <div class="hr-user-avatar">
                        <i class="fas fa-user"></i>
                    </div>
                    <span>Welcome, ${sessionScope.user.fullName != null ? sessionScope.user.fullName : 'HR Manager'}</span>
                </div>
            </header>

            <div class="hr-content">
                <!-- Dashboard Content will be loaded here -->
                <jsp:include page="/WEB-INF/views/hr/${page != null ? page : 'dashboard'}.jsp" />
            </div>
        </main>
    </div>

    <script src="${pageContext.request.contextPath}/js/hr-common.js"></script>
</body>
</html>