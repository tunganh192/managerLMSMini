package com.honda.managerlmsmini.mapper;

import com.honda.managerlmsmini.dto.lesson.LessonCreateRequest;
import com.honda.managerlmsmini.dto.lesson.LessonResponse;
import com.honda.managerlmsmini.dto.lesson.LessonUpdateRequest;
import com.honda.managerlmsmini.entity.Lesson;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LessonMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "course", ignore = true)
    Lesson toEntity(LessonCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "course", ignore = true)
    void updateEntity(LessonUpdateRequest request, @MappingTarget Lesson lesson);

    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseName", source = "course.name")
    @Mapping(target = "thumbnailUrl", ignore = true)
    @Mapping(target = "videoUrl", ignore = true)
    @Mapping(target = "images", ignore = true)
    LessonResponse toResponse(Lesson lesson);
}
