package com.honda.managerlmsmini.service;

import com.honda.managerlmsmini.dto.PageResponse;
import com.honda.managerlmsmini.dto.student.*;
import java.util.List;

public interface StudentService {
    StudentResponse create(StudentCreateRequest request);

    PageResponse<StudentResponse> search(String keyword, String name, String email, String phone, int page, int size);

    List<StudentResponse> getOptions();

    StudentResponse getById(Long id);

    StudentResponse update(Long id, StudentUpdateRequest request);

    void softDelete(Long id);

    byte[] export(String keyword, String name, String email, String phone);
}
