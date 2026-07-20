package com.honda.managerlmsmini.service;

import com.honda.managerlmsmini.dto.PageResponse;
import com.honda.managerlmsmini.dto.lesson.*;

public interface LessonService {
    LessonResponse create(LessonCreateRequest request);

    LessonResponse getById(Long id);

    PageResponse<LessonResponse> findByCourse(Long courseId, int page, int size);

    LessonResponse update(Long id, LessonUpdateRequest request);

    void softDelete(Long id);
}
