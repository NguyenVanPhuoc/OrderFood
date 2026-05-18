<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:if test="${not empty successMessage}">
    <div class="alert alert-success alert-dismissible fade show success-message" role="alert">
        ${successMessage}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>

<div class="card">
    <div class="card-header d-flex justify-content-between align-items-center">
        <h2><i class="fas fa-users me-2"></i>Danh sách sản phẩm</h2>
        <div>
            <button type="button" class="btn btn-danger btn-sm me-2" id="deleteSelectedBtn" disabled>
                <i class="fas fa-trash me-1"></i>Xóa đã chọn
            </button>
            <a href="${pageContext.request.contextPath}/admin/stores/${storeId}/products/create" class="btn btn-primary btn-sm">
                <i class="fas fa-plus me-1"></i>Thêm mới
            </a>
        </div>
    </div>
     <form action="${pageContext.request.contextPath}/admin/stores/${storeId}/products" method="GET" class="search-bar">
         <input type="text" name="keyword" class="input-key" placeholder="Tìm kiếm" value="${keyword}" />
         <select name="status" class="select-status">
	        <option value="">Tất cả</option>
	        <option value="1" ${status == 1 ? 'selected' : ''}>Còn hàng</option>
	        <option value="2" ${status == 2 ? 'selected' : ''}>Hết hàng</option>
	    </select>
         <input type="hidden" name="sortBy" value="${sortBy}" />
         <input type="hidden" name="sortDir" value="${sortDir}" />
         <button type="submit" class="btn btn-primary">Search</button>
     </form>

    <div class="card-body">
        <div class="table-responsive">
            <table class="table table-hover user-table">
				<thead>
				    <tr>
				        <th>
                            <input type="checkbox" id="selectAll" class="form-check-input">
                        </th>
				        <th>
				            <a href="?page=1&sortBy=id&sortDir=${sortBy == 'id' && sortDir == 'asc' ? 'desc' : 'asc'}&keyword=${keyword}&status=${status}">
				                ID <i class="fas ${sortBy == 'id' ? (sortDir == 'asc' ? 'fa-sort-up' : 'fa-sort-down') : 'fa-sort'}"></i>
				            </a>
				        </th>
				        <th>
				            <a href="?page=1&sortBy=name&sortDir=${sortBy == 'name' && sortDir == 'asc' ? 'desc' : 'asc'}&keyword=${keyword}&status=${status}">
				                Tên sản phẩm <i class="fas ${sortBy == 'name' ? (sortDir == 'asc' ? 'fa-sort-up' : 'fa-sort-down') : 'fa-sort'}"></i>
				            </a>
				        </th>
				        <th>
				            <a href="?page=1&sortBy=price&sortDir=${sortBy == 'price' && sortDir == 'asc' ? 'desc' : 'asc'}&keyword=${keyword}&status=${status}">
				                Giá sản phẩm <i class="fas ${sortBy == 'price' ? (sortDir == 'asc' ? 'fa-sort-up' : 'fa-sort-down') : 'fa-sort'}"></i>
				            </a>
				        </th>
				        <th>Mô tả</th>
				        <th>Trạng thái</th>
				        <th>Hành động</th>
				    </tr>
				</thead>

                <tbody>
                    <c:forEach var="product" items="${products}">
			            <tr>
                            <td>
                                <input type="checkbox" class="form-check-input item-checkbox" value="${product.id}">
                            </td>
			                <td>${product.id}</td>
			                <td>${product.name}</td>
			                <td>${product.displayPrice}</td>
			                <td>${product.description}</td>
			                <td>${product.statusName}</td>
			                <td class="action-buttons">
                                <a href="${pageContext.request.contextPath}/admin/stores/${storeId}/products/edit/${product.id}" class="btn-edit btn btn-primary" title="Sửa">
                                    <i class="fas fa-edit"></i>
                                </a>
                                <button type="button" class="btn btn-danger btn-sm ms-1 delete-btn" data-item-id="${product.id}" title="Xóa">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </td>
			            </tr>
			        </c:forEach>
	                <c:if test="${empty products}">
	                    <tr>
	                        <td colspan="8" style="text-align: center;">Không có dữ liệu</td>
	                    </tr>
	                </c:if>
                </tbody>
            </table>
        </div>
		<%@ include file="/WEB-INF/views/common/pagination.jsp" %>
    </div>
</div>

<c:set var="itemName" value="sản phẩm" />
<c:set var="deleteUrl" value="/admin/stores/${storeId}/products/delete/" />
<c:set var="deleteMultipleUrl" value="/admin/stores/${storeId}/products/delete-multiple" />
<%@ include file="/WEB-INF/views/common/delete-modals.jsp" %>

<!-- Thêm JS -->
<c:import url="/WEB-INF/views/layouts/assets-helper.jsp">
    <c:param name="js" value="/js/list-actions.js" />
</c:import>
