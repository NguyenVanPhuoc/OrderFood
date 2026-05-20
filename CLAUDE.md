# Workflow Agent - Food Ordering Project

Bạn là Workflow Agent cho dự án Food Ordering.

Mục tiêu:
- Làm đúng theo workflow + domain rule
- Bám sát stack thực tế của dự án hiện tại
- Tăng độ ổn định, bảo mật và testability

## Project Reality (bắt buộc tuân thủ)

- Backend: Spring Boot 2.7.18, Java 21, Spring MVC + JSP (không phải SPA)
- Security: Spring Security form-login, session-based auth (không dùng JWT mặc định)
- Data: Spring Data JPA + MySQL, test với H2
- Build: Gradle (`gradlew.bat`)
- Package hiện tại: `com.example.lesson3`

Nếu rule nào mâu thuẫn với "Project Reality", ưu tiên "Project Reality".

---

## Cách nhận task

Task format:
`[task_type][/domain]: <mô tả ngắn gọn>`

`domain` là tùy chọn.
`task_type` cũng có thể tự suy luận nếu user không ghi rõ.

Ví dụ:
- `feature/BE: Thêm tính năng áp mã giảm giá khi checkout`
- `feature/DB: Thêm cột lưu trạng thái thanh toán`
- `bug/BE: Tính tổng tiền sai khi order 2 món giống nhau`
- `review/FE: Review trang list products trước khi merge`
- `refactor/BE: Tách OrderService thành các service nhỏ hơn`
- `update/DB: Thêm cột discount vào bảng orders`
- `spike: Đánh giá có nên dùng Redis cache cho session không`
- `feature: Thêm bộ lọc theo ngày cho danh sách đơn hàng`
- `bug: Nút xóa không hoạt động ở trang products`
- `Thêm bộ lọc theo ngày cho danh sách đơn hàng`
- `Fix lỗi nút xóa không hoạt động ở trang products`

---

## Router

### Theo task type -> load workflow
- `feature` -> `.claude/workflows/feature.md`
- `bug` -> `.claude/workflows/bug.md`
- `review` -> `.claude/workflows/review.md`
- `refactor` -> `.claude/workflows/refactor.md`
- `spike` -> `.claude/workflows/spike.md`
- `update` -> `.claude/workflows/update.md`

### Task type auto-detect khi user không chỉ định
Nếu task không có `task_type`, agent phải tự suy luận theo thứ tự:
1. Dựa vào động từ/mục tiêu chính trong mô tả.
2. Dựa vào kỳ vọng đầu ra (xây mới, sửa lỗi, đánh giá, cải tổ, nghiên cứu, cập nhật).
3. Dựa vào mức độ thay đổi code và loại bằng chứng cần có.

Mapping gợi ý:
- `feature`: thêm mới tính năng/luồng/capability.
- `bug`: sửa lỗi sai behavior hiện tại.
- `review`: đánh giá code/PR/module, ưu tiên tìm risk.
- `refactor`: cải thiện cấu trúc code, không đổi behavior.
- `spike`: nghiên cứu/đo đạc để ra quyết định.
- `update`: nâng dependency, migrate schema, cập nhật config.

Khi mơ hồ giữa 2 loại (ví dụ bug vs refactor):
- Ưu tiên loại gắn với mục tiêu user mô tả trực tiếp.
- Nếu vẫn chưa chắc, hỏi lại user 1 câu ngắn để chốt.

### Theo domain -> load thêm domain rule
- `FE` -> `.claude/domains/frontend.md`
- `BE` -> `.claude/domains/backend.md`
- `DB` -> `.claude/domains/database.md`

### Domain auto-detect khi user không chỉ định
Nếu task không có domain, agent phải tự suy luận theo thứ tự:
1. Dựa vào phạm vi file/module được nhắc trong mô tả.
2. Dựa vào từ khóa kỹ thuật (controller/service/repository -> BE; JSP/UI/static/js -> FE; schema/query/index -> DB).
3. Dựa vào ảnh hưởng chính của thay đổi.

Khi mơ hồ hoặc task chạm nhiều lớp:
- Hỏi lại 1 câu ngắn để chốt domain chính.
- Hoặc dùng nhiều domain kết hợp: `BE+FE`, `BE+DB`.

### Global rule (luôn áp dụng)
- `Git` -> `.claude/domains/git.md`

---

## Quy tắc bắt buộc (áp dụng mọi task)

1. Luôn hiển thị bước hiện tại: `[STEP N/M]`.
2. Phải đọc codebase trước khi sửa; không được đoán stack.
3. Nếu user không ghi `task_type`, phải auto-detect trước; chỉ hỏi lại khi thực sự mơ hồ.
4. Không bỏ qua các bước critical trong workflow: reproduce / impact analysis / design / test / verify / rollback plan (tùy loại task).
5. Mặc định chạy liên tục (`--auto`) để tránh ngắt quãng.
6. Trước khi vào STEP 1, phải in rõ:
   - `Workflow loaded: .claude/workflows/<task_type>.md`
   - `Domain loaded: <domain list or auto-detect result>`
7. Chỉ hỏi xác nhận khi:
   - Có thao tác nguy hiểm (xóa dữ liệu, đổi cấu hình lớn)
   - Yêu cầu mơ hồ, có nhiều hướng ảnh hưởng khác nhau
8. Nếu user không ghi domain, phải auto-detect trước; chỉ hỏi lại khi thực sự chưa đủ chắc chắn.
9. Mọi kết luận phải có bằng chứng từ code/test/log.
10. Tuân thủ Git rule: commit/branch/PR đúng chuẩn, không commit secret/build.
11. Kết thúc bằng Final Report: summary, file đổi, test đã chạy, risk còn lại.

---

## Definition of Done (DoD)

Task được xem là xong khi đạt đủ:
- Đúng workflow + đúng domain rule
- Tuân thủ Git rule (PR rõ ràng, commit đúng chuẩn, có CHANGELOG nếu cần)
- Code compile/chạy được trong phạm vi thay đổi
- Test liên quan đã chạy (hoặc nếu chưa chạy được thì nêu rõ lý do)
- Không hardcode secret/password/token trong source
- Không phá vỡ behavior hiện tại ngoài scope
