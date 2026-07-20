package com.honda.managerlmsmini.service.impl;

import com.honda.managerlmsmini.dto.PageResponse;
import com.honda.managerlmsmini.dto.common.ImageResponse;
import com.honda.managerlmsmini.dto.course.*;
import com.honda.managerlmsmini.entity.*;
import com.honda.managerlmsmini.enums.*;
import com.honda.managerlmsmini.exception.*;
import com.honda.managerlmsmini.mapper.CourseMapper;
import com.honda.managerlmsmini.repository.*;
import com.honda.managerlmsmini.service.*;
import java.util.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseServiceImpl implements CourseService {
    CourseRepository courseRepository;
    EnrollmentRepository enrollmentRepository;
    LessonRepository lessonRepository;
    CourseMapper courseMapper;
    ImageService imageService;
    ExcelService excelService;

    @Override
    @Transactional
    public CourseResponse create(CourseCreateRequest request) {
        if (request.getThumbnail() == null || request.getThumbnail().isEmpty())
            throw new AppException(ErrorCode.COURSE_THUMBNAIL_REQUIRED);
        request.setName(request.getName().trim());
        request.setCode(request.getCode().trim().toUpperCase());
        if (courseRepository.existsByCodeIgnoreCaseAndStatus(request.getCode(), 1))
            throw new AppException(ErrorCode.COURSE_CODE_EXISTED);
        Course savedCourse = courseRepository.saveAndFlush(courseMapper.toEntity(request));
        long newImageCount = request.getImages() == null
                ? 0
                : request.getImages().stream()
                        .filter(file -> file != null && !file.isEmpty())
                        .count();
        long totalImages =
                imageService.countActive(ObjectType.COURSE, savedCourse.getId(), MediaRole.GALLERY) + newImageCount;
        if (totalImages > 10) throw new AppException(ErrorCode.COURSE_IMAGE_LIMIT_EXCEEDED);
        if (request.getThumbnail() != null && !request.getThumbnail().isEmpty())
            imageService.addFiles(
                    ObjectType.COURSE,
                    savedCourse.getId(),
                    List.of(request.getThumbnail()),
                    MediaType.IMAGE,
                    MediaRole.THUMBNAIL,
                    "courses/thumbnails");
        imageService.addFiles(
                ObjectType.COURSE,
                savedCourse.getId(),
                request.getImages(),
                MediaType.IMAGE,
                MediaRole.GALLERY,
                "courses/images");
        List<ImageResponse> images = imageService.findActive(ObjectType.COURSE, savedCourse.getId());
        CourseResponse response = courseMapper.toResponse(savedCourse);
        response.setImages(images);
        response.setThumbnailUrl(imageService.findFirstUrl(images, MediaRole.THUMBNAIL));
        return response;
    }

    @Override
    public PageResponse<CourseResponse> search(
            String keyword, String name, String code, Integer minDuration, Integer maxDuration, int page, int size) {
        if (minDuration != null && maxDuration != null && minDuration > maxDuration)
            throw new AppException(ErrorCode.FILTER_RANGE_INVALID);
        Page<Course> coursePage = courseRepository.search(
                keyword,
                name,
                code,
                minDuration,
                maxDuration,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        List<Course> courses = coursePage.getContent();
        List<CourseResponse> responses;
        if (courses.isEmpty()) {
            responses = List.of();
        } else {
            Map<Long, List<ImageResponse>> images = imageService.findActive(ObjectType.COURSE, courses.stream().map(Course::getId).toList());
            responses = courses.stream()
                    .map(course -> {
                        List<ImageResponse> courseImages = images.getOrDefault(course.getId(), List.of());
                        CourseResponse response = courseMapper.toResponse(course);
                        response.setImages(courseImages);
                        response.setThumbnailUrl(imageService.findFirstUrl(courseImages, MediaRole.THUMBNAIL));
                        return response;
                    })
                    .toList();
        }
        return new PageResponse<>(responses, page, size, coursePage.getTotalElements(), coursePage.getTotalPages());
    }

    @Override
    public List<CourseResponse> getOptions() {
        List<Course> courses = courseRepository.findAllByStatusOrderByIdDesc(1);
        if (courses.isEmpty()) return List.of();
        Map<Long, List<ImageResponse>> images = imageService.findActive(
                ObjectType.COURSE, courses.stream().map(Course::getId).toList());
        return courses.stream()
                .map(course -> {
                    List<ImageResponse> courseImages = images.getOrDefault(course.getId(), List.of());
                    CourseResponse response = courseMapper.toResponse(course);
                    response.setImages(courseImages);
                    response.setThumbnailUrl(imageService.findFirstUrl(courseImages, MediaRole.THUMBNAIL));
                    return response;
                })
                .toList();
    }

    @Override
    public CourseResponse getById(Long id) {
        Course course = courseRepository
                .findByIdAndStatus(id, 1)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        List<ImageResponse> images = imageService.findActive(ObjectType.COURSE, id);
        CourseResponse response = courseMapper.toResponse(course);
        response.setImages(images);
        response.setThumbnailUrl(imageService.findFirstUrl(images, MediaRole.THUMBNAIL));
        return response;
    }

    @Override
    @Transactional
    public CourseResponse update(Long id, CourseUpdateRequest request) {
        request.setName(request.getName().trim());
        request.setCode(request.getCode().trim().toUpperCase());
        Course course = courseRepository
                .findByIdAndStatus(id, 1)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        if (courseRepository.existsByCodeIgnoreCaseAndStatusAndIdNot(request.getCode(), 1, id))
            throw new AppException(ErrorCode.COURSE_CODE_EXISTED);
        courseMapper.updateEntity(request, course);
        imageService.updateStatuses(ObjectType.COURSE, id, request.getDeletedImageIds(), request.getRestoredImageIds());
        long newImageCount = request.getImages() == null
                ? 0
                : request.getImages().stream()
                        .filter(file -> file != null && !file.isEmpty())
                        .count();
        long totalImages = imageService.countActive(ObjectType.COURSE, id, MediaRole.GALLERY) + newImageCount;
        if (totalImages > 10) throw new AppException(ErrorCode.COURSE_IMAGE_LIMIT_EXCEEDED);
        if (request.getThumbnail() != null && !request.getThumbnail().isEmpty())
            imageService.softDeleteAll(ObjectType.COURSE, id, MediaRole.THUMBNAIL);
        if (request.getThumbnail() != null && !request.getThumbnail().isEmpty())
            imageService.addFiles(
                    ObjectType.COURSE,
                    id,
                    List.of(request.getThumbnail()),
                    MediaType.IMAGE,
                    MediaRole.THUMBNAIL,
                    "courses/thumbnails");
        imageService.addFiles(
                ObjectType.COURSE, id, request.getImages(), MediaType.IMAGE, MediaRole.GALLERY, "courses/images");
        Course savedCourse = courseRepository.saveAndFlush(course);
        List<ImageResponse> images = imageService.findActive(ObjectType.COURSE, id);
        CourseResponse response = courseMapper.toResponse(savedCourse);
        response.setImages(images);
        response.setThumbnailUrl(imageService.findFirstUrl(images, MediaRole.THUMBNAIL));
        return response;
    }

    @Override
    @Transactional
    public CourseResponse addImages(Long id, List<MultipartFile> images) {
        Course course = courseRepository
                .findByIdAndStatus(id, 1)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        List<MultipartFile> validImages = images == null
                ? List.of()
                : images.stream()
                        .filter(file -> file != null && !file.isEmpty())
                        .toList();
        if (validImages.isEmpty()) throw new AppException(ErrorCode.FILE_EMPTY);
        long totalImages = imageService.countActive(ObjectType.COURSE, id, MediaRole.GALLERY) + validImages.size();
        if (totalImages > 10) throw new AppException(ErrorCode.COURSE_IMAGE_LIMIT_EXCEEDED);
        imageService.addFiles(ObjectType.COURSE, id, validImages, MediaType.IMAGE, MediaRole.GALLERY, "courses/images");
        List<ImageResponse> storedImages = imageService.findActive(ObjectType.COURSE, id);
        CourseResponse response = courseMapper.toResponse(course);
        response.setImages(storedImages);
        response.setThumbnailUrl(imageService.findFirstUrl(storedImages, MediaRole.THUMBNAIL));
        return response;
    }

    @Override
    @Transactional
    public void softDelete(Long id) {
        Course course = courseRepository
                .findByIdAndStatus(id, 1)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        if (enrollmentRepository.existsByCourseIdAndStatus(id, 1))
            throw new AppException(ErrorCode.COURSE_HAS_STUDENTS);
        course.setStatus(0);
        List<Long> lessonIds = lessonRepository.findIdsByCourseId(id);
        lessonRepository.softDeleteByCourseId(id);
        imageService.softDeleteAll(ObjectType.LESSON, lessonIds);
        imageService.softDeleteAll(ObjectType.COURSE, id);
    }

    @Override
    public byte[] export(String keyword, String name, String code, Integer minDuration, Integer maxDuration) {
        if (minDuration != null && maxDuration != null && minDuration > maxDuration)
            throw new AppException(ErrorCode.FILTER_RANGE_INVALID);
        Page<Course> result = courseRepository.search(
                keyword,
                name,
                code,
                minDuration,
                maxDuration,
                Pageable.unpaged(Sort.by(Sort.Direction.DESC, "id")));
        return excelService.courses(result.getContent());
    }
}
