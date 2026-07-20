package com.honda.managerlmsmini.entity;

import jakarta.persistence.*;
import java.util.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "students", uniqueConstraints = @UniqueConstraint(name = "uk_student_email", columnNames = "email"))
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Student extends BaseEntity {
    @Column(nullable = false, length = 150)
    String name;

    @Column(nullable = false, length = 150)
    String email;

    @Column(length = 20)
    String phone;

    @OneToMany(
            mappedBy = "student",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    List<Enrollment> enrollments = new ArrayList<>();
}
