<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ include file="/WEB-INF/views/common/format-utils.jspf"%>
<fmt:setLocale value="vi_VN"/>

<style>
/* =====================================================
   ORDER HISTORY — scoped với prefix .oh-
   Đảm bảo load bằng cách embed trực tiếp trong JSP
   ===================================================== */
.oh-page { font-family: inherit; }

/* ── Breadcrumb ── */
.oh-breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.82rem;
  color: #9ca3af;
  margin-bottom: 22px;
}
.oh-breadcrumb a {
  color: #16a34a;
  text-decoration: none;
  font-weight: 600;
}
.oh-breadcrumb a:hover { text-decoration: underline; }
.oh-breadcrumb__sep { color: #d1d5db; font-size: 0.7rem; }

/* ── Page Header ── */
.oh-page-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 28px;
}
.oh-page-header__eyebrow {
  display: inline-flex;
  align-items: center;
  gap: 7px;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.07em;
  text-transform: uppercase;
  color: #16a34a;
  background: #dcfce7;
  border: 1px solid #bbf7d0;
  border-radius: 999px;
  padding: 5px 12px;
  margin-bottom: 10px;
}
.oh-page-header__title {
  font-size: 1.85rem;
  font-weight: 800;
  color: #14532d;
  margin: 0 0 7px;
  line-height: 1.2;
}
.oh-page-header__sub {
  color: #6b7280;
  font-size: 0.9rem;
  margin: 0;
  line-height: 1.6;
  max-width: 480px;
}
.oh-btn-order {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  height: 44px;
  padding: 0 18px;
  border-radius: 12px;
  background: linear-gradient(135deg, #16a34a, #22c55e);
  color: #fff !important;
  font-size: 0.88rem;
  font-weight: 700;
  white-space: nowrap;
  text-decoration: none !important;
  box-shadow: 0 6px 16px rgba(22,163,74,0.28);
  border: none;
  transition: all 0.2s;
  flex-shrink: 0;
}
.oh-btn-order:hover {
  background: linear-gradient(135deg, #15803d, #16a34a);
  box-shadow: 0 8px 22px rgba(22,163,74,0.38);
  transform: translateY(-1px);
}

/* ── Stats Row ── */
.oh-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14px;
  margin-bottom: 22px;
}
.oh-stat {
  display: flex;
  align-items: center;
  gap: 14px;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 16px;
  padding: 15px 17px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  position: relative;
  overflow: hidden;
}
.oh-stat::before {
  content: '';
  position: absolute;
  left: 0; top: 0; bottom: 0;
  width: 4px;
  border-radius: 4px 0 0 4px;
}
.oh-stat--total::before { background: #16a34a; }
.oh-stat--page::before  { background: #3b82f6; }
.oh-stat--filter::before{ background: #f59e0b; }
.oh-stat__icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.05rem;
  flex-shrink: 0;
}
.oh-stat--total .oh-stat__icon { background: #dcfce7; color: #16a34a; }
.oh-stat--page  .oh-stat__icon { background: #dbeafe; color: #3b82f6; }
.oh-stat--filter .oh-stat__icon{ background: #fef3c7; color: #d97706; }
.oh-stat__label {
  display: block;
  font-size: 0.75rem;
  font-weight: 600;
  color: #9ca3af;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: 4px;
}
.oh-stat__value {
  display: block;
  font-size: 1.3rem;
  font-weight: 800;
  color: #111827;
  line-height: 1;
}
.oh-stat__value--sm { font-size: 1rem; }

/* ── Filter Bar ── */
.oh-filter {
  display: flex;
  align-items: flex-end;
  flex-wrap: wrap;
  gap: 12px;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 18px;
  padding: 18px 20px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.04);
  margin-bottom: 24px;
}
.oh-filter__label {
  display: block;
  font-size: 0.8rem;
  font-weight: 700;
  color: #374151;
  margin-bottom: 7px;
}
.oh-filter__select {
  height: 44px;
  min-width: 210px;
  border-radius: 12px;
  border: 1.5px solid #d1fae5;
  background: #f0fdf4;
  color: #374151;
  font-size: 0.9rem;
  padding: 0 14px;
  outline: none;
  -webkit-appearance: none;
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12'%3E%3Cpath fill='%239ca3af' d='M6 8L1 3h10z'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 14px center;
  padding-right: 36px;
}
.oh-filter__select:focus {
  border-color: #16a34a;
  box-shadow: 0 0 0 3px rgba(22,163,74,0.12);
}
.oh-filter__actions {
  display: flex;
  gap: 10px;
  margin-left: auto;
}
.oh-btn {
  height: 44px;
  padding: 0 20px;
  border-radius: 12px;
  font-size: 0.88rem;
  font-weight: 700;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  border: none;
  text-decoration: none !important;
  transition: all 0.2s;
  white-space: nowrap;
}
.oh-btn--primary {
  background: linear-gradient(135deg, #16a34a, #22c55e);
  color: #fff !important;
  box-shadow: 0 5px 14px rgba(22,163,74,0.28);
}
.oh-btn--primary:hover {
  background: linear-gradient(135deg, #15803d, #16a34a);
  box-shadow: 0 7px 18px rgba(22,163,74,0.36);
  transform: translateY(-1px);
  color: #fff !important;
}
.oh-btn--ghost {
  background: #fff;
  color: #6b7280 !important;
  border: 1.5px solid #e5e7eb;
}
.oh-btn--ghost:hover {
  background: #f9fafb;
  color: #374151 !important;
  border-color: #d1d5db;
}

/* ── Order Card ── */
.oh-card {
  background: #fff;
  border: 1px solid #e9f5ee;
  border-radius: 20px;
  overflow: hidden;
  margin-bottom: 16px;
  box-shadow: 0 3px 14px rgba(0,0,0,0.06);
  transition: box-shadow 0.22s, transform 0.22s;
}
.oh-card:hover {
  box-shadow: 0 8px 28px rgba(22,163,74,0.13);
  transform: translateY(-2px);
}
.oh-card__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  padding: 16px 20px;
  background: linear-gradient(135deg, #f0fdf4 0%, #fafffe 100%);
  border-bottom: 1px solid #e9f5ee;
}
.oh-card__store {
  display: flex;
  align-items: center;
  gap: 13px;
  min-width: 0;
}
.oh-card__icon {
  width: 46px;
  height: 46px;
  border-radius: 14px;
  background: linear-gradient(135deg, #16a34a, #22c55e);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.15rem;
  box-shadow: 0 6px 14px rgba(22,163,74,0.25);
  flex-shrink: 0;
}
.oh-card__name {
  font-size: 1rem;
  font-weight: 800;
  color: #14532d;
  margin: 0 0 5px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.oh-card__meta {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
  font-size: 0.8rem;
  color: #9ca3af;
}
.oh-card__meta-item {
  display: inline-flex;
  align-items: center;
  gap: 5px;
}
.oh-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  border-radius: 999px;
  padding: 7px 14px;
  font-size: 0.79rem;
  font-weight: 700;
  white-space: nowrap;
  flex-shrink: 0;
}
.oh-badge--paid {
  background: #dcfce7;
  color: #15803d;
  border: 1px solid #bbf7d0;
}
.oh-badge--unpaid {
  background: #fef9c3;
  color: #92400e;
  border: 1px solid #fde68a;
}
.oh-badge--cancelled {
  background: #fee2e2;
  color: #991b1b;
  border: 1px solid #fca5a5;
}
.oh-card__body { padding: 16px 20px 18px; }

/* ── Items Table ── */
.oh-table-wrap { overflow-x: auto; margin-bottom: 14px; }
.oh-table {
  width: 100%;
  border-collapse: collapse;
}
.oh-table thead th {
  padding: 8px 10px;
  font-size: 0.72rem;
  font-weight: 700;
  color: #9ca3af;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  border-bottom: 1.5px solid #f0fdf4;
  background: transparent;
}
.oh-table tbody tr {
  border-bottom: 1px solid #f9fafb;
  transition: background 0.15s;
}
.oh-table tbody tr:last-child { border-bottom: none; }
.oh-table tbody tr:hover { background: #fafffe; }
.oh-table td {
  padding: 11px 10px;
  vertical-align: top;
  font-size: 0.88rem;
  color: #374151;
}
.oh-item-name {
  font-weight: 600;
  color: #1f2937;
  line-height: 1.4;
}
.oh-item-note {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  margin-top: 6px;
  padding: 4px 9px;
  border-radius: 999px;
  background: #f0fdf4;
  color: #16a34a;
  font-size: 0.76rem;
  border: 1px solid #bbf7d0;
}
.oh-qty {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 34px;
  height: 34px;
  border-radius: 10px;
  background: #f0fdf4;
  border: 1px solid #bbf7d0;
  font-weight: 700;
  color: #16a34a;
  font-size: 0.85rem;
}
.oh-price {
  font-weight: 700;
  color: #15803d;
}

/* ── Order Total ── */
.oh-total {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: linear-gradient(135deg, #f0fdf4, #dcfce7);
  border: 1px solid #bbf7d0;
  border-radius: 14px;
  padding: 14px 16px;
}
.oh-total__label {
  font-size: 0.88rem;
  font-weight: 600;
  color: #374151;
  display: flex;
  align-items: center;
  gap: 8px;
}
.oh-total__label i { color: #16a34a; }
.oh-total__amount {
  font-size: 1.1rem;
  font-weight: 800;
  color: #15803d;
}

/* ── Empty State ── */
.oh-empty {
  text-align: center;
  padding: 64px 24px;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 24px;
  box-shadow: 0 3px 14px rgba(0,0,0,0.05);
}
.oh-empty__icon {
  width: 80px;
  height: 80px;
  border-radius: 24px;
  background: #f0fdf4;
  color: #16a34a;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  margin: 0 auto 20px;
}
.oh-empty__title {
  font-size: 1.2rem;
  font-weight: 800;
  color: #14532d;
  margin-bottom: 10px;
}
.oh-empty__sub {
  color: #9ca3af;
  font-size: 0.9rem;
  max-width: 360px;
  margin: 0 auto 24px;
  line-height: 1.6;
}

/* ── Pagination ── */
.oh-pagi {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  margin-top: 28px;
}
.oh-pagi__list {
  display: flex;
  align-items: center;
  gap: 6px;
  list-style: none;
  padding: 0;
  margin: 0;
}
.oh-pagi__link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 40px;
  height: 40px;
  border-radius: 11px;
  border: 1.5px solid #e5e7eb;
  background: #fff;
  color: #374151 !important;
  font-weight: 700;
  font-size: 0.88rem;
  text-decoration: none !important;
  transition: all 0.18s;
  padding: 0 8px;
}
.oh-pagi__link:hover {
  background: #f0fdf4;
  border-color: #bbf7d0;
  color: #16a34a !important;
}
.oh-pagi__item--active .oh-pagi__link {
  background: linear-gradient(135deg, #16a34a, #22c55e);
  border-color: transparent;
  color: #fff !important;
  box-shadow: 0 4px 12px rgba(22,163,74,0.3);
}
.oh-pagi__meta {
  font-size: 0.83rem;
  color: #9ca3af;
}
.oh-pagi__meta strong { color: #374151; }

/* ── Responsive ── */
@media (max-width: 640px) {
  .oh-page-header { flex-direction: column; align-items: flex-start; }
  .oh-page-header__title { font-size: 1.5rem; }
  .oh-stats { grid-template-columns: 1fr; }
  .oh-filter { flex-direction: column; align-items: stretch; }
  .oh-filter__select { min-width: unset; width: 100%; }
  .oh-filter__actions { margin-left: 0; }
  .oh-btn { width: 100%; justify-content: center; }
  .oh-card__head { flex-direction: column; align-items: flex-start; }
  .oh-total { flex-direction: column; align-items: flex-start; gap: 6px; }
}
@media (min-width: 641px) and (max-width: 900px) {
  .oh-stats { grid-template-columns: repeat(2, 1fr); }
}
</style>

<div class="container py-4 oh-page">

  <%-- ── Breadcrumb ── --%>
  <nav class="oh-breadcrumb">
    <a href="${pageContext.request.contextPath}/"><i class="fas fa-home"></i> Trang chủ</a>
    <span class="oh-breadcrumb__sep"><i class="fas fa-chevron-right"></i></span>
    <span>Lịch sử đơn hàng</span>
  </nav>

  <%-- ── Page Header ── --%>
  <div class="oh-page-header">
    <div>
      <div class="oh-page-header__eyebrow">
        <i class="fas fa-clock-rotate-left"></i>
        Theo dõi đơn hàng
      </div>
      <h1 class="oh-page-header__title">Lịch sử đơn hàng</h1>
      <p class="oh-page-header__sub">
        Xem lại các đơn đã đặt, theo dõi trạng thái thanh toán và kiểm tra tổng tiền từng đơn một cách rõ ràng.
      </p>
    </div>
    <a href="${pageContext.request.contextPath}/" class="oh-btn-order">
      <i class="fas fa-utensils"></i>
      Đặt thêm món
    </a>
  </div>

  <%-- ── Stats ── --%>
  <c:if test="${not empty orders}">
    <div class="oh-stats">
      <div class="oh-stat oh-stat--total">
        <div class="oh-stat__icon"><i class="fas fa-receipt"></i></div>
        <div>
          <span class="oh-stat__label">Tổng đơn</span>
          <span class="oh-stat__value">${totalItems}</span>
        </div>
      </div>
      <div class="oh-stat oh-stat--page">
        <div class="oh-stat__icon"><i class="fas fa-book-open"></i></div>
        <div>
          <span class="oh-stat__label">Trang</span>
          <span class="oh-stat__value oh-stat__value--sm">${currentPage} / ${totalPages}</span>
        </div>
      </div>
      <div class="oh-stat oh-stat--filter">
        <div class="oh-stat__icon"><i class="fas fa-filter"></i></div>
        <div>
          <span class="oh-stat__label">Bộ lọc</span>
          <span class="oh-stat__value oh-stat__value--sm">
            <c:choose>
              <c:when test="${status == 'paid'}">Đã thanh toán</c:when>
              <c:when test="${status == 'unpaid'}">Chưa thanh toán</c:when>
              <c:otherwise>Tất cả</c:otherwise>
            </c:choose>
          </span>
        </div>
      </div>
    </div>
  </c:if>

  <%-- ── Filter Bar ── --%>
  <form action="${pageContext.request.contextPath}/user/orders/history" method="GET" class="oh-filter">
    <div>
      <label class="oh-filter__label" for="statusFilter">Trạng thái đơn hàng</label>
      <select id="statusFilter" name="status" class="oh-filter__select">
        <option value="">Tất cả đơn hàng</option>
        <option value="unpaid" ${status == 'unpaid' ? 'selected' : ''}>Chưa thanh toán</option>
        <option value="paid"   ${status == 'paid'   ? 'selected' : ''}>Đã thanh toán</option>
      </select>
    </div>
    <div class="oh-filter__actions">
      <button type="submit" class="oh-btn oh-btn--primary">
        <i class="fas fa-sliders-h"></i> Lọc đơn
      </button>
      <a href="${pageContext.request.contextPath}/user/orders/history" class="oh-btn oh-btn--ghost">
        <i class="fas fa-rotate-left"></i> Xóa lọc
      </a>
    </div>
  </form>

  <%-- ── Order List / Empty ── --%>
  <c:choose>
    <c:when test="${not empty orders}">

      <c:forEach var="order" items="${orders}">
        <div class="oh-card">

          <%-- Header --%>
          <div class="oh-card__head">
            <div class="oh-card__store">
              <div class="oh-card__icon"><i class="fas fa-store"></i></div>
              <div>
                <div class="oh-card__name"><c:out value="${order.store.name}"/></div>
                <div class="oh-card__meta">
                  <span class="oh-card__meta-item">
                    <i class="far fa-calendar-alt"></i>
                    <fmt:formatDate value="${order.createdAtAsDate}" pattern="dd/MM/yyyy HH:mm"/>
                  </span>
                  <span class="oh-card__meta-item">
                    <i class="fas fa-hashtag"></i>
                    Mã đơn ${order.id}
                  </span>
                </div>
              </div>
            </div>
            <c:choose>
              <c:when test="${order.status == 'paid'}">
                <span class="oh-badge oh-badge--paid">
                  <i class="fas fa-circle-check"></i> Đã thanh toán
                </span>
              </c:when>
              <c:when test="${order.status == 'cancelled'}">
                <span class="oh-badge oh-badge--cancelled">
                  <i class="fas fa-circle-xmark"></i> Đã hủy
                </span>
              </c:when>
              <c:otherwise>
                <span class="oh-badge oh-badge--unpaid">
                  <i class="fas fa-wallet"></i> Chưa thanh toán
                </span>
              </c:otherwise>
            </c:choose>
          </div>

          <%-- Body --%>
          <div class="oh-card__body">
            <div class="oh-table-wrap">
              <table class="oh-table">
                <thead>
                  <tr>
                    <th>Sản phẩm</th>
                    <th class="text-center" style="width:80px">SL</th>
                    <th class="text-end"    style="width:140px">Thành tiền</th>
                  </tr>
                </thead>
                <tbody>
                  <c:forEach var="item" items="${order.orderItems}">
                    <tr>
                      <td>
                        <div class="oh-item-name"><c:out value="${item.product.name}"/></div>
                        <c:if test="${not empty item.note}">
                          <div class="oh-item-note">
                            <i class="fas fa-note-sticky"></i>
                            <c:out value="${item.note}"/>
                          </div>
                        </c:if>
                      </td>
                      <td class="text-center">
                        <span class="oh-qty">${item.quantity}</span>
                      </td>
                      <td class="text-end">
                        <span class="oh-price">${dotFormatter.format(item.quantity * item.price)}&nbsp;đ</span>
                      </td>
                    </tr>
                  </c:forEach>
                </tbody>
              </table>
            </div>

            <div class="oh-total">
              <div class="oh-total__label">
                <i class="fas fa-calculator"></i>
                Tổng cộng đơn hàng
              </div>
              <div class="oh-total__amount">
                ${dotFormatter.format(order.totalPrice)}&nbsp;đ
              </div>
            </div>

            <div style="display:flex; justify-content:flex-end; margin-top:12px;">
              <a href="${pageContext.request.contextPath}/user/orders/${order.id}"
                 class="oh-btn oh-btn--ghost" style="font-size:0.82rem; height:36px; padding:0 14px;">
                <i class="fas fa-eye"></i>&nbsp;Xem chi tiết
              </a>
            </div>
          </div>

        </div><%-- /oh-card --%>
      </c:forEach>

      <%-- ── Pagination ── --%>
      <c:if test="${totalPages > 1}">
        <nav class="oh-pagi">
          <ul class="oh-pagi__list">
            <c:if test="${currentPage > 1}">
              <li>
                <a class="oh-pagi__link" href="?status=${status}&page=${currentPage - 1}&size=${size}">
                  <i class="fas fa-chevron-left"></i>
                </a>
              </li>
            </c:if>
            <c:forEach begin="1" end="${totalPages}" var="p">
              <li class="oh-pagi__item ${p == currentPage ? 'oh-pagi__item--active' : ''}">
                <a class="oh-pagi__link" href="?status=${status}&page=${p}&size=${size}">${p}</a>
              </li>
            </c:forEach>
            <c:if test="${currentPage < totalPages}">
              <li>
                <a class="oh-pagi__link" href="?status=${status}&page=${currentPage + 1}&size=${size}">
                  <i class="fas fa-chevron-right"></i>
                </a>
              </li>
            </c:if>
          </ul>
          <div class="oh-pagi__meta">
            Tổng <strong>${totalItems}</strong> đơn &mdash;
            trang <strong>${currentPage}</strong> / <strong>${totalPages}</strong>
          </div>
        </nav>
      </c:if>

    </c:when>
    <c:otherwise>
      <div class="oh-empty">
        <div class="oh-empty__icon"><i class="fas fa-box-open"></i></div>
        <div class="oh-empty__title">Chưa có đơn hàng nào</div>
        <p class="oh-empty__sub">
          Lịch sử đặt món của bạn sẽ hiển thị tại đây sau khi bạn tạo đơn đầu tiên.
        </p>
        <a href="${pageContext.request.contextPath}/" class="oh-btn oh-btn--primary">
          <i class="fas fa-utensils"></i> Đặt hàng ngay
        </a>
      </div>
    </c:otherwise>
  </c:choose>

</div>
