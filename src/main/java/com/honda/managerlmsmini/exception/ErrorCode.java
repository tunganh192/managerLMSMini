package com.honda.managerlmsmini.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "error.uncategorized", HttpStatus.INTERNAL_SERVER_ERROR),

    INVALID_REQUEST(1001, "error.validation", HttpStatus.BAD_REQUEST),
    FILE_TOO_LARGE(1002, "error.file.too_large", HttpStatus.CONTENT_TOO_LARGE),
    DATA_CONFLICT(1003, "error.data_conflict", HttpStatus.CONFLICT),
    METHOD_NOT_ALLOWED(1004, "error.method_not_allowed", HttpStatus.METHOD_NOT_ALLOWED),
    API_NOT_FOUND(1005, "error.api_not_found", HttpStatus.NOT_FOUND),
    FILTER_RANGE_INVALID(1006, "error.filter.range_invalid", HttpStatus.BAD_REQUEST),

    STUDENT_NOT_FOUND(2001, "error.student.not_found", HttpStatus.NOT_FOUND),
    STUDENT_EMAIL_EXISTED(2002, "error.student.email_existed", HttpStatus.CONFLICT),
    STUDENT_AVATAR_REQUIRED(2003, "error.student.avatar_required", HttpStatus.BAD_REQUEST),

    COURSE_NOT_FOUND(3001, "error.course.not_found", HttpStatus.NOT_FOUND),
    COURSE_CODE_EXISTED(3002, "error.course.code_existed", HttpStatus.CONFLICT),
    COURSE_HAS_STUDENTS(3003, "error.course.has_students", HttpStatus.CONFLICT),
    COURSE_IMAGE_LIMIT_EXCEEDED(3004, "error.course.image_limit", HttpStatus.BAD_REQUEST),
    COURSE_THUMBNAIL_REQUIRED(3005, "error.course.thumbnail_required", HttpStatus.BAD_REQUEST),

    LESSON_NOT_FOUND(4001, "error.lesson.not_found", HttpStatus.NOT_FOUND),
    LESSON_THUMBNAIL_REQUIRED(4002, "error.lesson.thumbnail_required", HttpStatus.BAD_REQUEST),
    LESSON_VIDEO_REQUIRED(4003, "error.lesson.video_required", HttpStatus.BAD_REQUEST),

    ENROLLMENT_NOT_FOUND(5001, "error.enrollment.not_found", HttpStatus.NOT_FOUND),
    ENROLLMENT_EXISTED(5002, "error.enrollment.existed", HttpStatus.CONFLICT),

    FILE_EMPTY(6001, "error.file.empty", HttpStatus.BAD_REQUEST),
    FILE_INVALID_TYPE(6002, "error.file.invalid_type", HttpStatus.BAD_REQUEST),
    FILE_STORE_ERROR(6003, "error.file.store", HttpStatus.INTERNAL_SERVER_ERROR),

    EXCEL_EXPORT_ERROR(7001, "error.excel.export", HttpStatus.INTERNAL_SERVER_ERROR);

    int code;
    String messageKey;
    HttpStatusCode httpStatus;
}
