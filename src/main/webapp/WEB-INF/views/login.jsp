<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/popuo-box.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>

    <h1>LOGIN FORM</h1>
	<div class="w3layoutscontaineragileits">
		<h2>Login</h2>
		<form action="${pageContext.request.contextPath}/admin/login" method="post">
			<input type="email" name="username" placeholder="Email" required>
            <input type="password" name="password" placeholder="Password" required>
			<% if (request.getParameter("error") != null) { %>
			    <div class="danger-error">Email or Password is incorrect!</div>
			<% } %>
			<ul class="agileinfotickwthree">
				<li>
					<input type="checkbox" id="brand1" name="remember-me" value="on">
					<label for="brand1"><span></span>Remember me</label>
					<a href="${pageContext.request.contextPath}/admin/forgot-password">Forgot password?</a>
				</li>
			</ul>
			<div class="aitssendbuttonw3ls">
				<input type="submit" value="LOGIN">
			</div>
		</form>
	</div>
</body>
</html>
