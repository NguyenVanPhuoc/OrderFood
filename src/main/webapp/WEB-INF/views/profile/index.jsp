<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
  <%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>

    <div class=proflie-page>
      <!-- Thông báo thành công/lỗi -->
      <c:if test="${not empty successMessage}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
          <i class="fas fa-check-circle me-2"></i>${successMessage}
          <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
      </c:if>

      <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
          <i class="fas fa-exclamation-circle me-2"></i>${errorMessage}
          <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
      </c:if>

      <div>
        <div class="header-title">
          <h4 class="mb-0 d-flex align-items-center">
            <i class="fas fa-user-circle me-2"></i>Thông tin cá nhân
          </h4>
        </div>
        <form action="${pageContext.request.contextPath}/profile/update" method="POST" enctype="multipart/form-data"
          id="profileForm">
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
          <div class="row">
            <!-- Cột trái: Ảnh đại diện -->
            <div class="col-lg-4 mb-4 mb-lg-0">
              <div class="card">
                <div class="card-body">
                  <div class="avatar-container mb-4">
                    <div class="avatar-wrapper">
                      <c:choose>
                        <c:when test="${not empty user.avatar}">
                          <img src="${pageContext.request.contextPath}/uploads/${user.avatar}" id="avatarPreview"
                            alt="Avatar">
                        </c:when>
                        <c:otherwise>
                          <img src="${pageContext.request.contextPath}/images/default-avatar.png" id="avatarPreview"
                            alt="Default Avatar">
                        </c:otherwise>
                      </c:choose>
                      <div class="avatar-edit">
                        <label for="imageFile" class="btn btn-sm btn-primary rounded-circle" title="Thay đổi ảnh"> <i
                            class="fas fa-camera"></i>
                        </label> <input type="file" class="d-none" id="imageFile" name="imageFile" accept="image/*">
                      </div>
                    </div>
                  </div>
                  <h4 class="mb-1">${user.name}</h4>
                  <p class="text-muted mb-3">${user.email}</p>
                  <div class="user-info-summary">
                    <div class="mb-2">
                      <i class="fas fa-phone-alt me-2 text-primary"></i>${user.phone}
                    </div>
                    <div>
                      <i class="fas fa-map-marker-alt me-2 text-primary"></i>${user.address}
                    </div>
                  </div>
                </div>
              </div>

            </div>

            <!-- Cột phải: Thông tin cá nhân -->
            <div class="col-lg-8">
              <div class="card">
                <div class="card-body text-left">
                  <div class="form-group mb-3">
                    <label for="name" class="form-label">Họ và tên</label>
                    <div class="input-group">
                      <span class="input-group-text bg-light"><i class="fas fa-user"></i></span>
                      <input type="text" class="form-control" id="name" name="name" value="${user.name}" readonly
                        disabled>
                    </div>
                  </div>
                  <div class="form-group mb-3">
                    <label for="email" class="form-label">Email</label>
                    <div class="input-group">
                      <span class="input-group-text bg-light"><i class="fas fa-envelope"></i></span> <input type="email"
                        class="form-control" id="email" name="email" value="${user.email}">
                    </div>
                  </div>
                  <div class="form-group mb-3">
                    <label for="phone" class="form-label">Số điện thoại</label>
                    <div class="input-group">
                      <span class="input-group-text bg-light"><i class="fas fa-phone"></i></span>
                      <input type="tel" class="form-control" id="phone" name="phone" value="${user.phone}">
                    </div>
                  </div>
                  <div class="form-group mb-3">
                    <label for="address" class="form-label">Địa chỉ</label>
                    <div class="input-group">
                      <span class="input-group-text bg-light"><i class="fas fa-map-marker-alt"></i></span> <input
                        type="text" class="form-control" id="address" name="address" value="${user.address}">
                    </div>
                  </div>

                  <hr class="my-4">
                  <h5 class="mb-3">
                    <i class="fas fa-key me-2"></i>Thay đổi email / mật khẩu
                  </h5>
                  <p class="text-muted small mb-3">Bắt buộc nhập mật khẩu hiện tại nếu muốn đổi email hoặc mật khẩu.</p>

                  <div class="form-group mb-3">
                    <label for="currentPassword" class="form-label">Mật khẩu hiện tại</label>
                    <div class="input-group">
                      <span class="input-group-text bg-light"><i class="fas fa-lock"></i></span>
                      <input type="password" class="form-control" id="currentPassword" name="currentPassword" placeholder="Nhập mật khẩu hiện tại">
                      <button class="btn btn-outline-secondary toggle-password" type="button" tabindex="-1">
                        <i class="fas fa-eye"></i>
                      </button>
                    </div>
                  </div>

                  <div class="form-group mb-3">
                    <label for="password" class="form-label">Mật khẩu mới</label>
                    <div class="input-group">
                      <span class="input-group-text bg-light"><i class="fas fa-lock"></i></span>
                      <input type="password" class="form-control" id="password" name="password">
                      <button class="btn btn-outline-secondary toggle-password" type="button" tabindex="-1">
                        <i class="fas fa-eye"></i>
                      </button>
                    </div>
                  </div>
                  <div class="form-group mb-3">
                    <label for="confirmPassword" class="form-label">Xác nhận
                      mật khẩu</label>
                    <div class="input-group">
                      <span class="input-group-text bg-light"><i class="fas fa-lock"></i></span>
                      <input type="password" class="form-control" id="confirmPassword" name="confirmPassword">
                      <button class="btn btn-outline-secondary toggle-password" type="button" tabindex="-1">
                        <i class="fas fa-eye"></i>
                      </button>
                    </div>
                  </div>

                  <div class="d-flex justify-content-end mt-4">
                    <button type="submit" class="btn btn-success">
                      <i class="fas fa-save me-1"></i>Cập nhật
                    </button>
                  </div>
                </div>

              </div>
            </div>
          </div>
        </form>
      </div>
    </div>

    <script>
      document.addEventListener('DOMContentLoaded', function () {
        // Xử lý hiển thị ảnh preview khi chọn file
        const imageFile = document.getElementById('imageFile');
        const avatarPreview = document.getElementById('avatarPreview');

        imageFile.addEventListener('change', function (e) {
          const file = e.target.files[0];
          if (file) {
            const reader = new FileReader();
            reader.onload = function (e) {
              avatarPreview.src = e.target.result;
              avatarPreview.style.animation = "fadeIn 0.5s";
            }
            reader.readAsDataURL(file);
          }
        });

        // Xử lý validation form
        const form = document.getElementById('profileForm');
        form.addEventListener('submit', function (e) {
          const password = document.getElementById('password').value;
          const confirmPassword = document.getElementById('confirmPassword').value;

          if (password || confirmPassword) {
            if (password !== confirmPassword) {
              e.preventDefault();

              // Hiển thị thông báo lỗi
              const errorAlert = document.createElement('div');
              errorAlert.className = 'alert alert-danger alert-dismissible fade show mt-3';
              errorAlert.role = 'alert';
              errorAlert.innerHTML = `
                    <i class="fas fa-exclamation-circle me-2"></i>Mật khẩu xác nhận không khớp với mật khẩu mới!
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                `;

              const passwordField = document.getElementById('password').parentNode.parentNode;
              passwordField.insertAdjacentElement('afterend', errorAlert);

              // Scroll to error
              errorAlert.scrollIntoView({ behavior: 'smooth', block: 'center' });
            }
          }
        });

        // Xử lý hiển thị/ẩn mật khẩu
        const toggleButtons = document.querySelectorAll('.toggle-password');
        toggleButtons.forEach(button => {
          button.addEventListener('click', function () {
            const input = this.previousElementSibling;
            const icon = this.querySelector('i');

            if (input.type === 'password') {
              input.type = 'text';
              icon.classList.remove('fa-eye');
              icon.classList.add('fa-eye-slash');
            } else {
              input.type = 'password';
              icon.classList.remove('fa-eye-slash');
              icon.classList.add('fa-eye');
            }
          });
        });

        // Animation cho các phần tử khi load trang
        document.querySelector('.card').style.animation = "fadeIn 0.5s";

      });
    </script>