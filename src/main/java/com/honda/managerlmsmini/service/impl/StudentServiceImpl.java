package com.honda.managerlmsmini.service.impl;

import com.honda.managerlmsmini.dto.PageResponse;
import com.honda.managerlmsmini.dto.common.ImageResponse;
import com.honda.managerlmsmini.dto.student.*;
import com.honda.managerlmsmini.entity.*;
import com.honda.managerlmsmini.enums.*;
import com.honda.managerlmsmini.exception.*;
import com.honda.managerlmsmini.mapper.StudentMapper;
import com.honda.managerlmsmini.repository.*;
import com.honda.managerlmsmini.service.*;
import java.util.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StudentServiceImpl implements StudentService {
    StudentRepository studentRepository;
    EnrollmentRepository enrollmentRepository;
    StudentMapper studentMapper;
    ImageService imageService;
    ExcelService excelService;

    @Override
    @Transactional
    public StudentResponse create(StudentCreateRequest request) {
        if (request.getAvatar() == null || request.getAvatar().isEmpty())
            throw new AppException(ErrorCode.STUDENT_AVATAR_REQUIRED);
        request.setName(request.getName().trim());
        request.setEmail(request.getEmail().trim().toLowerCase());
        if (studentRepository.existsByEmailIgnoreCase(request.getEmail()))
            throw new AppException(ErrorCode.STUDENT_EMAIL_EXISTED);
        Student savedStudent = studentRepository.saveAndFlush(studentMapper.toEntity(request));
        if (request.getAvatar() != null && !request.getAvatar().isEmpty())
            imageService.addFiles(
                    ObjectType.STUDENT,
                    savedStudent.getId(),
                    List.of(request.getAvatar()),
                    MediaType.IMAGE,
                    MediaRole.AVATAR,
                    "students");
        List<ImageResponse> images = imageService.findActive(ObjectType.STUDENT, savedStudent.getId());
        StudentResponse response = studentMapper.toResponse(savedStudent);
        response.setAvatarUrl(imageService.findFirstUrl(images, MediaRole.AVATAR));
        return response;
    }

    @Override
    public PageResponse<StudentResponse> search(
            String keyword, String name, String email, String phone, int page, int size) {
        Page<Student> studentPage = studentRepository.search(
                keyword, name, email, phone, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        List<Student> students = studentPage.getContent();
        List<StudentResponse> responses;
        if (students.isEmpty()) {
            responses = List.of();
        } else {
            Map<Long, List<ImageResponse>> images = imageService.findActive(ObjectType.STUDENT, students.stream().map(Student::getId).toList());
            responses = students.stream()
                    .map(student -> {
                        List<ImageResponse> studentImages = images.getOrDefault(student.getId(), List.of());
                        StudentResponse response = studentMapper.toResponse(student);
                        response.setAvatarUrl(imageService.findFirstUrl(studentImages, MediaRole.AVATAR));
                        return response;
                    })
                    .toList();
        }
        return new PageResponse<>(responses, page, size, studentPage.getTotalElements(), studentPage.getTotalPages());
    }

    @Override
    public List<StudentResponse> getOptions() {
        List<Student> students = studentRepository.findAllByStatusOrderByIdDesc(1);
        if (students.isEmpty()) return List.of();
        Map<Long, List<ImageResponse>> images = imageService.findActive(
                ObjectType.STUDENT, students.stream().map(Student::getId).toList());
        return students.stream()
                .map(student -> {
                    List<ImageResponse> studentImages = images.getOrDefault(student.getId(), List.of());
                    StudentResponse response = studentMapper.toResponse(student);
                    response.setAvatarUrl(imageService.findFirstUrl(studentImages, MediaRole.AVATAR));
                    return response;
                })
                .toList();
    }

    @Override
    public StudentResponse getById(Long id) {
        Student student = studentRepository
                .findByIdAndStatus(id, 1)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));
        List<ImageResponse> images = imageService.findActive(ObjectType.STUDENT, id);
        StudentResponse response = studentMapper.toResponse(student);
        response.setAvatarUrl(imageService.findFirstUrl(images, MediaRole.AVATAR));
        return response;
    }

    @Override
    @Transactional
    public StudentResponse update(Long id, StudentUpdateRequest request) {
        request.setName(request.getName().trim());
        request.setEmail(request.getEmail().trim().toLowerCase());
        Student student = studentRepository
                .findByIdAndStatus(id, 1)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));
        if (studentRepository.existsByEmailIgnoreCaseAndIdNot(request.getEmail(), id))
            throw new AppException(ErrorCode.STUDENT_EMAIL_EXISTED);
        studentMapper.updateEntity(request, student);
        imageService.updateStatuses(
                ObjectType.STUDENT, id, request.getDeletedImageIds(), request.getRestoredImageIds());
        if (request.getAvatar() != null && !request.getAvatar().isEmpty())
            imageService.softDeleteAll(ObjectType.STUDENT, id, MediaRole.AVATAR);
        if (request.getAvatar() != null && !request.getAvatar().isEmpty())
            imageService.addFiles(
                    ObjectType.STUDENT,
                    id,
                    List.of(request.getAvatar()),
                    MediaType.IMAGE,
                    MediaRole.AVATAR,
                    "students");
        Student savedStudent = studentRepository.saveAndFlush(student);
        List<ImageResponse> images = imageService.findActive(ObjectType.STUDENT, id);
        StudentResponse response = studentMapper.toResponse(savedStudent);
        response.setAvatarUrl(imageService.findFirstUrl(images, MediaRole.AVATAR));
        return response;
    }

    @Override
    @Transactional
    public void softDelete(Long id) {
        Student student = studentRepository
                .findByIdAndStatus(id, 1)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));
        student.setStatus(0);
        enrollmentRepository.softDeleteByStudentId(id);
        imageService.softDeleteAll(ObjectType.STUDENT, id);
    }

    @Override
    public byte[] export(String keyword, String name, String email, String phone) {
        Page<Student> result = studentRepository.search(
                keyword,
                name,
                email,
                phone,
                Pageable.unpaged(Sort.by(Sort.Direction.DESC, "id")));
        return excelService.students(result.getContent());
    }
}
