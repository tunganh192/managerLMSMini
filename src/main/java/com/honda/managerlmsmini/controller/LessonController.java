package com.honda.managerlmsmini.controller;

import com.honda.managerlmsmini.dto.ApiResponse;
import com.honda.managerlmsmini.dto.PageResponse;
import com.honda.managerlmsmini.dto.lesson.*;
import com.honda.managerlmsmini.service.LessonService;
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
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LessonController {
    LessonService lessonService;
    MessageSource messageSource;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<LessonResponse>> create(
            @Valid @ModelAttribute LessonCreateRequest request, Locale locale) {
        LessonResponse data = lessonService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(201, messageSource.getMessage("success.created", null, locale), data));
    }

    @GetMapping("/course/{courseId}")
    public ApiResponse<PageResponse<LessonResponse>> findByCourse(
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
                lessonService.findByCourse(courseId, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<LessonResponse> getById(
            @PathVariable @Positive(message = "{validation.id.positive}") Long id, Locale locale) {
        return new ApiResponse<>(
                200, messageSource.getMessage("success.fetched", null, locale), lessonService.getById(id));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<LessonResponse> update(
            @PathVariable @Positive(message = "{validation.id.positive}") Long id,
            @Valid @ModelAttribute LessonUpdateRequest request,
            Locale locale) {
        return new ApiResponse<>(
                200, messageSource.getMessage("success.updated", null, locale), lessonService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @PathVariable @Positive(message = "{validation.id.positive}") Long id, Locale locale) {
        lessonService.softDelete(id);
        return new ApiResponse<>(200, messageSource.getMessage("success.deleted", null, locale), null);
    }
}
