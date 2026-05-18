<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ include file="/WEB-INF/views/common/format-utils.jspf"%>

<style>
  .stat-card { border-radius: 12px; color: #fff; padding: 20px 24px; display:flex; justify-content:space-between; align-items:center; }
  .stat-card .stat-icon { font-size: 2.4rem; opacity: 0.8; }
  .stat-card .stat-value { font-size: 2rem; font-weight: 700; }
  .stat-card .stat-label { font-size: 0.85rem; opacity: 0.9; margin-top: 2px; }
  .card-orders   { background: linear-gradient(135deg,#4e73df,#224abe); }
  .card-revenue  { background: linear-gradient(135deg,#1cc88a,#13855c); }
  .card-users    { background: linear-gradient(135deg,#36b9cc,#258391); }
  .card-stores   { background: linear-gradient(135deg,#f6c23e,#dda20a); }
  .card-paid     { background: linear-gradient(135deg,#1cc88a,#13855c); }
  .card-unpaid   { background: linear-gradient(135deg,#e74a3b,#be2617); }
  .chart-card    { background:#fff; border-radius:12px; padding:20px; box-shadow:0 2px 12px rgba(0,0,0,0.07); }
</style>

<div class="mb-4">
  <h4 class="fw-bold"><i class="fas fa-tachometer-alt me-2 text-primary"></i>Tổng quan hệ thống</h4>
  <p class="text-muted mb-0">Thống kê tính đến hôm nay</p>
</div>

<!-- Stat Cards Row 1 -->
<div class="row g-3 mb-4">
  <div class="col-xl-3 col-md-6">
    <div class="stat-card card-orders shadow">
      <div>
        <div class="stat-value">${stats.totalOrders}</div>
        <div class="stat-label">Tổng đơn hàng</div>
      </div>
      <div class="stat-icon"><i class="fas fa-shopping-cart"></i></div>
    </div>
  </div>
  <div class="col-xl-3 col-md-6">
    <div class="stat-card card-revenue shadow">
      <div>
        <div class="stat-value">${dotFormatter.format(stats.totalRevenue)} đ</div>
        <div class="stat-label">Doanh thu (đã thanh toán)</div>
      </div>
      <div class="stat-icon"><i class="fas fa-money-bill-wave"></i></div>
    </div>
  </div>
  <div class="col-xl-3 col-md-6">
    <div class="stat-card card-users shadow">
      <div>
        <div class="stat-value">${stats.totalUsers}</div>
        <div class="stat-label">Người dùng</div>
      </div>
      <div class="stat-icon"><i class="fas fa-users"></i></div>
    </div>
  </div>
  <div class="col-xl-3 col-md-6">
    <div class="stat-card card-stores shadow">
      <div>
        <div class="stat-value">${stats.totalStores}</div>
        <div class="stat-label">Cửa hàng</div>
      </div>
      <div class="stat-icon"><i class="fas fa-store"></i></div>
    </div>
  </div>
</div>

<!-- Stat Cards Row 2 -->
<div class="row g-3 mb-4">
  <div class="col-xl-3 col-md-6">
    <div class="stat-card card-paid shadow">
      <div>
        <div class="stat-value">${stats.paidOrders}</div>
        <div class="stat-label">Đơn đã thanh toán</div>
      </div>
      <div class="stat-icon"><i class="fas fa-check-circle"></i></div>
    </div>
  </div>
  <div class="col-xl-3 col-md-6">
    <div class="stat-card card-unpaid shadow">
      <div>
        <div class="stat-value">${stats.unpaidOrders}</div>
        <div class="stat-label">Đơn chưa thanh toán</div>
      </div>
      <div class="stat-icon"><i class="fas fa-clock"></i></div>
    </div>
  </div>
</div>

<!-- Charts Row -->
<div class="row g-3">
  <div class="col-xl-8">
    <div class="chart-card shadow">
      <h6 class="fw-bold mb-3"><i class="fas fa-chart-bar me-2 text-primary"></i>Đơn hàng 7 ngày gần nhất</h6>
      <canvas id="ordersChart" height="120"></canvas>
    </div>
  </div>
  <div class="col-xl-4">
    <div class="chart-card shadow h-100">
      <h6 class="fw-bold mb-3"><i class="fas fa-fire me-2 text-danger"></i>Top sản phẩm bán chạy</h6>
      <c:choose>
        <c:when test="${not empty stats.topProductNames}">
          <table class="table table-sm table-hover">
            <thead class="table-light"><tr><th>#</th><th>Sản phẩm</th><th class="text-end">SL</th></tr></thead>
            <tbody>
              <c:forEach var="i" begin="0" end="${stats.topProductNames.size() - 1}">
                <tr>
                  <td><span class="badge bg-primary">${i + 1}</span></td>
                  <td><c:out value="${stats.topProductNames[i]}"/></td>
                  <td class="text-end fw-bold">${stats.topProductQuantities[i]}</td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </c:when>
        <c:otherwise>
          <p class="text-muted text-center py-4">Chưa có dữ liệu</p>
        </c:otherwise>
      </c:choose>
    </div>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
<script>
(function () {
  var labels = ${chartLabelsJson};
  var data   = ${chartDataJson};
  var ctx = document.getElementById('ordersChart').getContext('2d');
  new Chart(ctx, {
    type: 'bar',
    data: {
      labels: labels,
      datasets: [{
        label: 'Số đơn hàng',
        data: data,
        backgroundColor: 'rgba(78,115,223,0.7)',
        borderColor: 'rgba(78,115,223,1)',
        borderWidth: 1,
        borderRadius: 6
      }]
    },
    options: {
      responsive: true,
      plugins: { legend: { display: false } },
      scales: {
        y: { beginAtZero: true, ticks: { stepSize: 1, precision: 0 } }
      }
    }
  });
})();
</script>
