package com.honda.managerlmsmini.dto.lesson;

import com.honda.managerlmsmini.dto.common.ImageResponse;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonResponse {
    Long id;
    Long courseId;
    String courseName;
    String title;
    String description;
    String thumbnailUrl;
    String videoUrl;
    Integer status;
    List<ImageResponse> images;
}
