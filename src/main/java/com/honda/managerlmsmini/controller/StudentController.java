package com.honda.managerlmsmini.controller;

import com.honda.managerlmsmini.dto.*;
import com.honda.managerlmsmini.dto.student.*;
import com.honda.managerlmsmini.service.StudentService;
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
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StudentController {
    StudentService studentService;
    MessageSource messageSource;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<StudentResponse>> create(
            @Valid @ModelAttribute StudentCreateRequest request, Locale locale) {
        StudentResponse data = studentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(201, messageSource.getMessage("success.created", null, locale), data));
    }

    @GetMapping
    public ApiResponse<PageResponse<StudentResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "{validation.page.min}") int page,
            @RequestParam(defaultValue = "10")
                    @Min(value = 1, message = "{validation.size.min}")
                    @Max(value = 100, message = "{validation.size.max}")
                    int size,
            Locale locale) {
        return new ApiResponse<>(
                200,
                messageSource.getMessage("success.fetched", null, locale),
                studentService.search(keyword, name, email, phone, page, size));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=students.xlsx")
                .contentType(
                        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(studentService.export(keyword, name, email, phone));
    }

    @GetMapping("/options")
    public ApiResponse<List<StudentResponse>> options(Locale locale) {
        return new ApiResponse<>(
                200, messageSource.getMessage("success.fetched", null, locale), studentService.getOptions());
    }

    @GetMapping("/{id}")
    public ApiResponse<StudentResponse> get(
            @PathVariable @Positive(message = "{validation.id.positive}") Long id, Locale locale) {
        return new ApiResponse<>(
                200, messageSource.getMessage("success.fetched", null, locale), studentService.getById(id));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<StudentResponse> update(
            @PathVariable @Positive(message = "{validation.id.positive}") Long id,
            @Valid @ModelAttribute StudentUpdateRequest request,
            Locale locale) {
        return new ApiResponse<>(
                200, messageSource.getMessage("success.updated", null, locale), studentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @PathVariable @Positive(message = "{validation.id.positive}") Long id, Locale locale) {
        studentService.softDelete(id);
        return new ApiResponse<>(200, messageSource.getMessage("success.deleted", null, locale), null);
    }
}
