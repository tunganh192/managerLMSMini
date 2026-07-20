package com.honda.managerlmsmini.dto.lesson;

import com.honda.managerlmsmini.validation.NotEmptyFile;
import jakarta.validation.constraints.*;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonCreateRequest {
    @NotNull(message = "{validation.lesson.course.required}")
    @Positive(message = "{validation.id.positive}")
    Long courseId;

    @NotBlank(message = "{validation.lesson.title.required}")
    @Size(max = 200, message = "{validation.lesson.title.size}")
    String title;

    @Size(max = 2000, message = "{validation.description.size}")
    String description;

    @NotEmptyFile(message = "{validation.lesson.thumbnail.required}")
    MultipartFile thumbnail;

    @NotEmptyFile(message = "{validation.lesson.video.required}")
    MultipartFile video;

    @Size(max = 10, message = "{validation.image.size}")
    List<MultipartFile> images;

    @Size(max = 5, message = "{validation.video.size}")
    List<MultipartFile> videos;
}
