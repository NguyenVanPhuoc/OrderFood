<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<%@ include file="/WEB-INF/views/common/format-utils.jspf" %>

<c:if test="${not empty successMessage}">
	<div
		class="alert alert-success alert-dismissible fade show success-message"
		role="alert">
		${successMessage}
		<button type="button" class="btn-close" data-bs-dismiss="alert"
			aria-label="Close"></button>
	</div>
</c:if>
<div class="card">
	<div
		class="card-header d-flex justify-content-between align-items-center">
		<h2>
			<i class="fas fa-shopping-cart me-2"></i>Danh sách đơn hàng
		</h2>
		<div>
			<button type="button" class="btn btn-danger btn-sm me-2"
				id="deleteSelectedBtn" disabled>
				<i class="fas fa-trash"></i> Xóa đã chọn
			</button>
		</div>
	</div>
	<div class="card-body">
		<form action="${pageContext.request.contextPath}/admin/orders"
			method="GET" class="search-bar">
			<div class="row g-2 search-row align-items-end">
				<div class="col-lg-2 col-md-4 search-col">
					<label for="keyword" class="form-label">Tên cửa hàng</label> <input
						type="text" class="form-control" id="keyword" name="keyword"
						placeholder="Nhập tên cửa hàng" value="${keyword}" />
				</div>
				<div class="col-lg-2 col-md-4 search-col">
					<label for="status" class="form-label">Trạng thái</label> <select
						class="form-select" id="status" name="status">
						<option value="">Tất cả</option>
						<option value="paid" ${status == 'paid' ? 'selected' : ''}>Đã
							thanh toán</option>
						<option value="unpaid" ${status == 'unpaid' ? 'selected' : ''}>Chưa
							thanh toán</option>
					</select>
				</div>
				<div class="col-lg-2 col-md-4 search-col">
					<label for="userId" class="form-label">Khách hàng</label> <select
						class="form-select" id="userId" name="userId">
						<option value="">Tất cả</option>
						<c:forEach var="user" items="${users}">
							<option value="${user.id}" ${userId == user.id ? 'selected' : ''}>${user.name}</option>
						</c:forEach>
					</select>
				</div>
				<div class="col-lg-2 col-md-6 search-col">
					<label for="startDate" class="form-label">Từ ngày</label> <input
						type="date" class="form-control" id="startDate" name="startDate"
						value="${startDate}" />
				</div>
				<div class="col-lg-2 col-md-6 search-col">
					<label for="endDate" class="form-label">Đến ngày</label> <input
						type="date" class="form-control" id="endDate" name="endDate"
						value="${endDate}" />
				</div>
				<div class="col-lg-2 col-md-12 search-col">
					<button type="submit" class="btn btn-primary search-btn w-100">
						<i class="fas fa-search me-1"></i> Tìm kiếm
					</button>
				</div>
			</div>
		</form>

		<div class="table-responsive">
			<table class="table table-hover user-table">
				<thead>
					<tr>
						<th width="50"><input type="checkbox"
							class="form-check-input" id="selectAll"></th>
						<th>Mã đơn</th>
						<th>Khách hàng</th>
						<th>Cửa hàng</th>
						<th>Ngày đặt</th>
						<th>Trạng thái</th>
						<th>Tổng tiền</th>
						<th>Thao tác</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="order" items="${orders}">
						<tr>
							<td><input type="checkbox"
								class="form-check-input item-checkbox" value="${order.id}"
								data-status="${order.status}"></td>
							<td>${order.id}</td>
							<td>${order.user.name}</td>
							<td>${order.store.name}</td>
							<td><fmt:formatDate value="${order.createdAtAsDate}"
									pattern="dd/MM/yyyy HH:mm" /></td>
							<td><span
								class="badge ${order.status == 'paid' ? 'bg-success' : 'bg-warning'}"
								style="font-size: 0.9rem;"> ${order.status == 'paid' ? 'Đã thanh toán' : 'Chưa thanh toán'}
							</span> <c:if test="${order.status == 'unpaid'}">
									<button type="button" class="btn btn-sm btn-success ms-2 confirm-payment-btn"
										data-order-id="${order.id}">
										<i class="fas fa-check"></i> Xác nhận thanh toán
									</button>
								</c:if></td>
							<td class="product-price">${dotFormatter.format(order.totalPrice)} đ</td>
							<td>
								<button type="button" class="btn btn-info btn-sm view-detail-btn"
									data-order-id="${order.id}">
									<i class="fas fa-info-circle"></i>
								</button>
								<button type="button" class="btn btn-danger btn-sm ms-1 delete-btn"
									data-item-id="${order.id}">
									<i class="fas fa-trash"></i>
								</button>
							</td>
						</tr>
					</c:forEach>
					<c:if test="${empty orders}">
						<tr>
							<td colspan="8" style="text-align: center;">Không có dữ liệu</td>
						</tr>
					</c:if>
				</tbody>
			</table>
		</div>

		<!-- Modal chi tiết đơn hàng -->
		<div class="modal fade" id="orderDetailModal" tabindex="-1" aria-hidden="true">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<div class="modal-header">
						<h5 class="modal-title">Chi tiết đơn hàng #<span id="detailOrderId"></span></h5>
						<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
					</div>
					<div class="modal-body">
						<div class="row mb-3">
							<div class="col-md-6">
								<p><strong>Khách hàng:</strong> <span id="customerName"></span></p>
								<p><strong>Cửa hàng:</strong> <span id="storeName"></span></p>
							</div>
							<div class="col-md-6">
								<p><strong>Ngày đặt:</strong> <span id="orderDate"></span></p>
								<p><strong>Trạng thái:</strong> <span id="orderStatus"></span></p>
							</div>
						</div>
						<div class="table-responsive">
							<table class="table table-bordered">
								<thead class="table-light">
									<tr>
										<th>Sản phẩm</th>
										<th>Số lượng</th>
										<th>Đơn giá</th>
										<th>Thành tiền</th>
										<th>Ghi chú</th>
									</tr>
								</thead>
								<tbody id="orderItemsBody">
								</tbody>
								<tfoot>
									<tr>
										<td colspan="3" class="text-end"><strong>Tổng cộng:</strong></td>
										<td colspan="2"><strong><span id="totalPrice"></span></strong></td>
									</tr>
								</tfoot>
							</table>
						</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
					</div>
				</div>
			</div>
		</div>

		<!-- Modal xác nhận thanh toán -->
		<div class="modal fade" id="confirmPaymentModal" tabindex="-1" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<h5 class="modal-title">Xác nhận thanh toán</h5>
						<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
					</div>
					<div class="modal-body">
						<p>Bạn có chắc chắn xác nhận đơn hàng #<span id="paymentOrderId"></span> đã thanh toán không?</p>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
						<form id="confirmPaymentForm" action="" method="POST" style="display: inline;">
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
							<input type="hidden" name="status" value="paid" />
							<button type="submit" class="btn btn-success">Xác nhận</button>
						</form>
					</div>
				</div>
			</div>
		</div>

		<!-- Set variables for delete modal -->
		<c:set var="itemName" value="đơn hàng" />
		<c:set var="deleteUrl" value="/admin/orders/delete/" />
		<c:set var="deleteMultipleUrl" value="/admin/orders/delete-multiple" />
		<%@ include file="/WEB-INF/views/common/delete-modals.jsp"%>
		<input type="hidden" id="selectedItemIds" name="selectedItemIds" value="">

		<%@ include file="/WEB-INF/views/common/pagination.jsp"%>

	</div>
