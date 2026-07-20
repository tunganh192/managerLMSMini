package com.honda.managerlmsmini.mapper;

import com.honda.managerlmsmini.dto.student.StudentCreateRequest;
import com.honda.managerlmsmini.dto.student.StudentResponse;
import com.honda.managerlmsmini.dto.student.StudentUpdateRequest;
import com.honda.managerlmsmini.entity.Student;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StudentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "enrollments", ignore = true)
    Student toEntity(StudentCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "enrollments", ignore = true)
    void updateEntity(StudentUpdateRequest request, @MappingTarget Student student);

    StudentResponse toResponse(Student student);
}
