-- Dữ liệu mẫu phục vụ kiểm tra phân trang LMS Mini.
-- Số lượng active sau khi chạy:
--   25 học viên, 12 khóa học, 24 bài học, 40 đăng ký, 121 media.
-- File có thể chạy lại: chỉ xóa dữ liệu mang prefix PAGE-/pagination-test.local.

SET NAMES utf8mb4;
USE TungAnhTest;

START TRANSACTION;

-- =========================================================
-- 1. Dọn bộ dữ liệu phân trang cũ nếu đã chạy file trước đó
-- =========================================================

DELETE image
FROM images image
JOIN students student
  ON image.object_type = 'STUDENT' AND image.object_id = student.id
WHERE student.email LIKE '%@pagination-test.local';

DELETE image
FROM images image
JOIN lessons lesson
  ON image.object_type = 'LESSON' AND image.object_id = lesson.id
JOIN courses course ON lesson.course_id = course.id
WHERE course.code LIKE 'PAGE-%';

DELETE image
FROM images image
JOIN courses course
  ON image.object_type = 'COURSE' AND image.object_id = course.id
WHERE course.code LIKE 'PAGE-%';

DELETE enrollment
FROM enrollments enrollment
LEFT JOIN students student ON enrollment.student_id = student.id
LEFT JOIN courses course ON enrollment.course_id = course.id
WHERE student.email LIKE '%@pagination-test.local'
   OR course.code LIKE 'PAGE-%';

DELETE lesson
FROM lessons lesson
JOIN courses course ON lesson.course_id = course.id
WHERE course.code LIKE 'PAGE-%';

DELETE FROM courses WHERE code LIKE 'PAGE-%';
DELETE FROM students WHERE email LIKE '%@pagination-test.local';

-- =========================================================
-- 2. Học viên: 25 bản ghi active
-- =========================================================

INSERT INTO students (name, email, phone, status, created_date, modified_date) VALUES
('Nguyễn Minh Anh 01', 'student01@pagination-test.local', '0902000001', 1, NOW(), NOW()),
('Trần Thu Hà 02', 'student02@pagination-test.local', '0902000002', 1, NOW(), NOW()),
('Lê Hoàng Nam 03', 'student03@pagination-test.local', '0902000003', 1, NOW(), NOW()),
('Phạm Quỳnh Chi 04', 'student04@pagination-test.local', '0902000004', 1, NOW(), NOW()),
('Đỗ Đức Long 05', 'student05@pagination-test.local', '0902000005', 1, NOW(), NOW()),
('Vũ Ngọc Mai 06', 'student06@pagination-test.local', '0902000006', 1, NOW(), NOW()),
('Bùi Gia Huy 07', 'student07@pagination-test.local', '0902000007', 1, NOW(), NOW()),
('Hoàng Khánh Linh 08', 'student08@pagination-test.local', '0902000008', 1, NOW(), NOW()),
('Đặng Tuấn Kiệt 09', 'student09@pagination-test.local', '0902000009', 1, NOW(), NOW()),
('Ngô Hải Yến 10', 'student10@pagination-test.local', '0902000010', 1, NOW(), NOW()),
('Dương Quốc Bảo 11', 'student11@pagination-test.local', '0902000011', 1, NOW(), NOW()),
('Lý Thanh Trúc 12', 'student12@pagination-test.local', '0902000012', 1, NOW(), NOW()),
('Mai Anh Tuấn 13', 'student13@pagination-test.local', '0902000013', 1, NOW(), NOW()),
('Tạ Bảo Ngọc 14', 'student14@pagination-test.local', '0902000014', 1, NOW(), NOW()),
('Cao Minh Khang 15', 'student15@pagination-test.local', '0902000015', 1, NOW(), NOW()),
('Hồ Phương Thảo 16', 'student16@pagination-test.local', '0902000016', 1, NOW(), NOW()),
('Chu Nhật Minh 17', 'student17@pagination-test.local', '0902000017', 1, NOW(), NOW()),
('La Thị Hương 18', 'student18@pagination-test.local', '0902000018', 1, NOW(), NOW()),
('Trịnh Công Sơn 19', 'student19@pagination-test.local', '0902000019', 1, NOW(), NOW()),
('Đinh Mỹ Duyên 20', 'student20@pagination-test.local', '0902000020', 1, NOW(), NOW()),
('Phan Việt Anh 21', 'student21@pagination-test.local', '0902000021', 1, NOW(), NOW()),
('Quách Thanh Tâm 22', 'student22@pagination-test.local', '0902000022', 1, NOW(), NOW()),
('Lương Ngọc Lan 23', 'student23@pagination-test.local', '0902000023', 1, NOW(), NOW()),
('Tô Đức Thành 24', 'student24@pagination-test.local', '0902000024', 1, NOW(), NOW()),
('Hà Kim Oanh 25', 'student25@pagination-test.local', '0902000025', 1, NOW(), NOW());

