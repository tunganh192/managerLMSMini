# LMS Mini

Backend REST API quản lý học viên, khóa học, bài học và đăng ký học bằng Spring Boot và MariaDB.

## Chạy ứng dụng

Yêu cầu Java 17+ và Maven. Cấu hình kết nối bằng biến môi trường, không lưu mật khẩu trong source code:

```powershell
$env:DB_URL='jdbc:mariadb://localhost:3306/manager_lms_mini?useUnicode=true&characterEncoding=utf8'
$env:DB_USERNAME='root'
$env:DB_PASSWORD='your-password'
$env:JPA_DDL_AUTO='validate'
```

Với database local hoàn toàn mới, có thể dùng `JPA_DDL_AUTO=update` đúng một lần để tạo schema, sau đó chuyển lại `validate`. Không dùng `update` trên database dùng chung/production.

Sau đó chạy:

```powershell
mvn spring-boot:run
```

Ứng dụng chạy tại `http://localhost:8080`. Trang Media Studio tại `/` cho phép thêm/sửa học viên, khóa học, bài học và preview ảnh/video trước khi upload.

Các API JSON đều trả `ApiResponse` có cấu trúc `code`, `message`, `data` và thông báo Việt/Anh theo header `Accept-Language`. Riêng API export trả trực tiếp file Excel.

## API chính

- `/api/students`: CRUD và tìm kiếm phân trang theo `keyword`, `name`, `email`, `phone`.
- `/api/students/export`: export Excel theo cùng bộ lọc học viên.
- `/api/courses`: CRUD và tìm kiếm phân trang theo `keyword`, `name`, `code`, `minDuration`, `maxDuration`.
- `/api/courses/export`: export Excel theo cùng bộ lọc khóa học.
- `/api/lessons`: CRUD bài học và danh sách theo khóa học.
- `/api/enrollments`: đăng ký nhiều khóa, sửa/xóa và danh sách học viên theo khóa.

Ảnh được lưu dưới `uploads/students`, `uploads/courses`, `uploads/lessons/thumbnails`; video nằm trong `uploads/lessons/videos`.
