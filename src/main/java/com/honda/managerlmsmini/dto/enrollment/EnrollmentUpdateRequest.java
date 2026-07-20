package com.honda.managerlmsmini.dto.enrollment;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EnrollmentUpdateRequest {
    @NotNull(message = "{validation.enrollment.student.required}")
    @Positive(message = "{validation.id.positive}")
    Long studentId;

    @NotNull(message = "{validation.enrollment.course.required}")
    @Positive(message = "{validation.id.positive}")
    Long courseId;
}
