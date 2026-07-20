package com.honda.managerlmsmini.exception;

import com.honda.managerlmsmini.dto.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GlobalExceptionHandler {
    MessageSource messageSource;

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusiness(AppException exception, Locale locale) {
        ErrorCode errorCode = exception.getErrorCode();
        String message = messageSource.getMessage(errorCode.getMessageKey(), null, errorCode.getMessageKey(), locale);
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(new ApiResponse<>(errorCode.getCode(), message, null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(
            MethodArgumentNotValidException exception, Locale locale) {
        Map<String, String> errors = exception.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> {
                            if (error.isBindingFailure()) {
                                String fieldName = error.getField();
                                int bracket = fieldName.indexOf('[');
                                String cleanFieldName = bracket < 0 ? fieldName : fieldName.substring(0, bracket);
                                String messageKey =
                                        switch (cleanFieldName) {
                                            case "id",
                                                    "studentId",
                                                    "studentIds",
                                                    "courseId",
                                                    "courseIds" -> "validation.id.type";
                                            case "page" -> "validation.page.type";
                                            case "size" -> "validation.size.type";
                                            case "duration", "minDuration", "maxDuration" -> "validation.number.type";
                                            case "avatar", "thumbnail" -> "validation.image.type";
                                            case "video" -> "validation.video.type";
                                            default -> "validation.type.invalid";
                                        };
                                return messageSource.getMessage(
                                        messageKey,
                                        null,
                                        messageSource.getMessage(
                                                "validation.type.invalid", null, "Invalid value", locale),
                                        locale);
                            }
                            String value = error.getDefaultMessage();
                            if (value == null) return "";
                            String messageKey = value.startsWith("{") && value.endsWith("}")
                                    ? value.substring(1, value.length() - 1)
                                    : value;
                            return messageSource.getMessage(messageKey, null, value, locale);
                        },
                        (left, right) -> left));
        ErrorCode errorCode = ErrorCode.INVALID_REQUEST;
        String message = messageSource.getMessage(errorCode.getMessageKey(), null, errorCode.getMessageKey(), locale);
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(new ApiResponse<>(errorCode.getCode(), message, errors));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleBind(BindException exception, Locale locale) {
        Map<String, String> errors = exception.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        error -> {
                            String fieldName = error.getField();
                            int bracket = fieldName.indexOf('[');
                            return bracket < 0 ? fieldName : fieldName.substring(0, bracket);
                        },
                        error -> {
                            if (error.isBindingFailure()) {
                                String fieldName = error.getField();
                                int bracket = fieldName.indexOf('[');
                                String cleanFieldName = bracket < 0 ? fieldName : fieldName.substring(0, bracket);
                                String messageKey =
                                        switch (cleanFieldName) {
                                            case "id",
                                                    "studentId",
                                                    "studentIds",
                                                    "courseId",
                                                    "courseIds" -> "validation.id.type";
                                            case "page" -> "validation.page.type";
                                            case "size" -> "validation.size.type";
                                            case "duration", "minDuration", "maxDuration" -> "validation.number.type";
                                            case "avatar", "thumbnail" -> "validation.image.type";
                                            case "video" -> "validation.video.type";
                                            default -> "validation.type.invalid";
                                        };
                                return messageSource.getMessage(
                                        messageKey,
                                        null,
                                        messageSource.getMessage(
                                                "validation.type.invalid", null, "Invalid value", locale),
                                        locale);
                            }
                            String value = error.getDefaultMessage();
                            if (value == null) return "";
                            String messageKey = value.startsWith("{") && value.endsWith("}")
                                    ? value.substring(1, value.length() - 1)
                                    : value;
                            return messageSource.getMessage(messageKey, null, value, locale);
                        },
                        (left, right) -> left));
        ErrorCode errorCode = ErrorCode.INVALID_REQUEST;
        String message = messageSource.getMessage(errorCode.getMessageKey(), null, errorCode.getMessageKey(), locale);
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(new ApiResponse<>(errorCode.getCode(), message, errors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleConstraint(
            ConstraintViolationException exception, Locale locale) {
        Map<String, String> errors = exception.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> {
                            String path = violation.getPropertyPath().toString();
                            int separator = path.lastIndexOf('.');
                            return separator < 0 ? path : path.substring(separator + 1);
                        },
                        violation -> {
                            String value = violation.getMessage();
                            if (value == null) return "";
                            String messageKey = value.startsWith("{") && value.endsWith("}")
                                    ? value.substring(1, value.length() - 1)
                                    : value;
                            return messageSource.getMessage(messageKey, null, value, locale);
                        },
                        (left, right) -> left));
        ErrorCode errorCode = ErrorCode.INVALID_REQUEST;
        String message = messageSource.getMessage(errorCode.getMessageKey(), null, errorCode.getMessageKey(), locale);
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(new ApiResponse<>(errorCode.getCode(), message, errors));
    }

    @ExceptionHandler({
        MissingServletRequestParameterException.class,
        HttpMediaTypeNotSupportedException.class,
        HttpMessageNotReadableException.class,
        MissingServletRequestPartException.class,
        MultipartException.class
    })
    public ResponseEntity<ApiResponse<Object>> handleBadRequest(Exception exception, Locale locale) {
        ErrorCode errorCode = ErrorCode.INVALID_REQUEST;
        String message = messageSource.getMessage(errorCode.getMessageKey(), null, errorCode.getMessageKey(), locale);
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(new ApiResponse<>(errorCode.getCode(), message, null));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception, Locale locale) {
        ErrorCode errorCode = ErrorCode.INVALID_REQUEST;
        Map<String, String> errors = new HashMap<>();
        String fieldName = exception.getName();
        int bracket = fieldName.indexOf('[');
        String cleanFieldName = bracket < 0 ? fieldName : fieldName.substring(0, bracket);
        String messageKey =
                switch (cleanFieldName) {
                    case "id", "studentId", "studentIds", "courseId", "courseIds" -> "validation.id.type";
                    case "page" -> "validation.page.type";
                    case "size" -> "validation.size.type";
                    case "duration", "minDuration", "maxDuration" -> "validation.number.type";
                    case "avatar", "thumbnail" -> "validation.image.type";
                    case "video" -> "validation.video.type";
                    default -> "validation.type.invalid";
                };
        errors.put(
                exception.getName(),
                messageSource.getMessage(
                        messageKey,
                        null,
                        messageSource.getMessage("validation.type.invalid", null, "Invalid value", locale),
                        locale));
        String message = messageSource.getMessage(errorCode.getMessageKey(), null, errorCode.getMessageKey(), locale);
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(new ApiResponse<>(errorCode.getCode(), message, errors));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException exception, Locale locale) {
        ErrorCode errorCode = ErrorCode.METHOD_NOT_ALLOWED;
        String message = messageSource.getMessage(errorCode.getMessageKey(), null, errorCode.getMessageKey(), locale);
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(new ApiResponse<>(errorCode.getCode(), message, null));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNoResource(NoResourceFoundException exception, Locale locale) {
        ErrorCode errorCode = ErrorCode.API_NOT_FOUND;
        String message = messageSource.getMessage(errorCode.getMessageKey(), null, errorCode.getMessageKey(), locale);
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(new ApiResponse<>(errorCode.getCode(), message, null));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrity(
            DataIntegrityViolationException exception, Locale locale) {
        ErrorCode errorCode = ErrorCode.DATA_CONFLICT;
        String message = messageSource.getMessage(errorCode.getMessageKey(), null, errorCode.getMessageKey(), locale);
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(new ApiResponse<>(errorCode.getCode(), message, null));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Object>> handleUploadSize(
            MaxUploadSizeExceededException exception, Locale locale) {
        ErrorCode errorCode = ErrorCode.FILE_TOO_LARGE;
        String message = messageSource.getMessage(errorCode.getMessageKey(), null, errorCode.getMessageKey(), locale);
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(new ApiResponse<>(errorCode.getCode(), message, null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleUnexpected(Exception exception, Locale locale) {
        log.error("Unexpected application error", exception);
        ErrorCode errorCode = ErrorCode.UNCATEGORIZED_EXCEPTION;
        String message = messageSource.getMessage(errorCode.getMessageKey(), null, errorCode.getMessageKey(), locale);
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(new ApiResponse<>(errorCode.getCode(), message, null));
    }
}