</div>

<!-- Thêm JS -->
<c:import url="/WEB-INF/views/layouts/assets-helper.jsp">
    <c:param name="js" value="/js/list-actions.js" />
</c:import>
<script>
// Hàm định dạng số tiền
function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN').format(amount) + ' đ';
}

document.addEventListener('DOMContentLoaded', function() {
    // Xử lý nút xác nhận thanh toán
    document.querySelectorAll('.confirm-payment-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            const orderId = this.dataset.orderId;
            const modal = document.getElementById('confirmPaymentModal');
            if (modal) {
                const orderIdSpan = modal.querySelector('#paymentOrderId');
                const form = modal.querySelector('#confirmPaymentForm');
                if (orderIdSpan) orderIdSpan.textContent = orderId;
                if (form) form.action = '${pageContext.request.contextPath}/admin/orders/' + orderId + '/status';
                new bootstrap.Modal(modal).show();
            } else {
                console.error('Payment confirmation modal not found!');
            }
        });
    });

    // Xử lý nút xem chi tiết
    document.querySelectorAll('.view-detail-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            const orderId = this.dataset.orderId;
            const modal = document.getElementById('orderDetailModal');
            if (modal) {
                new bootstrap.Modal(modal).show();
                document.getElementById('detailOrderId').textContent = orderId;
                const url = '${pageContext.request.contextPath}/admin/orders/' + orderId + '/items';
                fetch(url)
                    .then(response => {
                        console.log('Response status:', response.status);
                        if (!response.ok) {
                            throw new Error('Network response was not ok');
                        }
                        return response.json();
                    })
                    .then(data => {
                        console.log('Received data:', data);
                        // Cập nhật thông tin cơ bản
                        document.getElementById('customerName').textContent = data.customerName;
                        document.getElementById('storeName').textContent = data.storeName;
                        document.getElementById('orderDate').textContent = data.orderDate;
                        document.getElementById('orderStatus').textContent = data.status === 'paid' ? 'Đã thanh toán' : 'Chưa thanh toán';
                        document.getElementById('totalPrice').textContent = formatCurrency(data.totalPrice);

                        // Cập nhật danh sách sản phẩm
                        const tbody = document.getElementById('orderItemsBody');
                        tbody.innerHTML = '';
                        data.items.forEach(item => {
                            const row = document.createElement('tr');
                            const cells = [
                                document.createElement('td'),
                                document.createElement('td'),
                                document.createElement('td'),
                                document.createElement('td'),
                                document.createElement('td')
                            ];
                            
                            cells[0].textContent = item.productName;
                            cells[1].textContent = item.quantity;
                            cells[2].textContent = formatCurrency(item.price);
                            cells[3].textContent = formatCurrency(item.price * item.quantity);
                            cells[4].textContent = item.note || '-';
                            
                            cells.forEach(cell => row.appendChild(cell));
                            tbody.appendChild(row);
                        });
                    })
                    .catch(error => {
                        console.error('Error fetching order details:', error);
                        alert('Có lỗi xảy ra khi tải chi tiết đơn hàng');
                    });
            } else {
                console.error('Order detail modal not found!');
            }
        });
    });
});
</script>
