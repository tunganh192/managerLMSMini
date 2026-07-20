package com.honda.managerlmsmini.entity;

import jakarta.persistence.*;
import java.util.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Course extends BaseEntity {
    @Column(nullable = false, length = 200)
    String name;

    @Column(nullable = false, length = 50)
    String code;

    @Column(length = 2000)
    String description;

    @Column(nullable = false)
    Integer duration;

    @OneToMany(
            mappedBy = "course",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    List<Lesson> lessons = new ArrayList<>();

    @OneToMany(
            mappedBy = "course",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    List<Enrollment> enrollments = new ArrayList<>();
}
