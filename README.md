# Order Food (Bau Food)

Ứng dụng web đặt đồ ăn xây dựng bằng **Spring Boot** với giao diện JSP. Dự án hỗ trợ quản lý cửa hàng, sản phẩm, đơn hàng và người dùng.

## Công nghệ sử dụng

- **Java 21**
- **Spring Boot 2.7.18**
- **Spring Security** – Xác thực Admin & User
- **Spring Data JPA** – Truy cập cơ sở dữ liệu
- **MySQL 8** – Cơ sở dữ liệu
- **JSP / JSTL** – Giao diện
- **Gradle** – Build tool

## Yêu cầu hệ thống

- **Java 21** (hoặc 17 trở lên)
- **MySQL 8**
- **Gradle** (hoặc dùng `gradlew` có sẵn trong project)

---

## Cách 1: Chạy bằng Docker (Khuyến nghị)

Dễ nhất khi chưa cài sẵn MySQL.

### Bước 1: Cài đặt Docker

Đảm bảo máy đã cài [Docker](https://docs.docker.com/get-docker/) và [Docker Compose](https://docs.docker.com/compose/install/).

### Bước 2: Chạy ứng dụng

```bash
# Trong thư mục gốc của project
docker-compose up --build
```

> **Lưu ý:** Sau khi nâng cấp Java/Spring Boot, cần rebuild image: `docker-compose up --build` hoặc `docker-compose build --no-cache`

### Bước 3: Truy cập ứng dụng

| Dịch vụ | URL | Mô tả |
|---------|-----|-------|
| Ứng dụng | http://localhost:8081 | Trang web chính |
| Admin | http://localhost:8081/admin/login | Trang quản trị |
| phpMyAdmin | http://localhost:8765 | Quản lý DB (user: root, pass: root) |

### Dừng Docker

```bash
docker-compose down
```

---

## Cách 2: Chạy trực tiếp trên máy

### Bước 1: Cài đặt MySQL

1. Cài MySQL 8 và chạy MySQL Server.
2. Tạo database và user (hoặc dùng `root`):

```sql
CREATE DATABASE bau_food CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- Nếu dùng user khác root:
-- CREATE USER 'bau_food'@'localhost' IDENTIFIED BY 'your_password';
-- GRANT ALL ON bau_food.* TO 'bau_food'@'localhost';
-- FLUSH PRIVILEGES;
```

### Bước 2: Cấu hình kết nối DB

Mở `src/main/resources/application.properties` và chỉnh URL kết nối MySQL:

```properties
# Dùng localhost khi chạy local
spring.datasource.url=jdbc:mysql://localhost:3306/bau_food
#spring.datasource.url=jdbc:mysql://mysql:3306/bau_food

spring.datasource.username=root
spring.datasource.password=root
```

### Bước 3: Chạy ứng dụng

```bash
# Trong thư mục gốc của project
./gradlew bootRun

# Trên Windows (PowerShell hoặc CMD):
gradlew.bat bootRun
```

### Bước 4: Truy cập

- Trang chủ: http://localhost:8080
- Admin: http://localhost:8080/admin/login

## Seed dữ liệu mẫu

Ứng dụng tự động seed **2 user** để đăng nhập khi chạy lần đầu (khi database chưa có user).

### Tài khoản đăng nhập

| Vai trò | Email | Mật khẩu |
|---------|-------|----------|
| **Admin** | admin@bau.com | 123456 |
| **User** | user@test.com | 123456 |

> **Lưu ý:** Seed chỉ chạy khi `users` table rỗng. Muốn seed lại: xóa hết users rồi restart ứng dụng.

---

## Chạy Unit Test

### Yêu cầu

- **Java 21** đã cài đặt
- Biến môi trường **JAVA_HOME** trỏ đến thư mục cài đặt Java

### Kiểm tra Java

```bash
java -version
```

Nếu chưa có `JAVA_HOME` (Windows):

```powershell
# Kiểm tra
$env:JAVA_HOME

# Thiết lập (thay đường dẫn cho đúng với máy bạn)
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
```

### Chạy toàn bộ Unit Test

```bash
# Linux / Mac
./gradlew test

# Windows (PowerShell hoặc CMD)
gradlew.bat test
```

### Một số lệnh hữu ích

```bash
# Chạy test và build
gradlew.bat build

# Chạy test không dùng daemon
gradlew.bat test --no-daemon

# Chạy một class test cụ thể (ví dụ: UserServiceTest)
gradlew.bat test --tests "com.example.lesson3.service.UserServiceTest"

# Chạy một method test cụ thể
gradlew.bat test --tests "com.example.lesson3.service.UserServiceTest.testCreateUser"

# Build bỏ qua test
gradlew.bat build -x test

# Xóa build cũ rồi chạy test lại
gradlew.bat clean test
```

### Kết quả test

Sau khi chạy xong:

- Báo cáo HTML: `build/reports/tests/test/index.html`
- Mở file này bằng trình duyệt để xem chi tiết từng test

### Chạy test trong Docker

Dùng service `test` (H2 in-memory, **không cần MySQL**):

```bash
# Cách 1: Dùng service test (khuyến nghị - dùng H2, không xung đột với app)
docker-compose --profile test run test

# Cách 2: Nếu app đang chạy, tạm dừng (Ctrl+C) rồi chạy:
docker-compose --profile test run test
```

> **Quan trọng:** Service `test` dùng biến môi trường H2, ghi đè config MySQL của `app`. Khi chạy trong Docker, nếu dùng `app` service thì env MySQL sẽ ghi đè config H2 trong `application.properties` → test sẽ cố kết nối MySQL và fail.

Sau khi chạy xong, báo cáo HTML: `build/reports/tests/test/index.html`

### Unit test có cần MySQL không?

**Không.** Unit test dùng **H2 in-memory** – database nhúng chạy trong RAM, không cần cài MySQL.

| Loại test | Cần DB? | Giải thích |
|-----------|---------|------------|
| **Unit test** (ServiceTest với @Mock) | Không | Mock repository, không gọi DB thật |
| **Integration test** (RepositoryTest, contextLoads) | Có DB | Test query thật – dùng **H2** thay MySQL |

H2 tương thích MySQL syntax nên Repository test chạy bình thường mà không cần MySQL.

### Lưu ý

- Cấu hình test: `src/test/resources/application.properties`

---

## Build (không chạy test)

```bash
./gradlew build
# hoặc
gradlew.bat build

# Build bỏ qua test
gradlew.bat build -x test
```

---

## Cấu hình nâng cao

| Thuộc tính | Mặc định | Mô tả |
|------------|----------|-------|
| `server.port` | 8080 | Cổng ứng dụng |
| `spring.datasource.url` | jdbc:mysql://... | URL MySQL |
| `spring.jpa.hibernate.ddl-auto` | update | Tự tạo/cập nhật schema |
| `file.upload-dir` | uploads/ | Thư mục lưu ảnh |

---

## Xử lý lỗi thường gặp

### Lỗi kết nối MySQL

- Kiểm tra MySQL đang chạy.
- Kiểm tra `username`, `password` và tên database trong `application.properties`.
- Khi dùng Docker: dùng host `mysql`; khi chạy local: dùng `localhost`.

### Lỗi port đã sử dụng

- Đổi `server.port` trong `application.properties` hoặc cổng map trong `docker-compose.yml`.

### Lỗi Gradle

- Chạy: `./gradlew clean build` rồi thử lại.

### Lỗi `JAVA_HOME is not set` / `java command not found`

**Bước 1: Cài Java 21** (nếu chưa có)

- Tải: [Eclipse Temurin JDK 21](https://adoptium.net/temurin/releases/?version=21&os=windows) hoặc [Oracle JDK 21](https://www.oracle.com/java/technologies/downloads/#java21)
- Cài đặt (thường vào `C:\Program Files\Java\jdk-21` hoặc `C:\Program Files\Eclipse Adoptium\jdk-21.x.x.x-hotspot`)

**Bước 2: Thiết lập JAVA_HOME (Windows)**

1. Nhấn `Win + R` → gõ `sysdm.cpl` → Enter  
2. Tab **Advanced** → **Environment Variables**  
3. Trong **System variables** → **New**  
   - Variable name: `JAVA_HOME`  
   - Variable value: `C:\Program Files\Java\jdk-21` *(đổi cho đúng đường dẫn cài Java của bạn)*  
4. Chọn biến **Path** → **Edit** → **New** → thêm `%JAVA_HOME%\bin`  
5. **OK** → đóng tất cả cửa sổ  
6. Mở **CMD hoặc PowerShell mới** rồi chạy `java -version` để kiểm tra  

**Bước 3: Chạy lại test**

```cmd
cd D:\Project Tôi\OrderFood
gradlew.bat test
```

> **Lưu ý:** Phải mở CMD/PowerShell mới sau khi đổi biến môi trường thì mới có hiệu lực.

**Tạm thời (chỉ trong phiên hiện tại)**

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
gradlew.bat test
```

### Các lỗi Unit Test khác

| Lỗi | Cách xử lý |
|-----|------------|
| Test fail / timeout | Chạy `gradlew.bat clean test` để xóa cache và chạy lại |

---