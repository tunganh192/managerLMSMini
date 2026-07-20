package com.honda.managerlmsmini.dto.student;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentResponse {
    Long id;
    String name;
    String email;
    String phone;
    String avatarUrl;
    Integer status;
}
