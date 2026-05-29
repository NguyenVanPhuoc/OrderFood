<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
	<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
		<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

			<!-- Lấy user từ Spring Security -->
			<sec:authentication property="principal" var="userPrincipal" />
			<c:set var="currentUserId" value="${userPrincipal.user.id}" />

			<!-- Thêm JS -->
			<script>
				// Khai báo global variables với giá trị mặc định
				window.APP_CONFIG = {
					currentUserId: ${not empty userPrincipal.user.id ? userPrincipal.user.id : 0 },
				orderStartTime: "${not empty store.orderStartTime ? store.orderStartTime : '08:00'}",
					orderEndTime: "${not empty store.orderEndTime ? store.orderEndTime : '17:00'}",
						storeId: ${not empty store.id ? store.id : 0 }
    };
			</script>

			<c:import url="/WEB-INF/views/layouts/assets-helper.jsp">
				<c:param name="js" value="/js/index.js" />
			</c:import>

			<h2 class="page-title">${store.name}</h2>
			<p>Địa chỉ: ${store.address}</p>

			<div class="flex-between mt-4">
				<div class="product-list">
					<h3 class="title">Sản phẩm</h3>
					<p class="order-time">Thời gian đặt hàng: ${store.orderStartTime}
						- ${store.orderEndTime}</p>

					<c:forEach var="product" items="${products}">
						<div class="product-item ${product.status == 2 ? 'status-disabled' : ''}" id="product-${product.id}"
							data-id="${product.id}" data-name="${product.name}" data-price="${product.price}">
							<img
								src="${pageContext.request.contextPath}/uploads/${not empty product.image ? product.image : '../images/food.jpg'}"
								alt="${product.name}" class="product-image">
							<div class="product-info">
								<div class="name-note">
									<div class="product-name">${product.name}</div>
									<p class="font-small">${product.description}</p>
								</div>
								<div class="product-price">${product.displayPrice}đ</div>
							</div>
							<button class="btn btn-add">+</button>
						</div>
					</c:forEach>
				</div>

				<div class="cart">
					<div class="detail-cart">
						<h3 class="title">Giỏ hàng</h3>
						<div id="cart-items">
							<div class="empty-cart">Giỏ hàng trống</div>
						</div>
						<div class="total-price">
							Tổng tiền: <span id="total">0</span> đ
						</div>
						<div class="d-flex justify-content-end">
							<button id="confirm-btn" class="confirm-btn">Xác nhận</button>
						</div>
					</div>
					<div class="ordered-products mt-4">
						<h3 class="title">Sản Phẩm Đã Đặt</h3>

						<table class="order-table">
							<thead>
								<tr>
									<th>Sản Phẩm</th>
									<th>Số Lượng</th>
									<th>Ghi Chú</th>
									<th>Người Đặt</th>
									<th>Thao tác</th>
								</tr>
							</thead>
							<tbody id="ordered-products-table">
								<c:set var="totalQuantity" value="0" />

							</tbody>
						</table>
					</div>
				</div>
			</div>