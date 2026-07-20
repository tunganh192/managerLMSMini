package com.honda.managerlmsmini.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(
        name = "enrollments",
        uniqueConstraints =
                @UniqueConstraint(
                        name = "uk_enrollment",
                        columnNames = {"student_id", "course_id"}))
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Enrollment extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    Course course;

    @Column(nullable = false)
    LocalDate enrolledDate;
}
