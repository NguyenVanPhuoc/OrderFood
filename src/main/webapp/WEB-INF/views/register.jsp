<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng Ký</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }
        body {
            background: url(../images/b2.jpg) no-repeat fixed center / cover;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px 0;
        }
        .container {
            background-color: rgba(255,255,255,0.96);
            border-radius: 10px;
            box-shadow: 0 15px 25px rgba(0,0,0,0.2);
            padding: 36px 40px;
            width: 460px;
            max-width: 95%;
        }
        .logo { text-align: center; margin-bottom: 24px; }
        .logo h1 { color: #5a5a5a; font-size: 26px; font-weight: 600; }
        .form-group { margin-bottom: 18px; position: relative; }
        .form-group label { display: block; color: #5a5a5a; margin-bottom: 6px; font-weight: 500; font-size: 14px; }
        .form-group input {
            width: 100%; padding: 12px 15px; border: 1px solid #ddd;
            border-radius: 6px; font-size: 15px; outline: none; transition: all 0.3s;
        }
        .form-group input:focus { border-color: #28a745; box-shadow: 0 0 5px rgba(40,167,69,0.3); }
        .form-group i { position: absolute; right: 14px; top: 38px; color: #aaa; }
        .error-msg { color: #dc3545; font-size: 12px; margin-top: 4px; }
        .btn-register {
            background: linear-gradient(135deg, #218838, #28a745);
            color: white; border: none; padding: 13px;
            width: 100%; border-radius: 6px; font-size: 16px;
            font-weight: 600; cursor: pointer; margin-top: 4px; transition: all 0.3s;
        }
        .btn-register:hover { background: linear-gradient(135deg, #1e7e34, #218838); }
        .login-link { text-align: center; margin-top: 16px; font-size: 14px; color: #5a5a5a; }
        .login-link a { color: #28a745; text-decoration: none; font-weight: 600; }
        .login-link a:hover { text-decoration: underline; }
        .alert-success {
            background: #d4edda; border: 1px solid #c3e6cb; color: #155724;
            padding: 10px 14px; border-radius: 6px; margin-bottom: 16px; font-size: 14px;
        }
    </style>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
<div class="container">
    <div class="logo">
        <h1><span style="color:#218838; font-weight:700;">Bầu</span> Food</h1>
        <p style="color:#888; font-size:14px; margin-top:4px;">Tạo tài khoản mới</p>
    </div>

    <c:if test="${not empty successMessage}">
        <div class="alert-success">${successMessage}</div>
    </c:if>

    <form:form action="/register" method="post" modelAttribute="registerRequest">
        <div class="form-group">
            <label for="name">Họ và tên <span style="color:red">*</span></label>
            <form:input path="name" id="name" placeholder="Nhập họ và tên" cssClass=""/>
            <i class="fas fa-user"></i>
            <form:errors path="name" cssClass="error-msg"/>
        </div>
        <div class="form-group">
            <label for="email">Email <span style="color:red">*</span></label>
            <form:input path="email" id="email" type="email" placeholder="Nhập email của bạn" cssClass=""/>
            <i class="fas fa-envelope"></i>
            <form:errors path="email" cssClass="error-msg"/>
        </div>
        <div class="form-group">
            <label for="phone">Số điện thoại</label>
            <form:input path="phone" id="phone" placeholder="Nhập số điện thoại (tuỳ chọn)" cssClass=""/>
            <i class="fas fa-phone"></i>
            <form:errors path="phone" cssClass="error-msg"/>
        </div>
        <div class="form-group">
            <label for="password">Mật khẩu <span style="color:red">*</span></label>
            <form:password path="password" id="password" placeholder="Tối thiểu 6 ký tự" cssClass=""/>
            <i class="fas fa-lock"></i>
            <form:errors path="password" cssClass="error-msg"/>
        </div>
        <div class="form-group">
            <label for="confirmPassword">Xác nhận mật khẩu <span style="color:red">*</span></label>
            <form:password path="confirmPassword" id="confirmPassword" placeholder="Nhập lại mật khẩu" cssClass=""/>
            <i class="fas fa-lock"></i>
            <form:errors path="confirmPassword" cssClass="error-msg"/>
        </div>
        <button type="submit" class="btn-register">ĐĂNG KÝ</button>
    </form:form>

    <div class="login-link">
        Đã có tài khoản? <a href="/login">Đăng nhập ngay</a>
    </div>
</div>
</body>
</html>
