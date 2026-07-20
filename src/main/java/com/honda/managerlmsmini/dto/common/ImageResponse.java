package com.honda.managerlmsmini.dto.common;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImageResponse {
    Long id;
    String objectType;
    Long objectId;
    String mediaType;
    String mediaRole;
    String url;
    String originalName;
    String contentType;
}
