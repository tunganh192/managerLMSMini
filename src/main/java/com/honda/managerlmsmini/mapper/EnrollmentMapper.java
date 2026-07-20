package com.honda.managerlmsmini.mapper;

import com.honda.managerlmsmini.dto.enrollment.EnrollmentResponse;
import com.honda.managerlmsmini.entity.Enrollment;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EnrollmentMapper {
    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentName", source = "student.name")
    @Mapping(target = "phone", source = "student.phone")
    @Mapping(target = "avatarUrl", ignore = true)
    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseName", source = "course.name")
    EnrollmentResponse toResponse(Enrollment enrollment);
}