-- =========================================================
-- 3. Khóa học: 12 bản ghi active
-- PAGE-12 không có enrollment để kiểm tra xóa khóa học thành công.
-- =========================================================

INSERT INTO courses (name, code, description, duration, status, created_date, modified_date) VALUES
('Java Core thực hành', 'PAGE-01', 'Kiến thức Java nền tảng và bài tập thực hành.', 20, 1, NOW(), NOW()),
('Spring Boot REST API', 'PAGE-02', 'Xây dựng REST API với Spring Boot.', 32, 1, NOW(), NOW()),
('Spring Data JPA', 'PAGE-03', 'Entity, repository, transaction và tối ưu truy vấn.', 24, 1, NOW(), NOW()),
('Hibernate nâng cao', 'PAGE-04', 'Lazy loading, fetch join và xử lý N+1.', 18, 1, NOW(), NOW()),
('MariaDB căn bản', 'PAGE-05', 'Thiết kế bảng, index và truy vấn SQL.', 22, 1, NOW(), NOW()),
('Vue 3 Composition API', 'PAGE-06', 'Xây dựng giao diện với Vue 3.', 26, 1, NOW(), NOW()),
('Element Plus thực chiến', 'PAGE-07', 'Form, Table, Dialog và Upload.', 16, 1, NOW(), NOW()),
('RESTful API Design', 'PAGE-08', 'Thiết kế tài nguyên và chuẩn hóa response.', 14, 1, NOW(), NOW()),
('Validation và Exception', 'PAGE-09', 'Hibernate Validator và ControllerAdvice.', 12, 1, NOW(), NOW()),
('Upload ảnh và video', 'PAGE-10', 'Multipart, lưu file và preview media.', 15, 1, NOW(), NOW()),
('Export Excel với POI', 'PAGE-11', 'Xuất dữ liệu học viên và khóa học.', 10, 1, NOW(), NOW()),
('Khóa học chưa có học viên', 'PAGE-12', 'Dùng để kiểm tra xóa khóa học thành công.', 8, 1, NOW(), NOW());

-- =========================================================
-- 4. Bài học: mỗi khóa 2 bài, tổng cộng 24 bản ghi active
-- =========================================================

INSERT INTO lessons (course_id, title, description, status, created_date, modified_date)
SELECT course.id,
       CONCAT('Bài 1 - Tổng quan ', course.name),
       CONCAT('Giới thiệu nội dung và mục tiêu của khóa ', course.name, '.'),
       1, NOW(), NOW()
FROM courses course
WHERE course.code LIKE 'PAGE-%'
UNION ALL
SELECT course.id,
       CONCAT('Bài 2 - Thực hành ', course.name),
       CONCAT('Bài tập thực hành cho khóa ', course.name, '.'),
       1, NOW(), NOW()
FROM courses course
WHERE course.code LIKE 'PAGE-%';

