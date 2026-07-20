package com.honda.managerlmsmini.entity;

import com.honda.managerlmsmini.enums.*;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "images", indexes = @Index(name = "idx_images_object", columnList = "object_type,object_id,status"))
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Image extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @Column(name = "object_type", nullable = false, length = 20)
    ObjectType objectType;

    @Column(name = "object_id", nullable = false)
    Long objectId;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false, length = 20)
    MediaType mediaType;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_role", nullable = false, length = 30)
    MediaRole mediaRole;

    @Column(nullable = false, length = 500)
    String url;

    @Column(length = 255)
    String originalName;

    @Column(length = 100)
    String contentType;
}
