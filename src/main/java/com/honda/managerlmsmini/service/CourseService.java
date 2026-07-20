package com.honda.managerlmsmini.service;

import com.honda.managerlmsmini.dto.PageResponse;
import com.honda.managerlmsmini.dto.course.*;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface CourseService {
    CourseResponse create(CourseCreateRequest request);

    PageResponse<CourseResponse> search(
            String keyword, String name, String code, Integer minDuration, Integer maxDuration, int page, int size);

    List<CourseResponse> getOptions();

    CourseResponse getById(Long id);

    CourseResponse update(Long id, CourseUpdateRequest request);

    CourseResponse addImages(Long id, List<MultipartFile> images);

    void softDelete(Long id);

    byte[] export(String keyword, String name, String code, Integer minDuration, Integer maxDuration);
}