-- =========================================================
-- 5. Đăng ký: 40 bản ghi active
-- 25 học viên đăng ký khóa đầu tiên và 15 học viên đầu đăng ký thêm khóa thứ hai.
-- Chỉ phân bổ vào PAGE-01 đến PAGE-11, để PAGE-12 luôn không có học viên.
-- =========================================================

INSERT INTO enrollments
    (student_id, course_id, enrolled_date, status, created_date, modified_date)
SELECT student.id,
       course.id,
       DATE_SUB(CURDATE(), INTERVAL CAST(RIGHT(SUBSTRING_INDEX(student.email, '@', 1), 2) AS UNSIGNED) DAY),
       1, NOW(), NOW()
FROM students student
JOIN courses course
  ON course.code = CONCAT(
      'PAGE-',
      LPAD(MOD(CAST(RIGHT(SUBSTRING_INDEX(student.email, '@', 1), 2) AS UNSIGNED) - 1, 11) + 1, 2, '0'))
WHERE student.email LIKE '%@pagination-test.local'
UNION ALL
SELECT student.id,
       course.id,
       DATE_SUB(CURDATE(), INTERVAL (CAST(RIGHT(SUBSTRING_INDEX(student.email, '@', 1), 2) AS UNSIGNED) + 5) DAY),
       1, NOW(), NOW()
FROM students student
JOIN courses course
  ON course.code = CONCAT(
      'PAGE-',
      LPAD(MOD(CAST(RIGHT(SUBSTRING_INDEX(student.email, '@', 1), 2) AS UNSIGNED), 11) + 1, 2, '0'))
WHERE student.email LIKE '%@pagination-test.local'
  AND CAST(RIGHT(SUBSTRING_INDEX(student.email, '@', 1), 2) AS UNSIGNED) <= 15;

-- =========================================================
-- 6. Media học viên: 25 avatar
-- =========================================================

INSERT INTO images
    (object_type, object_id, media_type, media_role, url, original_name, content_type,
     status, created_date, modified_date)
SELECT 'STUDENT', student.id, 'IMAGE', 'AVATAR',
       CASE MOD(student.id, 5)
           WHEN 0 THEN '/uploads/seed/assets/totoro.gif'
           WHEN 1 THEN '/uploads/seed/assets/vit.gif'
           WHEN 2 THEN '/uploads/seed/assets/meo.gif'
           WHEN 3 THEN '/uploads/seed/assets/medical-tech.jpg'
           ELSE '/uploads/seed/assets/medical-team.png'
       END,
       CASE MOD(student.id, 5)
           WHEN 0 THEN 'totoro.gif'
           WHEN 1 THEN 'vit.gif'
           WHEN 2 THEN 'meo.gif'
           WHEN 3 THEN 'medical-tech.jpg'
           ELSE 'medical-team.png'
       END,
       CASE MOD(student.id, 5)
           WHEN 3 THEN 'image/jpeg'
           WHEN 4 THEN 'image/png'
           ELSE 'image/gif'
       END,
       1, NOW(), NOW()
FROM students student
WHERE student.email LIKE '%@pagination-test.local';

-- =========================================================
-- 7. Media khóa học: 12 thumbnail + 12 ảnh gallery
-- =========================================================

INSERT INTO images
    (object_type, object_id, media_type, media_role, url, original_name, content_type,
     status, created_date, modified_date)
SELECT 'COURSE', course.id, 'IMAGE', 'THUMBNAIL',
       '/uploads/seed/assets/medical-tech.jpg',
       'medical-tech.jpg', 'image/jpeg', 1, NOW(), NOW()
FROM courses course
WHERE course.code LIKE 'PAGE-%'
UNION ALL
SELECT 'COURSE', course.id, 'IMAGE', 'GALLERY',
       '/uploads/seed/assets/medical-team.png',
       'medical-team.png', 'image/png', 1, NOW(), NOW()
