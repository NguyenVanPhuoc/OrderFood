<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quên mật khẩu</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/popuo-box.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/main.css">
    <style>
        .success-message {
            color: #2e7d32;
            background: #e8f5e9;
            border: 1px solid #a5d6a7;
            padding: 10px;
            border-radius: 4px;
            margin-bottom: 10px;
            font-size: 14px;
        }
        .back-link {
            display: block;
            text-align: center;
            margin-top: 15px;
            color: #666;
            font-size: 13px;
        }
        .back-link a {
            color: #e74c3c;
            text-decoration: none;
        }
        .back-link a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>

    <h1>QUÊN MẬT KHẨU</h1>
    <div class="w3layoutscontaineragileits">
        <h2>Đặt lại mật khẩu</h2>
        <% if (request.getAttribute("message") != null) { %>
            <div class="success-message">${message}</div>
        <% } else { %>
            <form action="${pageContext.request.contextPath}/admin/forgot-password" method="post">
                <input type="email" name="email" placeholder="Nhập email của bạn" required>
                <% if (request.getAttribute("error") != null) { %>
                    <div class="danger-error">${error}</div>
                <% } %>
                <div class="aitssendbuttonw3ls">
                    <input type="submit" value="GỬI EMAIL">
                </div>
            </form>
        <% } %>
        <div class="back-link">
            <a href="${pageContext.request.contextPath}/admin/login">&#8592; Quay lại đăng nhập</a>
        </div>
    </div>
</body>
</html>
