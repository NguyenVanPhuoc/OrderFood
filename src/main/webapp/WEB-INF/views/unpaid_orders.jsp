<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
	<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
		<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
			<%@ include file="/WEB-INF/views/common/format-utils.jspf" %>

				<fmt:setLocale value="vi_VN" />
				<h3 class="title_unpaid">Hóa đơn chưa thanh toán</h3>
				<div class="list-unpaid">
					<c:forEach var="order" items="${orders}">
						<div class="order-card-unpaid">
							<div class="order-header">
								<div class="order-date">
									Ngày đặt hàng:
									<fmt:formatDate value="${order.createdAtAsDate}" pattern="dd/MM/yyyy HH:mm" />
								</div>
							</div>

							<table class="table">
								<thead>
									<tr>
										<th class="text-left">Sản phẩm</th>
										<th class="text-center">Số lượng</th>
										<th class="text-right">Giá</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="item" items="${order.orderItems}">
										<tr>
											<td class="product-name text-left">${item.product.name}</td>
											<td class="product-quantity text-center"><span>${item.quantity}</span></td>
											<td class="product-price text-right">${dotFormatter.format(item.quantity *
												item.price)} đ</td>
										</tr>
									</c:forEach>

									<!-- Tổng giá của order này -->
									<tr class="order-subtotal-row">
										<td colspan="2" class="order-subtotal-label">Tổng giá đơn
											hàng:</td>
										<td class="order-subtotal-amount">${dotFormatter.format(order.totalPrice)}
											đ</td>
									</tr>
								</tbody>
							</table>
						</div>
					</c:forEach>

					<c:choose>
						<c:when test="${not empty orders}">
							<div class="total-all-orders">
								<div class="total-all-label">Tổng cộng tất cả đơn hàng:</div>
								<div class="total-all-amount">
									${dotFormatter.format(totalPrice)} đ
								</div>
							</div>
						</c:when>
						<c:otherwise>
							<p>Không có đơn hàng nào.</p>
						</c:otherwise>
					</c:choose>

				</div>