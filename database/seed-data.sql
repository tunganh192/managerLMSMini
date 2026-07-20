-- LMS Mini - dữ liệu mẫu đầy đủ để test REST API
-- Chạy sau khi schema đã được tạo. Chỉ dùng JPA_DDL_AUTO=update cho lần khởi tạo local,
-- sau đó chuyển lại validate; không dùng update trên môi trường thật.
-- Database hiện tại: TungAnhTest (xem application.yaml).

SET NAMES utf8mb4;
USE TungAnhTest;

-- ddl-auto=update không tự xóa unique index cũ sau khi bỏ @UniqueConstraint.
ALTER TABLE courses DROP INDEX IF EXISTS uk_course_code;

START TRANSACTION;


ALTER TABLE courses DROP COLUMN thumbnail_url;
ALTER TABLE lessons DROP COLUMN thumbnail_url;
ALTER TABLE lessons DROP COLUMN video_url;
ALTER TABLE students DROP COLUMN avatar_url;

DROP TABLE IF EXISTS course_images;
DROP TABLE IF EXISTS lesson_media;



-- =========================================================
-- 1. Dọn riêng bộ dữ liệu seed cũ để file có thể chạy lặp lại
-- =========================================================

DELETE i
FROM images i
JOIN students s ON i.object_type = 'STUDENT' AND i.object_id = s.id
WHERE s.email LIKE '%@lms-seed.test';

DELETE i
FROM images i
JOIN courses c ON i.object_type = 'COURSE' AND i.object_id = c.id
WHERE c.code LIKE 'LMS-%';

DELETE i
FROM images i
JOIN lessons l ON i.object_type = 'LESSON' AND i.object_id = l.id
JOIN courses c ON l.course_id = c.id
WHERE c.code LIKE 'LMS-%';

DELETE e
FROM enrollments e
LEFT JOIN courses c ON e.course_id = c.id
LEFT JOIN students s ON e.student_id = s.id
WHERE c.code LIKE 'LMS-%' OR s.email LIKE '%@lms-seed.test';

DELETE l
FROM lessons l
JOIN courses c ON l.course_id = c.id
WHERE c.code LIKE 'LMS-%';

DELETE FROM courses WHERE code LIKE 'LMS-%';
DELETE FROM students WHERE email LIKE '%@lms-seed.test';

-- =========================================================
-- 2. Học viên
-- =========================================================

INSERT INTO students (name, email, phone, status, created_date, modified_date) VALUES
('Nguyễn Minh Anh', 'minhanh@lms-seed.test', '0901000001', 1, NOW(), NOW()),
('Trần Thu Hà', 'thuha@lms-seed.test', '0901000002', 1, NOW(), NOW()),
('Lê Hoàng Nam', 'hoangnam@lms-seed.test', '0901000003', 1, NOW(), NOW()),
('Phạm Quỳnh Chi', 'quynhchi@lms-seed.test', '0901000004', 1, NOW(), NOW()),
('Đỗ Đức Long', 'duclong@lms-seed.test', '0901000005', 1, NOW(), NOW()),
('Vũ Ngọc Mai', 'ngocmai@lms-seed.test', '0901000006', 1, NOW(), NOW()),
('Học Viên Đã Xóa', 'deleted@lms-seed.test', '0901000099', 0, NOW(), NOW());

-- =========================================================
-- 3. Khóa học
-- LMS-DEVOPS không có enrollment, dùng để test xóa khóa học thành công.
-- Các khóa còn lại có học viên, dùng để test lỗi COURSE_HAS_STUDENTS.
-- =========================================================

INSERT INTO courses (name, code, description, duration, status, created_date, modified_date) VALUES
('Java Spring Boot từ cơ bản đến nâng cao', 'LMS-JAVA', 'REST API, JPA, Validation, Transaction và xử lý lỗi.', 48, 1, NOW(), NOW()),
('Vue 3 và Element Plus', 'LMS-VUE', 'Composition API, Router, Pinia và giao diện quản trị.', 36, 1, NOW(), NOW()),
('MariaDB thực chiến', 'LMS-DB', 'Thiết kế cơ sở dữ liệu, index, transaction và tối ưu truy vấn.', 24, 1, NOW(), NOW()),
('RESTful API Design', 'LMS-API', 'Thiết kế API, HTTP status, validation và tài liệu API.', 20, 1, NOW(), NOW()),
('DevOps căn bản', 'LMS-DEVOPS', 'Docker, CI/CD và triển khai ứng dụng.', 18, 1, NOW(), NOW()),
('Khóa học đã xóa', 'LMS-DELETED', 'Dữ liệu status 0 để kiểm tra xóa mềm.', 10, 0, NOW(), NOW());

