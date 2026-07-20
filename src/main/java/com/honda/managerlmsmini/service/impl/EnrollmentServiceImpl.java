package com.honda.managerlmsmini.service.impl;

import com.honda.managerlmsmini.dto.PageResponse;
import com.honda.managerlmsmini.dto.common.ImageResponse;
import com.honda.managerlmsmini.dto.enrollment.*;
import com.honda.managerlmsmini.entity.*;
import com.honda.managerlmsmini.enums.*;
import com.honda.managerlmsmini.exception.*;
import com.honda.managerlmsmini.mapper.EnrollmentMapper;
import com.honda.managerlmsmini.repository.*;
import com.honda.managerlmsmini.service.*;
import java.time.LocalDate;
import java.util.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EnrollmentServiceImpl implements EnrollmentService {
    private record EnrollmentKey(Long studentId, Long courseId) {}

    EnrollmentRepository enrollmentRepository;
    StudentRepository studentRepository;
    CourseRepository courseRepository;
    EnrollmentMapper enrollmentMapper;
    ImageService imageService;

    @Override
    @Transactional
    public List<EnrollmentResponse> create(EnrollmentCreateRequest request) {
        List<Long> studentIds = new ArrayList<>(new LinkedHashSet<>(request.getStudentIds()));
        List<Long> courseIds = new ArrayList<>(new LinkedHashSet<>(request.getCourseIds()));
        Map<Long, Student> students = studentRepository.findAllByIdInAndStatus(studentIds, 1).stream()
                .collect(java.util.stream.Collectors.toMap(Student::getId, student -> student));
        if (students.size() != studentIds.size()) throw new AppException(ErrorCode.STUDENT_NOT_FOUND);
        Map<Long, Course> courses = courseRepository.findAllByIdInAndStatus(courseIds, 1).stream()
                .collect(java.util.stream.Collectors.toMap(Course::getId, course -> course));
        if (courses.size() != courseIds.size()) throw new AppException(ErrorCode.COURSE_NOT_FOUND);

        Map<EnrollmentKey, Enrollment> existingEnrollments =
                enrollmentRepository.findByStudentsAndCourses(studentIds, courseIds).stream()
                        .collect(java.util.stream.Collectors.toMap(
                                enrollment -> new EnrollmentKey(
                                        enrollment.getStudent().getId(),
                                        enrollment.getCourse().getId()),
                                enrollment -> enrollment));
        Map<Long, List<ImageResponse>> avatarImages = imageService.findActive(ObjectType.STUDENT, studentIds);
        List<Enrollment> enrollments = new ArrayList<>();
        for (Long studentId : studentIds) {
            for (Long courseId : courseIds) {
                Enrollment enrollment =
                        existingEnrollments.getOrDefault(new EnrollmentKey(studentId, courseId), new Enrollment());
                enrollment.setStudent(students.get(studentId));
                enrollment.setCourse(courses.get(courseId));
                enrollment.setStatus(1);
                enrollment.setEnrolledDate(LocalDate.now());
                enrollments.add(enrollment);
            }
        }
        return enrollmentRepository.saveAll(enrollments).stream()
                .map(enrollment -> {
                    EnrollmentResponse response = enrollmentMapper.toResponse(enrollment);
                    response.setAvatarUrl(imageService.findFirstUrl(
                            avatarImages.getOrDefault(enrollment.getStudent().getId(), List.of()), MediaRole.AVATAR));
                    return response;
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EnrollmentResponse getById(Long id) {
        Enrollment enrollment = enrollmentRepository
                .findActiveById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_FOUND));
        List<ImageResponse> images = imageService.findActive(
                ObjectType.STUDENT, enrollment.getStudent().getId());
        EnrollmentResponse response = enrollmentMapper.toResponse(enrollment);
        response.setAvatarUrl(imageService.findFirstUrl(images, MediaRole.AVATAR));
        return response;
    }

    @Override
    @Transactional
    public EnrollmentResponse update(Long id, EnrollmentUpdateRequest request) {
        Enrollment enrollment = enrollmentRepository
                .findByIdAndStatus(id, 1)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_FOUND));
        Optional<Enrollment> duplicateEnrollment =
                enrollmentRepository.findByStudentIdAndCourseId(request.getStudentId(), request.getCourseId());
        if (duplicateEnrollment.isPresent()
                && !duplicateEnrollment.get().getId().equals(id)
                && Integer.valueOf(1).equals(duplicateEnrollment.get().getStatus()))
            throw new AppException(ErrorCode.ENROLLMENT_EXISTED);
        Student student = studentRepository
                .findByIdAndStatus(request.getStudentId(), 1)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));
        Course course = courseRepository
                .findByIdAndStatus(request.getCourseId(), 1)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        Enrollment savedEnrollment = enrollmentRepository.saveAndFlush(enrollment);
        List<ImageResponse> images = imageService.findActive(
                ObjectType.STUDENT, savedEnrollment.getStudent().getId());
        EnrollmentResponse response = enrollmentMapper.toResponse(savedEnrollment);
        response.setAvatarUrl(imageService.findFirstUrl(images, MediaRole.AVATAR));
        return response;
    }

    @Override
    @Transactional
    public void softDelete(Long id) {
        Enrollment enrollment = enrollmentRepository
                .findByIdAndStatus(id, 1)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_FOUND));
        enrollment.setStatus(0);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<EnrollmentResponse> findStudentsByCourse(Long courseId, int page, int size) {
        courseRepository.findByIdAndStatus(courseId, 1).orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        Page<Enrollment> enrollmentPage = enrollmentRepository.findActiveByCourse(
                courseId, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        List<Enrollment> enrollments = enrollmentPage.getContent();
        Map<Long, List<ImageResponse>> images = enrollments.isEmpty()
                ? Map.of()
                : imageService.findActive(
                        ObjectType.STUDENT,
                        enrollments.stream()
                                .map(enrollment -> enrollment.getStudent().getId())
                                .distinct()
                                .toList());
        List<EnrollmentResponse> responses = enrollments.stream()
                .map(enrollment -> {
                    List<ImageResponse> studentImages =
                            images.getOrDefault(enrollment.getStudent().getId(), List.of());
                    EnrollmentResponse response = enrollmentMapper.toResponse(enrollment);
                    response.setAvatarUrl(imageService.findFirstUrl(studentImages, MediaRole.AVATAR));
                    return response;
                })
                .toList();
        return new PageResponse<>(
                responses, page, size, enrollmentPage.getTotalElements(), enrollmentPage.getTotalPages());
    }
}