FROM courses course
WHERE course.code LIKE 'PAGE-%';

-- =========================================================
-- 8. Media bài học: mỗi bài có thumbnail, video chính và ảnh nội dung
-- 24 x 3 = 72 bản ghi
-- =========================================================

INSERT INTO images
    (object_type, object_id, media_type, media_role, url, original_name, content_type,
     status, created_date, modified_date)
SELECT 'LESSON', lesson.id, 'IMAGE', 'THUMBNAIL',
       '/uploads/seed/assets/vit.gif',
       'vit.gif', 'image/gif', 1, NOW(), NOW()
FROM lessons lesson
JOIN courses course ON lesson.course_id = course.id
WHERE course.code LIKE 'PAGE-%'
UNION ALL
SELECT 'LESSON', lesson.id, 'VIDEO', 'LESSON_VIDEO',
       CASE MOD(lesson.id, 5)
           WHEN 0 THEN '/uploads/seed/assets/lesson-video-1.mp4'
           WHEN 1 THEN '/uploads/seed/assets/lesson-video-2.mp4'
           WHEN 2 THEN '/uploads/seed/assets/lesson-video-3.mp4'
           WHEN 3 THEN '/uploads/seed/assets/lesson-video-4.mp4'
           ELSE '/uploads/seed/assets/lesson-video-5.mp4'
       END,
       CASE MOD(lesson.id, 5)
           WHEN 0 THEN 'lesson-video-1.mp4'
           WHEN 1 THEN 'lesson-video-2.mp4'
           WHEN 2 THEN 'lesson-video-3.mp4'
           WHEN 3 THEN 'lesson-video-4.mp4'
           ELSE 'lesson-video-5.mp4'
       END,
       'video/mp4', 1, NOW(), NOW()
FROM lessons lesson
JOIN courses course ON lesson.course_id = course.id
WHERE course.code LIKE 'PAGE-%'
UNION ALL
SELECT 'LESSON', lesson.id, 'IMAGE', 'LESSON_IMAGE',
       '/uploads/seed/assets/medical-team.png',
       'medical-team.png', 'image/png', 1, NOW(), NOW()
FROM lessons lesson
JOIN courses course ON lesson.course_id = course.id
WHERE course.code LIKE 'PAGE-%';

COMMIT;

-- =========================================================
-- 9. Kiểm tra số lượng sau khi insert
-- Kết quả mong đợi: 25, 12, 24, 40, 121
-- =========================================================

SELECT 'students_active' AS dataset, COUNT(*) AS total
FROM students
WHERE email LIKE '%@pagination-test.local' AND status = 1
UNION ALL
SELECT 'courses_active', COUNT(*)
FROM courses
WHERE code LIKE 'PAGE-%' AND status = 1
UNION ALL
SELECT 'lessons_active', COUNT(*)
FROM lessons lesson
JOIN courses course ON lesson.course_id = course.id
WHERE course.code LIKE 'PAGE-%' AND lesson.status = 1
UNION ALL
SELECT 'enrollments_active', COUNT(*)
FROM enrollments enrollment
JOIN students student ON enrollment.student_id = student.id
WHERE student.email LIKE '%@pagination-test.local' AND enrollment.status = 1
UNION ALL
SELECT 'images_active', COUNT(*)
FROM images image
WHERE image.status = 1
  AND (
      (image.object_type = 'STUDENT' AND image.object_id IN (
          SELECT student.id FROM students student
          WHERE student.email LIKE '%@pagination-test.local'))
      OR
      (image.object_type = 'COURSE' AND image.object_id IN (
          SELECT course.id FROM courses course
          WHERE course.code LIKE 'PAGE-%'))
      OR
      (image.object_type = 'LESSON' AND image.object_id IN (
          SELECT lesson.id
          FROM lessons lesson
          JOIN courses course ON lesson.course_id = course.id
          WHERE course.code LIKE 'PAGE-%'))
  );