-- Lưu ID để tạo quan hệ dễ đọc.
SET @s_anh  := (SELECT id FROM students WHERE email = 'minhanh@lms-seed.test');
SET @s_ha   := (SELECT id FROM students WHERE email = 'thuha@lms-seed.test');
SET @s_nam  := (SELECT id FROM students WHERE email = 'hoangnam@lms-seed.test');
SET @s_chi  := (SELECT id FROM students WHERE email = 'quynhchi@lms-seed.test');
SET @s_long := (SELECT id FROM students WHERE email = 'duclong@lms-seed.test');
SET @s_mai  := (SELECT id FROM students WHERE email = 'ngocmai@lms-seed.test');

SET @c_java   := (SELECT id FROM courses WHERE code = 'LMS-JAVA');
SET @c_vue    := (SELECT id FROM courses WHERE code = 'LMS-VUE');
SET @c_db     := (SELECT id FROM courses WHERE code = 'LMS-DB');
SET @c_api    := (SELECT id FROM courses WHERE code = 'LMS-API');
SET @c_devops := (SELECT id FROM courses WHERE code = 'LMS-DEVOPS');

-- =========================================================
-- 4. Bài học
-- =========================================================

INSERT INTO lessons (course_id, title, description, status, created_date, modified_date) VALUES
(@c_java, 'Giới thiệu Spring Boot', 'Cấu trúc project và Dependency Injection.', 1, NOW(), NOW()),
(@c_java, 'Spring Data JPA', 'Entity, Repository và quan hệ dữ liệu.', 1, NOW(), NOW()),
(@c_java, 'Validation và Exception Handling', 'Validate request và xử lý lỗi tập trung.', 1, NOW(), NOW()),
(@c_java, 'Bài học Java đã xóa', 'Dùng kiểm tra status bằng 0.', 0, NOW(), NOW()),

(@c_vue, 'Vue 3 Composition API', 'ref, reactive, computed và lifecycle.', 1, NOW(), NOW()),
(@c_vue, 'Element Plus', 'Table, Form, Dialog và Upload.', 1, NOW(), NOW()),
(@c_vue, 'Kết nối REST API', 'Axios, interceptor và xử lý response.', 1, NOW(), NOW()),

(@c_db, 'Thiết kế bảng và quan hệ', 'Primary key, foreign key và chuẩn hóa.', 1, NOW(), NOW()),
(@c_db, 'Index và tối ưu truy vấn', 'Phân tích execution plan.', 1, NOW(), NOW()),
(@c_db, 'Transaction trong MariaDB', 'Commit, rollback và isolation level.', 1, NOW(), NOW()),

(@c_api, 'Nguyên tắc REST', 'Resource, URI và HTTP method.', 1, NOW(), NOW()),
(@c_api, 'HTTP Status và Error Response', 'Chuẩn hóa response thành công và thất bại.', 1, NOW(), NOW()),
(@c_api, 'Pagination và Filter', 'Thiết kế API tìm kiếm có phân trang.', 1, NOW(), NOW()),

(@c_devops, 'Docker căn bản', 'Image, container và Dockerfile.', 1, NOW(), NOW()),
(@c_devops, 'CI/CD Pipeline', 'Build, test và deploy tự động.', 1, NOW(), NOW());

-- =========================================================
-- 5. Đăng ký học
-- =========================================================

INSERT INTO enrollments
    (student_id, course_id, enrolled_date, status, created_date, modified_date)
VALUES
(@s_anh,  @c_java, DATE_SUB(CURDATE(), INTERVAL 30 DAY), 1, NOW(), NOW()),
(@s_anh,  @c_vue,  DATE_SUB(CURDATE(), INTERVAL 20 DAY), 1, NOW(), NOW()),
(@s_ha,   @c_java, DATE_SUB(CURDATE(), INTERVAL 25 DAY), 1, NOW(), NOW()),
(@s_ha,   @c_db,   DATE_SUB(CURDATE(), INTERVAL 15 DAY), 1, NOW(), NOW()),
(@s_nam,  @c_java, DATE_SUB(CURDATE(), INTERVAL 18 DAY), 1, NOW(), NOW()),
(@s_nam,  @c_api,  DATE_SUB(CURDATE(), INTERVAL 10 DAY), 1, NOW(), NOW()),
(@s_chi,  @c_vue,  DATE_SUB(CURDATE(), INTERVAL 12 DAY), 1, NOW(), NOW()),
(@s_chi,  @c_api,  DATE_SUB(CURDATE(), INTERVAL 8 DAY),  1, NOW(), NOW()),
(@s_long, @c_db,   DATE_SUB(CURDATE(), INTERVAL 9 DAY),  1, NOW(), NOW()),
(@s_mai,  @c_java, DATE_SUB(CURDATE(), INTERVAL 7 DAY),  1, NOW(), NOW()),
(@s_mai,  @c_vue,  DATE_SUB(CURDATE(), INTERVAL 6 DAY),  1, NOW(), NOW()),
(@s_long, @c_api,  DATE_SUB(CURDATE(), INTERVAL 5 DAY),  0, NOW(), NOW());

