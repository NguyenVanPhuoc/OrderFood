<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ include file="/WEB-INF/views/common/format-utils.jspf"%>
<fmt:setLocale value="vi_VN"/>

<style>
.oh-status-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  border-radius: 999px;
  padding: 4px 12px;
  font-size: 0.78rem;
  font-weight: 700;
  white-space: nowrap;
}
.oh-status-badge--paid     { background: #dcfce7; color: #15803d; border: 1px solid #bbf7d0; }
.oh-status-badge--unpaid   { background: #fef9c3; color: #92400e; border: 1px solid #fde68a; }
.oh-status-badge--cancelled{ background: #fee2e2; color: #991b1b; border: 1px solid #fca5a5; }

.oh-filter-bar {
  display: flex;
  align-items: flex-end;
  flex-wrap: wrap;
  gap: 10px;
  background: #fff;
  border-radius: 12px;
  padding: 14px 18px;
  margin-bottom: 22px;
  box-shadow: var(--card-shadow);
}
.oh-filter-bar label {
  display: block;
  font-size: 0.82rem;
  font-weight: 700;
  color: #374151;
  margin-bottom: 6px;
}
.oh-filter-bar select {
  height: 40px;
  min-width: 200px;
  border-radius: 8px;
  border: 1.5px solid #d1fae5;
  background: #f0fdf4;
  color: #374151;
  font-size: 0.88rem;
  padding: 0 12px;
  outline: none;
}
.oh-filter-bar select:focus { border-color: #16a34a; }
.oh-btn-filter {
  height: 40px;
  padding: 0 18px;
  border-radius: 8px;
  font-size: 0.88rem;
  font-weight: 700;
  border: none;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 7px;
  text-decoration: none !important;
  transition: all 0.18s;
}
.oh-btn-filter--primary {
  background: linear-gradient(135deg, #16a34a, #22c55e);
  color: #fff !important;
  box-shadow: 0 4px 12px rgba(22,163,74,0.25);
}
.oh-btn-filter--primary:hover { background: linear-gradient(135deg, #15803d, #16a34a); }
.oh-btn-filter--ghost {
  background: #f9fafb;
  color: #6b7280 !important;
  border: 1.5px solid #e5e7eb;
}
.oh-btn-filter--ghost:hover { background: #f3f4f6; color: #374151 !important; }

.oh-pagi {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 6px;
  margin-top: 28px;
  flex-wrap: wrap;
}
.oh-pagi a, .oh-pagi span.oh-page-num {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 38px;
  height: 38px;
  border-radius: 9px;
  border: 1.5px solid #e5e7eb;
  font-size: 0.88rem;
  font-weight: 700;
  text-decoration: none !important;
  color: #374151 !important;
  transition: all 0.18s;
  padding: 0 6px;
}
.oh-pagi a:hover { background: #f0fdf4; border-color: #bbf7d0; color: #16a34a !important; }
.oh-pagi span.oh-page-num.active {
  background: #16a34a;
  border-color: transparent;
  color: #fff !important;
  box-shadow: 0 4px 10px rgba(22,163,74,0.28);
}

.oh-empty {
  text-align: center;
  padding: 60px 24px;
  background: #fff;
  border-radius: 16px;
  box-shadow: var(--card-shadow);
}
.oh-empty i { font-size: 2.5rem; color: #86efac; display: block; margin-bottom: 14px; }
.oh-empty-title { font-size: 1.15rem; font-weight: 800; color: #14532d; margin-bottom: 8px; }
.oh-empty p { color: #9ca3af; font-size: 0.9rem; margin-bottom: 20px; }
</style>

<div class="list-unpaid">

  <h3 class="title_unpaid">Lịch sử đơn hàng</h3>

  <%-- Filter --%>
  <form action="${pageContext.request.contextPath}/user/orders/history" method="GET" class="oh-filter-bar">
    <div>
      <label for="statusFilter">Trạng thái đơn hàng</label>
      <select id="statusFilter" name="status">
        <option value="">Tất cả</option>
        <option value="unpaid" ${status == 'unpaid' ? 'selected' : ''}>Chưa thanh toán</option>
        <option value="paid"   ${status == 'paid'   ? 'selected' : ''}>Đã thanh toán</option>
      </select>
    </div>
    <button type="submit" class="oh-btn-filter oh-btn-filter--primary">
      <i class="fas fa-sliders-h"></i> Lọc
    </button>
    <a href="${pageContext.request.contextPath}/user/orders/history" class="oh-btn-filter oh-btn-filter--ghost">
      <i class="fas fa-rotate-left"></i> Xóa lọc
    </a>
  </form>

  <%-- Order list --%>
  <c:choose>
    <c:when test="${not empty orders}">

      <c:forEach var="order" items="${orders}">
        <div class="order-card-unpaid">

          <div class="order-header">
            <div class="order-date">
              <fmt:formatDate value="${order.createdAtAsDate}" pattern="dd/MM/yyyy HH:mm"/>
              &nbsp;&mdash;&nbsp;<c:out value="${order.store.name}"/>
            </div>
            <c:choose>
              <c:when test="${order.status == 'paid'}">
                <span class="oh-status-badge oh-status-badge--paid">
                  <i class="fas fa-circle-check"></i> Đã thanh toán
                </span>
              </c:when>
              <c:when test="${order.status == 'cancelled'}">
                <span class="oh-status-badge oh-status-badge--cancelled">
                  <i class="fas fa-circle-xmark"></i> Đã hủy
                </span>
              </c:when>
              <c:otherwise>
                <span class="oh-status-badge oh-status-badge--unpaid">
                  <i class="fas fa-wallet"></i> Chưa thanh toán
                </span>
              </c:otherwise>
            </c:choose>
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
                  <td class="product-name text-left">
                    <c:out value="${item.product.name}"/>
                    <c:if test="${not empty item.note}">
                      <div style="font-size:0.78rem; color:#16a34a; margin-top:4px;">
                        <i class="fas fa-note-sticky"></i> <c:out value="${item.note}"/>
                      </div>
                    </c:if>
                  </td>
                  <td class="product-quantity text-center">
                    <span>${item.quantity}</span>
                  </td>
                  <td class="product-price text-right">
                    ${dotFormatter.format(item.quantity * item.price)} đ
                  </td>
                </tr>
              </c:forEach>
              <tr class="order-subtotal-row">
                <td colspan="2" class="order-subtotal-label">Tổng đơn hàng:</td>
                <td class="order-subtotal-amount">${dotFormatter.format(order.totalPrice)} đ</td>
              </tr>
            </tbody>
          </table>

        </div>
      </c:forEach>

      <%-- Pagination --%>
      <c:if test="${totalPages > 1}">
        <nav class="oh-pagi">
          <c:if test="${currentPage > 1}">
            <a href="?status=${status}&page=${currentPage - 1}&size=${size}">
              <i class="fas fa-chevron-left"></i>
            </a>
          </c:if>
          <c:forEach begin="1" end="${totalPages}" var="p">
            <span class="oh-page-num ${p == currentPage ? 'active' : ''}">
              <c:choose>
                <c:when test="${p == currentPage}">${p}</c:when>
                <c:otherwise>
                  <a href="?status=${status}&page=${p}&size=${size}" style="all:unset; cursor:pointer;">${p}</a>
                </c:otherwise>
              </c:choose>
            </span>
          </c:forEach>
          <c:if test="${currentPage < totalPages}">
            <a href="?status=${status}&page=${currentPage + 1}&size=${size}">
              <i class="fas fa-chevron-right"></i>
            </a>
          </c:if>
        </nav>
      </c:if>

    </c:when>
    <c:otherwise>
      <div class="oh-empty">
        <i class="fas fa-box-open"></i>
        <div class="oh-empty-title">Chưa có đơn hàng nào</div>
        <p>Lịch sử đặt món của bạn sẽ hiển thị tại đây sau khi tạo đơn đầu tiên.</p>
        <a href="${pageContext.request.contextPath}/" class="oh-btn-filter oh-btn-filter--primary">
          <i class="fas fa-utensils"></i> Đặt hàng ngay
        </a>
      </div>
    </c:otherwise>
  </c:choose>

</div>
