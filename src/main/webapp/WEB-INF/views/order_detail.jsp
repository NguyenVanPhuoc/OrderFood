<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ include file="/WEB-INF/views/common/format-utils.jspf"%>
<fmt:setLocale value="vi_VN"/>

<style>
.od-page { font-family: inherit; }

.od-breadcrumb {
  display: flex; align-items: center; gap: 8px;
  font-size: 0.82rem; color: #9ca3af; margin-bottom: 22px;
}
.od-breadcrumb a { color: #16a34a; text-decoration: none; font-weight: 600; }
.od-breadcrumb a:hover { text-decoration: underline; }
.od-breadcrumb__sep { color: #d1d5db; font-size: 0.7rem; }

.od-header-card {
  background: #fff;
  border: 1px solid #e9f5ee;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 3px 14px rgba(0,0,0,0.06);
  margin-bottom: 20px;
}
.od-header-top {
  display: flex; align-items: center; justify-content: space-between; gap: 16px;
  padding: 20px 24px;
  background: linear-gradient(135deg, #f0fdf4 0%, #fafffe 100%);
  border-bottom: 1px solid #e9f5ee;
  flex-wrap: wrap;
}
.od-store-info { display: flex; align-items: center; gap: 14px; min-width: 0; }
.od-store-icon {
  width: 52px; height: 52px; border-radius: 16px; flex-shrink: 0;
  background: linear-gradient(135deg, #16a34a, #22c55e); color: #fff;
  display: flex; align-items: center; justify-content: center;
  font-size: 1.3rem; box-shadow: 0 6px 14px rgba(22,163,74,0.25);
}
.od-store-name { font-size: 1.15rem; font-weight: 800; color: #14532d; margin: 0 0 6px; }
.od-meta { display: flex; flex-wrap: wrap; gap: 14px; font-size: 0.8rem; color: #9ca3af; }
.od-meta-item { display: inline-flex; align-items: center; gap: 5px; }

.od-badge {
  display: inline-flex; align-items: center; gap: 6px;
  border-radius: 999px; padding: 8px 16px;
  font-size: 0.82rem; font-weight: 700; white-space: nowrap; flex-shrink: 0;
}
.od-badge--paid    { background: #dcfce7; color: #15803d; border: 1px solid #bbf7d0; }
.od-badge--unpaid  { background: #fef9c3; color: #92400e; border: 1px solid #fde68a; }
.od-badge--cancelled { background: #fee2e2; color: #991b1b; border: 1px solid #fca5a5; }

.od-items-card {
  background: #fff; border: 1px solid #e9f5ee;
  border-radius: 20px; overflow: hidden;
  box-shadow: 0 3px 14px rgba(0,0,0,0.06);
  margin-bottom: 20px;
}
.od-items-title {
  padding: 16px 24px; font-size: 0.95rem; font-weight: 700; color: #14532d;
  background: linear-gradient(135deg, #f0fdf4 0%, #fafffe 100%);
  border-bottom: 1px solid #e9f5ee;
  display: flex; align-items: center; gap: 8px;
}
.od-table-wrap { overflow-x: auto; }
.od-table { width: 100%; border-collapse: collapse; }
.od-table thead th {
  padding: 10px 16px; font-size: 0.72rem; font-weight: 700; color: #9ca3af;
  text-transform: uppercase; letter-spacing: 0.06em;
  border-bottom: 1.5px solid #f0fdf4; background: transparent; text-align: left;
}
.od-table thead th.text-end { text-align: right; }
.od-table thead th.text-center { text-align: center; }
.od-table tbody tr { border-bottom: 1px solid #f9fafb; transition: background 0.15s; }
.od-table tbody tr:last-child { border-bottom: none; }
.od-table tbody tr:hover { background: #fafffe; }
.od-table td { padding: 13px 16px; vertical-align: top; font-size: 0.88rem; color: #374151; }
.od-item-name { font-weight: 600; color: #1f2937; line-height: 1.4; }
.od-item-note {
  display: inline-flex; align-items: center; gap: 5px; margin-top: 6px;
  padding: 4px 9px; border-radius: 999px; background: #f0fdf4;
  color: #16a34a; font-size: 0.76rem; border: 1px solid #bbf7d0;
}
.od-qty {
  display: inline-flex; align-items: center; justify-content: center;
  min-width: 34px; height: 34px; border-radius: 10px;
  background: #f0fdf4; border: 1px solid #bbf7d0;
  font-weight: 700; color: #16a34a; font-size: 0.85rem;
}
.od-price { font-weight: 600; color: #374151; }
.od-subtotal { font-weight: 700; color: #15803d; }

.od-total-row {
  display: flex; align-items: center; justify-content: space-between;
  background: linear-gradient(135deg, #f0fdf4, #dcfce7);
  border-top: 2px solid #bbf7d0;
  padding: 16px 24px;
}
.od-total-label { font-size: 0.9rem; font-weight: 600; color: #374151; display: flex; align-items: center; gap: 8px; }
.od-total-label i { color: #16a34a; }
.od-total-amount { font-size: 1.2rem; font-weight: 800; color: #15803d; }

.od-actions { display: flex; gap: 12px; flex-wrap: wrap; margin-top: 4px; }
.od-btn {
  height: 44px; padding: 0 20px; border-radius: 12px;
  font-size: 0.88rem; font-weight: 700;
  display: inline-flex; align-items: center; gap: 8px;
  cursor: pointer; border: none; text-decoration: none !important;
  transition: all 0.2s; white-space: nowrap;
}
.od-btn--ghost {
  background: #fff; color: #6b7280 !important;
  border: 1.5px solid #e5e7eb;
}
.od-btn--ghost:hover { background: #f9fafb; color: #374151 !important; border-color: #d1d5db; }
.od-btn--danger {
  background: #fff; color: #dc2626 !important;
  border: 1.5px solid #fca5a5;
}
.od-btn--danger:hover { background: #fef2f2; border-color: #f87171; }

.od-alert {
  display: flex; align-items: center; gap: 10px;
  padding: 12px 16px; border-radius: 12px; margin-bottom: 16px;
  font-size: 0.88rem; font-weight: 600;
}
.od-alert--success { background: #dcfce7; color: #15803d; border: 1px solid #bbf7d0; }
.od-alert--error   { background: #fee2e2; color: #991b1b; border: 1px solid #fca5a5; }

@media (max-width: 640px) {
  .od-header-top { flex-direction: column; align-items: flex-start; }
  .od-total-row { flex-direction: column; align-items: flex-start; gap: 6px; }
  .od-actions { flex-direction: column; }
  .od-btn { width: 100%; justify-content: center; }
}
</style>

<div class="container py-4 od-page">

  <%-- Flash messages --%>
  <c:if test="${not empty successMessage}">
    <div class="od-alert od-alert--success">
      <i class="fas fa-circle-check"></i> ${successMessage}
    </div>
  </c:if>
  <c:if test="${not empty errorMessage}">
    <div class="od-alert od-alert--error">
      <i class="fas fa-circle-xmark"></i> ${errorMessage}
    </div>
  </c:if>

  <%-- Breadcrumb --%>
  <nav class="od-breadcrumb">
    <a href="${pageContext.request.contextPath}/"><i class="fas fa-home"></i> Trang chủ</a>
    <span class="od-breadcrumb__sep"><i class="fas fa-chevron-right"></i></span>
    <a href="${pageContext.request.contextPath}/user/orders/history">Lịch sử đơn hàng</a>
    <span class="od-breadcrumb__sep"><i class="fas fa-chevron-right"></i></span>
    <span>Đơn hàng #${order.id}</span>
  </nav>

  <%-- Header card --%>
  <div class="od-header-card">
    <div class="od-header-top">
      <div class="od-store-info">
        <div class="od-store-icon"><i class="fas fa-store"></i></div>
        <div>
          <div class="od-store-name"><c:out value="${order.store.name}"/></div>
          <div class="od-meta">
            <span class="od-meta-item">
              <i class="far fa-calendar-alt"></i>
              <fmt:formatDate value="${order.createdAtAsDate}" pattern="dd/MM/yyyy HH:mm"/>
            </span>
            <span class="od-meta-item">
              <i class="fas fa-hashtag"></i>
              Mã đơn ${order.id}
            </span>
          </div>
        </div>
      </div>
      <c:choose>
        <c:when test="${order.status == 'paid'}">
          <span class="od-badge od-badge--paid">
            <i class="fas fa-circle-check"></i> Đã thanh toán
          </span>
        </c:when>
        <c:when test="${order.status == 'cancelled'}">
          <span class="od-badge od-badge--cancelled">
            <i class="fas fa-circle-xmark"></i> Đã hủy
          </span>
        </c:when>
        <c:otherwise>
          <span class="od-badge od-badge--unpaid">
            <i class="fas fa-wallet"></i> Chưa thanh toán
          </span>
        </c:otherwise>
      </c:choose>
    </div>
  </div>

  <%-- Items card --%>
  <div class="od-items-card">
    <div class="od-items-title">
      <i class="fas fa-list-ul"></i>
      Danh sách sản phẩm
    </div>
    <div class="od-table-wrap">
      <table class="od-table">
        <thead>
          <tr>
            <th>Sản phẩm</th>
            <th class="text-center" style="width:80px">SL</th>
            <th class="text-end"    style="width:130px">Đơn giá</th>
            <th class="text-end"    style="width:150px">Thành tiền</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="item" items="${order.orderItems}">
            <tr>
              <td>
                <div class="od-item-name"><c:out value="${item.product.name}"/></div>
                <c:if test="${not empty item.note}">
                  <div class="od-item-note">
                    <i class="fas fa-note-sticky"></i>
                    <c:out value="${item.note}"/>
                  </div>
                </c:if>
              </td>
              <td class="text-center"><span class="od-qty">${item.quantity}</span></td>
              <td class="text-end od-price">${dotFormatter.format(item.price)}&nbsp;đ</td>
              <td class="text-end od-subtotal">${dotFormatter.format(item.quantity * item.price)}&nbsp;đ</td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>
    <div class="od-total-row">
      <div class="od-total-label">
        <i class="fas fa-calculator"></i>
        Tổng cộng
      </div>
      <div class="od-total-amount">${dotFormatter.format(order.totalPrice)}&nbsp;đ</div>
    </div>
  </div>

  <%-- Actions --%>
  <div class="od-actions">
    <a href="${pageContext.request.contextPath}/user/orders/history" class="od-btn od-btn--ghost">
      <i class="fas fa-arrow-left"></i> Quay lại lịch sử
    </a>
    <c:if test="${order.status == 'unpaid'}">
      <form action="${pageContext.request.contextPath}/user/orders/${order.id}/cancel" method="POST"
            onsubmit="return confirm('Bạn có chắc muốn hủy đơn hàng #${order.id} không?')">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <button type="submit" class="od-btn od-btn--danger">
          <i class="fas fa-times-circle"></i> Hủy đơn hàng
        </button>
      </form>
    </c:if>
  </div>

</div>