-- =========================================================
-- 6. Images: avatar, thumbnail, gallery, ảnh/video bài học
-- URL là dữ liệu mẫu. Muốn preview file thật, đặt file tương ứng vào uploads/seed/.
-- =========================================================

-- Avatar học viên.
INSERT INTO images
    (object_type, object_id, media_type, media_role, url, original_name, content_type, status, created_date, modified_date)
SELECT 'STUDENT', id, 'IMAGE', 'AVATAR',
       CASE MOD(id, 5)
           WHEN 0 THEN '/uploads/seed/assets/totoro.gif'
           WHEN 1 THEN '/uploads/seed/assets/vit.gif'
           WHEN 2 THEN '/uploads/seed/assets/meo.gif'
           WHEN 3 THEN '/uploads/seed/assets/medical-tech.jpg'
           ELSE '/uploads/seed/assets/medical-team.png'
       END,
       CASE MOD(id, 5)
           WHEN 0 THEN 'totoro.gif' WHEN 1 THEN 'vit.gif' WHEN 2 THEN 'meo.gif'
           WHEN 3 THEN 'medical-tech.jpg' ELSE 'medical-team.png'
       END,
       CASE WHEN MOD(id, 5) IN (0, 1, 2) THEN 'image/gif'
            WHEN MOD(id, 5) = 3 THEN 'image/jpeg' ELSE 'image/png' END,
       1, NOW(), NOW()
FROM students
WHERE email LIKE '%@lms-seed.test' AND status = 1;

-- Thumbnail khóa học.
INSERT INTO images
    (object_type, object_id, media_type, media_role, url, original_name, content_type, status, created_date, modified_date)
SELECT 'COURSE', id, 'IMAGE', 'THUMBNAIL',
       CASE MOD(id, 5)
           WHEN 0 THEN '/uploads/seed/assets/medical-tech.jpg'
           WHEN 1 THEN '/uploads/seed/assets/medical-team.png'
           WHEN 2 THEN '/uploads/seed/assets/totoro.gif'
           WHEN 3 THEN '/uploads/seed/assets/vit.gif'
           ELSE '/uploads/seed/assets/meo.gif'
       END,
       CASE MOD(id, 5)
           WHEN 0 THEN 'medical-tech.jpg' WHEN 1 THEN 'medical-team.png'
           WHEN 2 THEN 'totoro.gif' WHEN 3 THEN 'vit.gif' ELSE 'meo.gif'
       END,
       CASE WHEN MOD(id, 5) = 0 THEN 'image/jpeg'
            WHEN MOD(id, 5) = 1 THEN 'image/png' ELSE 'image/gif' END,
       1, NOW(), NOW()
FROM courses
WHERE code LIKE 'LMS-%' AND status = 1;

-- Hai ảnh gallery cho mỗi khóa học active.
INSERT INTO images
    (object_type, object_id, media_type, media_role, url, original_name, content_type, status, created_date, modified_date)
SELECT 'COURSE', id, 'IMAGE', 'GALLERY',
       '/uploads/seed/assets/totoro.gif', 'totoro.gif', 'image/gif', 1, NOW(), NOW()
FROM courses WHERE code LIKE 'LMS-%' AND status = 1
UNION ALL
SELECT 'COURSE', id, 'IMAGE', 'GALLERY',
       '/uploads/seed/assets/medical-team.png', 'medical-team.png', 'image/png', 1, NOW(), NOW()
FROM courses WHERE code LIKE 'LMS-%' AND status = 1;

-- Một ảnh gallery status 0 để test restoredImageIds.
INSERT INTO images
    (object_type, object_id, media_type, media_role, url, original_name, content_type, status, created_date, modified_date)
VALUES
('COURSE', @c_java, 'IMAGE', 'GALLERY', '/uploads/seed/assets/medical-tech.jpg',
 'medical-tech.jpg', 'image/jpeg', 0, NOW(), NOW());

