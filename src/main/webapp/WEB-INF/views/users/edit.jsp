<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
 <div class="container">
     <div class="row justify-content-center">
         <div class="col-lg-8">
             <div class="card">
                 <div class="card-header d-flex align-items-center justify-content-between">
                     <h3 class="edit-user-heading"><i class="fas fa-user-edit me-2"></i>Chỉnh Sửa thành viên</h3>
                 </div>
                 <div class="card-body">
                     <div class="form-container">
                         <div class="user-info-summary d-flex align-items-center">
                             <div class="user-avatar me-3">
                                 <c:out value="${fn:toUpperCase(fn:substring(user.name, 0, 1))}" default="U" />
                             </div>
                             <div>
                                 <h5 class="mb-1"><c:out value="${user.name}" /></h5>
                                 <p class="mb-0 text-muted">
                                     <c:out value="${user.email}" /> • 
                                     <c:choose>
                                         <c:when test="${user.role == 1}">Admin</c:when>
                                         <c:otherwise>User</c:otherwise>
                                     </c:choose> • 
                                     ID: <c:out value="${user.id}" />
                                 </p>
                             </div>
                         </div>

                         <form:form action="${pageContext.request.contextPath}/admin/users/edit/${user.id}" method="post" modelAttribute="user" enctype="multipart/form-data">
                             <input type="hidden" name="id" value="${user.id}" />

                             <div class="form-group">
                                 <label for="username" class="form-label required-field">Username</label>
                                 <div class="input-group">
                                     <span class="input-group-text"><i class="fas fa-user"></i></span>
                                     <form:input path="username" cssClass="form-control" id="username" />
                                 </div>
                                 <form:errors path="username" cssClass="text-red-500 error-validate" />
                             </div>

                             <div class="form-group">
                                 <label for="email" class="form-label required-field">Email</label>
                                 <div class="input-group">
                                     <span class="input-group-text"><i class="fas fa-envelope"></i></span>
                                     <form:input type="email" path="email" cssClass="form-control" id="email" />
                                 </div>
                                 <form:errors path="email" cssClass="text-red-500 error-validate" />
                             </div>

                             <div class="form-group">
                                 <label for="name" class="form-label required-field">Họ và tên</label>
                                 <div class="input-group">
                                     <span class="input-group-text"><i class="fas fa-id-card"></i></span>
                                     <form:input path="name" cssClass="form-control" id="name" />
                                 </div>
                                 <form:errors path="name" cssClass="text-red-500 error-validate" />
                             </div>

                             <div class="form-group">
							    <label for="role" class="form-label required-field">Vai trò</label>
							    <div class="input-group">
							        <span class="input-group-text"><i class="fas fa-user-shield"></i></span>
							        <form:select path="role" cssClass="form-select" id="role">
							            <form:option value="1" label="Admin" />
							            <form:option value="2" label="User" />
							        </form:select>
							    </div>
							</div>

                            <div class="form-group">
							    <label for="status" class="form-label required-field">Trạng thái</label>
							    <div class="input-group">
							        <span class="input-group-text"><i class="fas fa-toggle-on"></i></span>
							        <form:select path="status" cssClass="form-select" id="status">
							            <form:option value="1" label="Hoạt động" />
							            <form:option value="0" label="Bị khóa" />
							        </form:select>
							    </div>
							</div>

                             <div class="form-group">
                                 <label for="phone" class="form-label">Số điện thoại</label>
                                 <div class="input-group">
                                     <span class="input-group-text"><i class="fas fa-phone"></i></span>
                                     <form:input path="phone" cssClass="form-control" id="phone" />
                                 </div>
                                 <form:errors path="phone" cssClass="text-red-500 error-validate" />
                             </div>

                             <div class="form-group">
                                 <label for="address" class="form-label">Địa chỉ</label>
                                 <div class="input-group">
                                     <span class="input-group-text"><i class="fas fa-map-marker-alt"></i></span>
                                     <form:input path="address" cssClass="form-control" id="address" />
                                 </div>
                                 <form:errors path="address" cssClass="text-red-500 error-validate" />
                             </div>
                             
                             <!-- Upload hình ảnh mới -->
                            <div class="form-group mb-2">
							    <label for="image" class="form-label">Ảnh đại diện</label>
							    <div class="input-group">
							        <span class="input-group-text"><i class="fas fa-image"></i></span>
							        <input type="file" class="form-control" name="imageFile" id="image" accept="image/*" />
							    </div>
							</div>
							
							<c:if test="${not empty user.avatar}">
							    <div class="form-group mb-4" id="previewContainer">
							        <img id="previewImage" 
							             src="${pageContext.request.contextPath}/uploads/${user.avatar}"
							             alt="Ảnh đại diện" 
							             class="img-thumbnail"
							             style="max-width: 150px; margin-top: 10px;"
							        />
							    </div>
							</c:if>

                             <div class="form-group d-flex justify-content-between mt-4">
                                 <div>
                                     <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-back me-2">
                                         <i class="fas fa-arrow-left me-2"></i>Quay lại
                                     </a>
                                 </div>
                                 <button type="submit" class="btn btn-primary">
                                     <i class="fas fa-save me-2"></i>Cập nhật
                                 </button>
                             </div>
                         </form:form>
                     </div>
                 </div>
             </div>
         </div>
     </div>
 </div>