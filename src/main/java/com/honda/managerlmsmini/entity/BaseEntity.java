package com.honda.managerlmsmini.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    Integer status = 1;

    LocalDateTime createdDate;
    LocalDateTime modifiedDate;

    @PrePersist
    void createAudit() {
        createdDate = modifiedDate = LocalDateTime.now();
        if (status == null) status = 1;
    }

    @PreUpdate
    void updateAudit() {
        modifiedDate = LocalDateTime.now();
    }
}
