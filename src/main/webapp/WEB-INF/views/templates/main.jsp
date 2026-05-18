<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title><c:out value="${pageTitle}" /></title>
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
  <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
  <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/home.css">
  <!-- Page Specific CSS Fragment -->
    <c:if test="${not empty requestScope.pageStyles}">
        <c:forEach items="${requestScope.pageStyles}" var="style">
            <link rel="stylesheet" href="${pageContext.request.contextPath}${style}">
        </c:forEach>
    </c:if>
</head>
<body>
	<header class="header bg-white shadow-sm py-3">
	  <%@ include file="/WEB-INF/views/templates/header.jsp" %>
	</header>
	<div class="content container">
        <main>
            <jsp:include page="${contentPage}" />
        </main>
    </div>
    <footer class="footer bg-success bg-opacity-10 py-4 mt-5 border-top border-success border-opacity-25">
        <%@ include file="/WEB-INF/views/templates/footer.jsp" %>
    </footer>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/js/bootstrap.bundle.min.js"></script>
    <script>
    /* CSRF: đọc cookie XSRF-TOKEN, tự động gắn vào mọi jQuery AJAX request */
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
    <script>
    function toggleMenu() {
        const menu = document.getElementById('logoutMenu');
        menu.classList.toggle('d-none');
    }

    document.addEventListener('click', function(event) {
        const avatar = document.getElementById('avatar');
        const menu = document.getElementById('logoutMenu');
        if (avatar && menu && !avatar.contains(event.target) && !menu.contains(event.target)) {
        menu.classList.add('d-none');
        }
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
