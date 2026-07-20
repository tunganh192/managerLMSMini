package com.honda.managerlmsmini.dto.enrollment;

import jakarta.validation.constraints.*;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EnrollmentCreateRequest {
    @NotEmpty(message = "{validation.enrollment.students.required}")
    List<@Positive(message = "{validation.id.positive}") Long> studentIds;

    @NotEmpty(message = "{validation.enrollment.courses.required}")
    List<@Positive(message = "{validation.id.positive}") Long> courseIds;
}
