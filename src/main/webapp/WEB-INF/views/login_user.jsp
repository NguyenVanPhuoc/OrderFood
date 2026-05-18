<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng Nhập</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        
        body {
            background: url(../images/b2.jpg);
		    background-repeat: no-repeat;
		    background-attachment: fixed;
		    background-position: center;
		    background-size: cover;
		    -webkit-background-size: cover;
            height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        
        .container {
            background-color: rgba(255, 255, 255, 0.95);
            border-radius: 10px;
            box-shadow: 0 15px 25px rgba(0, 0, 0, 0.2);
            padding: 40px;
            width: 400px;
            max-width: 90%;
        }
        
        .logo {
            text-align: center;
            margin-bottom: 30px;
        }
        
        .logo h1 {
            color: #5a5a5a;
            font-size: 28px;
            font-weight: 600;
        }
        
        .logo span {
            color: #764ba2;
            font-weight: 700;
        }
        
        .form-group {
            margin-bottom: 25px;
            position: relative;
        }
        
        .form-group label {
            display: block;
            color: #5a5a5a;
            margin-bottom: 8px;
            font-weight: 500;
            font-size: 14px;
        }
        
        .form-group input {
            width: 100%;
            padding: 14px 15px;
            border: 1px solid #ddd;
            border-radius: 6px;
            font-size: 15px;
            transition: all 0.3s ease;
            outline: none;
        }
        
        .form-group input:focus {
            border-color: #764ba2;
            box-shadow: 0 0 5px rgba(118, 75, 162, 0.3);
        }
        
        .form-group i {
            position: absolute;
            right: 15px;
            top: 40px;
            color: #aaa;
        }

        .btn-login {
            background: linear-gradient(135deg, #218838, #28a745);
            color: white;
            border: none;
            padding: 14px;
            margin-bottom: 10px;
            width: 100%;
            border-radius: 6px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s;
        }

        .auth-links {
            margin-top: 18px;
            text-align: center;
            color: #5a5a5a;
            font-size: 14px;
        }

        .auth-links a {
            color: #218838;
            font-weight: 600;
            text-decoration: none;
        }

        .auth-links a:hover {
            text-decoration: underline;
        }
        
        pre#tokenOutput {
		    white-space: pre-wrap;
		    word-break: break-all;
		}
		.mb-t2 {
			margin-top: 5px;
			margin-bottom: 15px;
			color: red;
		}
    </style>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
    <div class="container">
        <div class="logo">
            <h1>ĐĂNG NHẬP</h1>
        </div>
        
        <form action="/login" method="post">
		    <input type="hidden" name="_csrf" value="${_csrf.token}" />
		    <div class="form-group">
		        <label for="email">Email</label>
		        <input type="email" id="email" name="username" placeholder="Nhập email của bạn" required>
		        <i class="fas fa-envelope"></i>
		    </div>
		    <div class="form-group">
		        <label for="password">Mật khẩu</label>
		        <input type="password" id="password" name="password" placeholder="Nhập mật khẩu của bạn" required>
		        <i class="fas fa-lock"></i>
			    <% if (request.getParameter("error") != null) { %>
				    <div class="danger-error mb-t2">Email hoặc Mật khẩu không đúng !</div>
				<% } %>
		    </div>
		    <button type="submit" class="btn-login">ĐĂNG NHẬP</button>
		</form>

        <div class="auth-links">
            Ch&#432;a c&#243; t&#224;i kho&#7843;n? <a href="${pageContext.request.contextPath}/register">&#272;&#259;ng k&#253; ngay</a>
        </div>

    </div>
</body>
</html>
