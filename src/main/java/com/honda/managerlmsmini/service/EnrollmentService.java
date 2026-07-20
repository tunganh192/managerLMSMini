package com.honda.managerlmsmini.service;

import com.honda.managerlmsmini.dto.PageResponse;
import com.honda.managerlmsmini.dto.enrollment.*;
import java.util.List;

public interface EnrollmentService {
    List<EnrollmentResponse> create(EnrollmentCreateRequest request);

    EnrollmentResponse getById(Long id);

    EnrollmentResponse update(Long id, EnrollmentUpdateRequest request);

    void softDelete(Long id);

    PageResponse<EnrollmentResponse> findStudentsByCourse(Long courseId, int page, int size);
}
