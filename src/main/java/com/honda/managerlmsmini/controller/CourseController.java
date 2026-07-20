package com.honda.managerlmsmini.controller;

import com.honda.managerlmsmini.dto.*;
import com.honda.managerlmsmini.dto.course.*;
import com.honda.managerlmsmini.service.CourseService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.context.MessageSource;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseController {
    CourseService courseService;
    MessageSource messageSource;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<CourseResponse>> create(
            @Valid @ModelAttribute CourseCreateRequest request, Locale locale) {
        CourseResponse data = courseService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(201, messageSource.getMessage("success.created", null, locale), data));
    }

    @GetMapping
    public ApiResponse<PageResponse<CourseResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) @Min(value = 1, message = "{validation.course.duration.min}")
                    Integer minDuration,
            @RequestParam(required = false) @Min(value = 1, message = "{validation.course.duration.min}")
                    Integer maxDuration,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "{validation.page.min}") int page,
            @RequestParam(defaultValue = "10")
                    @Min(value = 1, message = "{validation.size.min}")
                    @Max(value = 100, message = "{validation.size.max}")
                    int size,
            Locale locale) {
        return new ApiResponse<>(
                200,
                messageSource.getMessage("success.fetched", null, locale),
                courseService.search(keyword, name, code, minDuration, maxDuration, page, size));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) @Min(value = 1, message = "{validation.course.duration.min}")
                    Integer minDuration,
            @RequestParam(required = false) @Min(value = 1, message = "{validation.course.duration.min}")
                    Integer maxDuration) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=courses.xlsx")
                .contentType(
                        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(courseService.export(keyword, name, code, minDuration, maxDuration));
    }

    @GetMapping("/options")
    public ApiResponse<List<CourseResponse>> options(Locale locale) {
        return new ApiResponse<>(
                200, messageSource.getMessage("success.fetched", null, locale), courseService.getOptions());
    }

    @GetMapping("/{id}")
    public ApiResponse<CourseResponse> get(
            @PathVariable @Positive(message = "{validation.id.positive}") Long id, Locale locale) {
        return new ApiResponse<>(
                200, messageSource.getMessage("success.fetched", null, locale), courseService.getById(id));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<CourseResponse> update(
            @PathVariable @Positive(message = "{validation.id.positive}") Long id,
            @Valid @ModelAttribute CourseUpdateRequest request,
            Locale locale) {
        return new ApiResponse<>(
                200, messageSource.getMessage("success.updated", null, locale), courseService.update(id, request));
    }

    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<CourseResponse> addImages(
            @PathVariable @Positive(message = "{validation.id.positive}") Long id,
            @RequestPart("images") @Size(min = 1, max = 10, message = "{validation.image.size}")
                    List<MultipartFile> images,
            Locale locale) {
        return new ApiResponse<>(
                200, messageSource.getMessage("success.uploaded", null, locale), courseService.addImages(id, images));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @PathVariable @Positive(message = "{validation.id.positive}") Long id, Locale locale) {
        courseService.softDelete(id);
        return new ApiResponse<>(200, messageSource.getMessage("success.deleted", null, locale), null);
    }
}
