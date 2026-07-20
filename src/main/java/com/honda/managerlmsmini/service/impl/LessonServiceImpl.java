package com.honda.managerlmsmini.service.impl;

import com.honda.managerlmsmini.dto.common.ImageResponse;
import com.honda.managerlmsmini.dto.PageResponse;
import com.honda.managerlmsmini.dto.lesson.*;
import com.honda.managerlmsmini.entity.*;
import com.honda.managerlmsmini.enums.*;
import com.honda.managerlmsmini.exception.*;
import com.honda.managerlmsmini.mapper.LessonMapper;
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
public class LessonServiceImpl implements LessonService {
    LessonRepository lessonRepository;
    CourseRepository courseRepository;
    LessonMapper lessonMapper;
    ImageService imageService;

    @Override
    @Transactional
    public LessonResponse create(LessonCreateRequest request) {
        if (request.getThumbnail() == null || request.getThumbnail().isEmpty())
            throw new AppException(ErrorCode.LESSON_THUMBNAIL_REQUIRED);
        if (request.getVideo() == null || request.getVideo().isEmpty())
            throw new AppException(ErrorCode.LESSON_VIDEO_REQUIRED);
        request.setTitle(request.getTitle().trim());
        Lesson lesson = lessonMapper.toEntity(request);
        Course course = courseRepository
                .findByIdAndStatus(request.getCourseId(), 1)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        lesson.setCourse(course);
        Lesson savedLesson = lessonRepository.saveAndFlush(lesson);
        if (request.getThumbnail() != null && !request.getThumbnail().isEmpty())
            imageService.addFiles(
                    ObjectType.LESSON,
                    savedLesson.getId(),
                    List.of(request.getThumbnail()),
                    MediaType.IMAGE,
                    MediaRole.THUMBNAIL,
                    "lessons/thumbnails");
        if (request.getVideo() != null && !request.getVideo().isEmpty())
            imageService.addFiles(
                    ObjectType.LESSON,
                    savedLesson.getId(),
                    List.of(request.getVideo()),
                    MediaType.VIDEO,
                    MediaRole.LESSON_VIDEO,
                    "lessons/videos");
        imageService.addFiles(
                ObjectType.LESSON,
                savedLesson.getId(),
                request.getImages(),
                MediaType.IMAGE,
                MediaRole.LESSON_IMAGE,
                "lessons/images");
        imageService.addFiles(
                ObjectType.LESSON,
                savedLesson.getId(),
                request.getVideos(),
                MediaType.VIDEO,
                MediaRole.LESSON_EXTRA_VIDEO,
                "lessons/videos");
        List<ImageResponse> images = imageService.findActive(ObjectType.LESSON, savedLesson.getId());
        LessonResponse response = lessonMapper.toResponse(savedLesson);
        response.setImages(images);
        response.setThumbnailUrl(imageService.findFirstUrl(images, MediaRole.THUMBNAIL));
        response.setVideoUrl(imageService.findFirstUrl(images, MediaRole.LESSON_VIDEO));
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public LessonResponse getById(Long id) {
        Lesson lesson = lessonRepository
                .findByIdAndStatus(id, 1)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));
        List<ImageResponse> images = imageService.findActive(ObjectType.LESSON, id);
        LessonResponse response = lessonMapper.toResponse(lesson);
        response.setImages(images);
        response.setThumbnailUrl(imageService.findFirstUrl(images, MediaRole.THUMBNAIL));
        response.setVideoUrl(imageService.findFirstUrl(images, MediaRole.LESSON_VIDEO));
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<LessonResponse> findByCourse(Long courseId, int page, int size) {
        courseRepository.findByIdAndStatus(courseId, 1).orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        Page<Lesson> lessonPage = lessonRepository.findByCourseIdAndStatus(
                courseId, 1, PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id")));
        List<Lesson> lessons = lessonPage.getContent();
        Map<Long, List<ImageResponse>> images = lessons.isEmpty()
                ? Map.of()
                : imageService.findActive(ObjectType.LESSON, lessons.stream().map(Lesson::getId).toList());
        List<LessonResponse> responses = lessons.stream()
                .map(lesson -> {
                    List<ImageResponse> lessonImages = images.getOrDefault(lesson.getId(), List.of());
                    LessonResponse response = lessonMapper.toResponse(lesson);
                    response.setImages(lessonImages);
                    response.setThumbnailUrl(imageService.findFirstUrl(lessonImages, MediaRole.THUMBNAIL));
                    response.setVideoUrl(imageService.findFirstUrl(lessonImages, MediaRole.LESSON_VIDEO));
                    return response;
                })
                .toList();
        return new PageResponse<>(responses, page, size, lessonPage.getTotalElements(), lessonPage.getTotalPages());
    }

    @Override
    @Transactional
    public LessonResponse update(Long id, LessonUpdateRequest request) {
        request.setTitle(request.getTitle().trim());
        Lesson lesson = lessonRepository
                .findByIdAndStatus(id, 1)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));
        Course course = courseRepository
                .findByIdAndStatus(request.getCourseId(), 1)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        lesson.setCourse(course);
        lessonMapper.updateEntity(request, lesson);
        imageService.updateStatuses(ObjectType.LESSON, id, request.getDeletedImageIds(), request.getRestoredImageIds());
        boolean hasThumbnail =
                request.getThumbnail() != null && !request.getThumbnail().isEmpty();
        boolean hasVideo = request.getVideo() != null && !request.getVideo().isEmpty();
        if (!hasThumbnail && request.getThumbnailImageId() != null)
            imageService.promoteMedia(
                    ObjectType.LESSON,
                    id,
                    request.getThumbnailImageId(),
                    MediaType.IMAGE,
                    MediaRole.THUMBNAIL,
                    MediaRole.LESSON_IMAGE);
        if (!hasVideo && request.getMainVideoId() != null)
            imageService.promoteMedia(
                    ObjectType.LESSON,
                    id,
                    request.getMainVideoId(),
                    MediaType.VIDEO,
                    MediaRole.LESSON_VIDEO,
                    MediaRole.LESSON_EXTRA_VIDEO);
        if (hasThumbnail) imageService.softDeleteAll(ObjectType.LESSON, id, MediaRole.THUMBNAIL);
        if (hasVideo) imageService.softDeleteAll(ObjectType.LESSON, id, MediaRole.LESSON_VIDEO);
        if (request.getThumbnail() != null && !request.getThumbnail().isEmpty())
            imageService.addFiles(
                    ObjectType.LESSON,
                    id,
                    List.of(request.getThumbnail()),
                    MediaType.IMAGE,
                    MediaRole.THUMBNAIL,
                    "lessons/thumbnails");
        if (request.getVideo() != null && !request.getVideo().isEmpty())
            imageService.addFiles(
                    ObjectType.LESSON,
                    id,
                    List.of(request.getVideo()),
                    MediaType.VIDEO,
                    MediaRole.LESSON_VIDEO,
                    "lessons/videos");
        imageService.addFiles(
                ObjectType.LESSON, id, request.getImages(), MediaType.IMAGE, MediaRole.LESSON_IMAGE, "lessons/images");
        imageService.addFiles(
                ObjectType.LESSON,
                id,
                request.getVideos(),
                MediaType.VIDEO,
                MediaRole.LESSON_EXTRA_VIDEO,
                "lessons/videos");
        Lesson savedLesson = lessonRepository.saveAndFlush(lesson);
        List<ImageResponse> images = imageService.findActive(ObjectType.LESSON, id);
        LessonResponse response = lessonMapper.toResponse(savedLesson);
        response.setImages(images);
        response.setThumbnailUrl(imageService.findFirstUrl(images, MediaRole.THUMBNAIL));
        response.setVideoUrl(imageService.findFirstUrl(images, MediaRole.LESSON_VIDEO));
        return response;
    }

    @Override
    @Transactional
    public void softDelete(Long id) {
        Lesson lesson = lessonRepository
                .findByIdAndStatus(id, 1)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));
        lesson.setStatus(0);
        imageService.softDeleteAll(ObjectType.LESSON, id);
    }
}
