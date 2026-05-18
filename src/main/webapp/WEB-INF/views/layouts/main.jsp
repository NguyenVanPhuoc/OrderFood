<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.springframework.security.core.Authentication"%>
<%@ page import="org.springframework.security.core.context.SecurityContextHolder"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>${pageTitle != null ? pageTitle : 'Admin Dashboard'}</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css">
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css">
    
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/users.css">

    <!-- Page Specific CSS Fragment -->
    <c:if test="${not empty requestScope.pageStyles}">
        <c:forEach items="${requestScope.pageStyles}" var="style">
            <link rel="stylesheet" href="${pageContext.request.contextPath}${style}">
        </c:forEach>
    </c:if>
</head>
<body class="flex">
    <!-- Sidebar cố định -->
    <div class="fixed top-0 left-0 h-screen w-64 bg-gray-800 text-white flex flex-col z-10">
        <%@ include file="/WEB-INF/views/layouts/sidebar.jsp" %>
    </div>

    <!-- Main Content -->
    <div class="ml-64 flex-1 p-6 overflow-y-auto h-screen">
        <%@ include file="/WEB-INF/views/layouts/header.jsp" %>

        <main>
            <jsp:include page="${contentPage}" />
        </main>
    </div>

    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>
    <script src="${pageContext.request.contextPath}/js/scripts.js"></script>
    <!-- CSRF: đọc cookie XSRF-TOKEN, tự động gắn vào mọi jQuery AJAX request -->
    <script>
    (function($) {
        function getCsrfToken() {
            var m = document.cookie.match(/(?:^|;\s*)XSRF-TOKEN=([^;]*)/);
            return m ? decodeURIComponent(m[1]) : null;
        }
        $(document).ajaxSend(function(e, xhr) {
            var token = getCsrfToken();
            if (token) xhr.setRequestHeader('X-XSRF-TOKEN', token);
        });
    })(jQuery);
    </script>
    <!-- Common Inline Scripts -->
    <script>
        function toggleMenu() {
            const menu = document.getElementById("logoutMenu");
            menu.classList.toggle("hidden");
        }
        document.addEventListener("click", function (e) {
            const avatar = document.getElementById("avatar");
            const menu = document.getElementById("logoutMenu");
            if (!avatar.contains(e.target) && !menu.contains(e.target)) {
                menu.classList.add("hidden");
            }
        });
        function toggleSubmenu(id) {
            const submenu = document.getElementById(id);
            if (submenu.style.display === "block") {
                submenu.style.display = "none";
            } else {
                submenu.style.display = "block";
            }
        }
        
        document.addEventListener("DOMContentLoaded", function () {
            flatpickr("#orderStartTime, #orderEndTime", {
                enableTime: true,
                noCalendar: true,
                dateFormat: "H:i",
                time_24hr: true,
                minuteIncrement: 5,
                defaultHour: 8 
            });
        });
    </script>
    <!-- Page Specific JS Fragment -->
    <c:if test="${not empty requestScope.pageScripts}">
        <c:forEach items="${requestScope.pageScripts}" var="script">
            <script src="${pageContext.request.contextPath}${script}"></script>
        </c:forEach>
    </c:if>
    
    <!-- Inline Scripts Fragment -->
    <c:if test="${not empty requestScope.inlineScripts}">
        <script>${requestScope.inlineScripts}</script>
    </c:if>
</body>
</html>
