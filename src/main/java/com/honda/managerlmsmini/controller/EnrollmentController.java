package com.honda.managerlmsmini.controller;

import com.honda.managerlmsmini.dto.ApiResponse;
import com.honda.managerlmsmini.dto.PageResponse;
import com.honda.managerlmsmini.dto.enrollment.*;
import com.honda.managerlmsmini.service.EnrollmentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.context.MessageSource;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EnrollmentController {
    EnrollmentService enrollmentService;
    MessageSource messageSource;

    @PostMapping
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> create(
            @Valid @RequestBody EnrollmentCreateRequest request, Locale locale) {
        List<EnrollmentResponse> data = enrollmentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(201, messageSource.getMessage("success.created", null, locale), data));
    }

    @GetMapping("/{id}")
    public ApiResponse<EnrollmentResponse> getById(
            @PathVariable @Positive(message = "{validation.id.positive}") Long id, Locale locale) {
        return new ApiResponse<>(
                200, messageSource.getMessage("success.fetched", null, locale), enrollmentService.getById(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<EnrollmentResponse> update(
            @PathVariable @Positive(message = "{validation.id.positive}") Long id,
            @Valid @RequestBody EnrollmentUpdateRequest request,
            Locale locale) {
        return new ApiResponse<>(
                200, messageSource.getMessage("success.updated", null, locale), enrollmentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @PathVariable @Positive(message = "{validation.id.positive}") Long id, Locale locale) {
        enrollmentService.softDelete(id);
        return new ApiResponse<>(200, messageSource.getMessage("success.deleted", null, locale), null);
    }

    @GetMapping("/course/{courseId}")
    public ApiResponse<PageResponse<EnrollmentResponse>> findStudentsByCourse(
            @PathVariable @Positive(message = "{validation.id.positive}") Long courseId,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "{validation.page.min}") int page,
            @RequestParam(defaultValue = "10")
                    @Min(value = 1, message = "{validation.size.min}")
                    @Max(value = 100, message = "{validation.size.max}")
                    int size,
            Locale locale) {
        return new ApiResponse<>(
                200,
                messageSource.getMessage("success.fetched", null, locale),
                enrollmentService.findStudentsByCourse(courseId, page, size));
    }
}
