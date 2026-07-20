package com.honda.managerlmsmini.dto.lesson;

import jakarta.validation.constraints.*;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonUpdateRequest {
    @NotNull(message = "{validation.lesson.course.required}")
    @Positive(message = "{validation.id.positive}")
    Long courseId;

    @NotBlank(message = "{validation.lesson.title.required}")
    @Size(max = 200, message = "{validation.lesson.title.size}")
    String title;

    @Size(max = 2000, message = "{validation.description.size}")
    String description;

    MultipartFile thumbnail;
    MultipartFile video;

    @Size(max = 10, message = "{validation.image.size}")
    List<MultipartFile> images;

    @Size(max = 5, message = "{validation.video.size}")
    List<MultipartFile> videos;

    List<@Positive(message = "{validation.id.positive}") Long> deletedImageIds;
    List<@Positive(message = "{validation.id.positive}") Long> restoredImageIds;

    @Positive(message = "{validation.id.positive}")
    Long thumbnailImageId;

    @Positive(message = "{validation.id.positive}")
    Long mainVideoId;
}
