package com.honda.managerlmsmini.dto.enrollment;

import java.time.LocalDate;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EnrollmentResponse {
    Long id;
    Long studentId;
    String studentName;
    String phone;
    String avatarUrl;
    Long courseId;
    String courseName;
    LocalDate enrolledDate;
}
