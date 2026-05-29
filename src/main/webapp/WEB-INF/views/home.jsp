<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
  <%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>

    <h2 class="page-title">Danh sách cửa hàng</h2>

    <div class="restaurant-grid">
      <c:forEach var="store" items="${stores}">
        <a href="${pageContext.request.contextPath}/store/detail/${store.slug}" class="restaurant-card-link">
          <div class="restaurant-card" data-store-id="${store.id}">
            <div class="restaurant-image">
              <img
                src="${pageContext.request.contextPath}/uploads/${not empty store.image ? store.image : '../images/food.jpg'}"
                alt="${store.name}" class="store-image">
              <div class="restaurant-overlay">
                <span class="view-details">Xem chi tiết</span>
              </div>
            </div>
            <div class="restaurant-info">
              <h3 class="restaurant-name">${store.name}</h3>
              <p class="store-rating">
                Đánh giá: <span style="color: gold;">
                  <c:set var="fullStars" value="${store.rating - (store.rating % 1)}" />
                  <c:set var="hasHalfStar" value="${store.rating % 1 >= 0.25 && store.rating % 1 < 0.75}" />
                  <c:set var="emptyStars" value="${5 - fullStars - (hasHalfStar ? 1 : 0)}" />
                  <c:forEach begin="1" end="${fullStars}" var="i">
                    <i class="fas fa-star"></i>
                  </c:forEach>
                  <c:if test="${hasHalfStar}">
                    <i class="fas fa-star-half-alt"></i>
                  </c:if>
                  <c:forEach begin="1" end="${emptyStars}" var="i">
                    <i class="far fa-star"></i>
                  </c:forEach>
                </span>
                <c:if test="${store.rating != null}">
                  (${store.rating})
                </c:if>

              </p>
            </div>
          </div>
        </a>
      </c:forEach>
    </div>