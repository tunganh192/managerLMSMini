package com.honda.managerlmsmini.mapper;

import com.honda.managerlmsmini.dto.course.CourseCreateRequest;
import com.honda.managerlmsmini.dto.course.CourseResponse;
import com.honda.managerlmsmini.dto.course.CourseUpdateRequest;
import com.honda.managerlmsmini.entity.Course;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CourseMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "lessons", ignore = true)
    @Mapping(target = "enrollments", ignore = true)
    Course toEntity(CourseCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "lessons", ignore = true)
    @Mapping(target = "enrollments", ignore = true)
    void updateEntity(CourseUpdateRequest request, @MappingTarget Course course);

    @Mapping(target = "images", ignore = true)
    CourseResponse toResponse(Course course);
}
