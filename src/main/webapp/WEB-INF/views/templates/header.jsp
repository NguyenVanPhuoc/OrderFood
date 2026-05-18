<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
	<div class="container">
		<div class="d-flex justify-content-between align-items-center">
			<!-- Logo section with Bầu Food text -->
			<div class="logo-container">
				<a href="/" class="nav-link px-3">
					<h1 class="m-0 d-flex align-items-center">
						<!-- You can replace this with an actual logo image -->
						<span class="logo-text fw-bold" style="color: #28a745; font-size: 28px;">
							<span style="color: #218838;">Bầu</span> Food
						</span>
					</h1>
				</a>
			</div>

			<!-- Navigation menu - you can add this if needed -->
			<nav class="d-none d-lg-flex">
				<ul class="nav-menu d-flex list-unstyled m-0">
					<li class="nav-item"><a href="/" class="nav-link px-3">Thực Đơn</a></li>
					<li class="nav-item"><a href="/user/orders/history" class="nav-link px-3">Lịch Sử Đơn</a></li>
					<li class="nav-item"><a href="/user/orders/unpaid" class="nav-link px-3">Chưa Thanh Toán</a></li>
					<li class="nav-item"><a href="/contact" class="nav-link px-3">Liên Hệ</a></li>
					<li class="nav-item"><a href="/profile" class="nav-link px-3">Hồ sơ</a></li>
				</ul>
			</nav>

			<!-- User authentication section -->
			<div class="header-right">
				<sec:authorize access="isAuthenticated()">
					<div class="position-relative user-profile">
						<img src="${not empty principal.avatar ? '/uploads/' += principal.avatar : 'https://picsum.photos/40'}"
							alt="User Avatar" class="rounded-circle cursor-pointer user-avatar" onclick="toggleMenu()"
							id="avatar" />


						<div id="logoutMenu"
							class="position-absolute end-0 mt-2 bg-white border rounded shadow-lg d-none"
							style="width: 150px; z-index: 1000;">
							<a class="d-block px-4 py-2 text-decoration-none text-dark hover-bg-light"
								href="${pageContext.request.contextPath}/logout">Đăng Xuất</a>
						</div>
					</div>
				</sec:authorize>

				<sec:authorize access="isAnonymous()">
					<a class="btn btn-outline-success me-2" href="${pageContext.request.contextPath}/login">Đăng
						Nhập</a>
					<a class="btn btn-success" href="${pageContext.request.contextPath}/register">Đăng Ký</a>
				</sec:authorize>
			</div>
		</div>
	</div>
