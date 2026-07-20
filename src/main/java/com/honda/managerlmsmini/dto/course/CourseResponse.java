package com.honda.managerlmsmini.dto.course;

import com.honda.managerlmsmini.dto.common.ImageResponse;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseResponse {
    Long id;
    String name;
    String code;
    String description;
    Integer duration;
    String thumbnailUrl;
    Integer status;
    List<ImageResponse> images;
}