-- Thumbnail cho mỗi bài học active.
INSERT INTO images
    (object_type, object_id, media_type, media_role, url, original_name, content_type, status, created_date, modified_date)
SELECT 'LESSON', l.id, 'IMAGE', 'THUMBNAIL',
       CASE MOD(l.id, 3)
           WHEN 0 THEN '/uploads/seed/assets/totoro.gif'
           WHEN 1 THEN '/uploads/seed/assets/vit.gif'
           ELSE '/uploads/seed/assets/meo.gif'
       END,
       CASE MOD(l.id, 3) WHEN 0 THEN 'totoro.gif' WHEN 1 THEN 'vit.gif' ELSE 'meo.gif' END,
       'image/gif', 1, NOW(), NOW()
FROM lessons l
JOIN courses c ON l.course_id = c.id
WHERE c.code LIKE 'LMS-%' AND l.status = 1;

-- Video cho mỗi bài học active.
INSERT INTO images
    (object_type, object_id, media_type, media_role, url, original_name, content_type, status, created_date, modified_date)
SELECT 'LESSON', l.id, 'VIDEO', 'LESSON_VIDEO',
       CASE MOD(l.id, 5)
           WHEN 0 THEN '/uploads/seed/assets/lesson-video-1.mp4'
           WHEN 1 THEN '/uploads/seed/assets/lesson-video-2.mp4'
           WHEN 2 THEN '/uploads/seed/assets/lesson-video-3.mp4'
           WHEN 3 THEN '/uploads/seed/assets/lesson-video-4.mp4'
           ELSE '/uploads/seed/assets/lesson-video-5.mp4'
       END,
       CASE MOD(l.id, 5)
           WHEN 0 THEN 'lesson-video-1.mp4'
           WHEN 1 THEN 'lesson-video-2.mp4'
           WHEN 2 THEN 'lesson-video-3.mp4'
           WHEN 3 THEN 'lesson-video-4.mp4'
           ELSE 'lesson-video-5.mp4'
       END,
       'video/mp4', 1, NOW(), NOW()
FROM lessons l
JOIN courses c ON l.course_id = c.id
WHERE c.code LIKE 'LMS-%' AND l.status = 1;

-- Ảnh nội dung phụ cho các bài học active.
INSERT INTO images
    (object_type, object_id, media_type, media_role, url, original_name, content_type, status, created_date, modified_date)
SELECT 'LESSON', l.id, 'IMAGE', 'LESSON_IMAGE',
       CASE WHEN MOD(l.id, 2) = 0
            THEN '/uploads/seed/assets/medical-team.png'
            ELSE '/uploads/seed/assets/medical-tech.jpg' END,
       CASE WHEN MOD(l.id, 2) = 0 THEN 'medical-team.png' ELSE 'medical-tech.jpg' END,
       CASE WHEN MOD(l.id, 2) = 0 THEN 'image/png' ELSE 'image/jpeg' END,
       1, NOW(), NOW()
FROM lessons l
JOIN courses c ON l.course_id = c.id
WHERE c.code LIKE 'LMS-%' AND l.status = 1;

COMMIT;

-- =========================================================
-- 7. Thống kê nhanh sau khi seed
-- =========================================================

SELECT 'students_active' AS dataset, COUNT(*) AS total
FROM students WHERE email LIKE '%@lms-seed.test' AND status = 1
UNION ALL
SELECT 'courses_active', COUNT(*) FROM courses WHERE code LIKE 'LMS-%' AND status = 1
UNION ALL
SELECT 'lessons_active', COUNT(*) FROM lessons l JOIN courses c ON l.course_id = c.id
    WHERE c.code LIKE 'LMS-%' AND l.status = 1
UNION ALL
SELECT 'enrollments_active', COUNT(*) FROM enrollments e JOIN courses c ON e.course_id = c.id
    WHERE c.code LIKE 'LMS-%' AND e.status = 1
UNION ALL
SELECT 'images_active', COUNT(*) FROM images WHERE status = 1
    AND ((object_type = 'STUDENT' AND object_id IN (SELECT id FROM students WHERE email LIKE '%@lms-seed.test'))
      OR (object_type = 'COURSE' AND object_id IN (SELECT id FROM courses WHERE code LIKE 'LMS-%'))
      OR (object_type = 'LESSON' AND object_id IN (
          SELECT l.id FROM lessons l JOIN courses c ON l.course_id = c.id WHERE c.code LIKE 'LMS-%')));
