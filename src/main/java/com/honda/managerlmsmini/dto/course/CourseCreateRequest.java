package com.honda.managerlmsmini.dto.course;

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
public class CourseCreateRequest {
    @NotBlank(message = "{validation.course.name.required}")
    @Size(max = 200, message = "{validation.course.name.size}")
    String name;

    @NotBlank(message = "{validation.course.code.required}")
    @Size(max = 50, message = "{validation.course.code.size}")
    String code;

    @Size(max = 2000, message = "{validation.description.size}")
    String description;

    @NotNull(message = "{validation.course.duration.required}")
    @Min(value = 1, message = "{validation.course.duration.min}")
    Integer duration;

    @NotEmptyFile(message = "{validation.course.thumbnail.required}")
    MultipartFile thumbnail;

    @Size(max = 10, message = "{validation.image.size}")
    List<MultipartFile> images;
}
