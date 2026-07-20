package com.honda.managerlmsmini.dto.student;

import com.honda.managerlmsmini.validation.NotEmptyFile;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentCreateRequest {
    @NotBlank(message = "{validation.student.name.required}")
    @Size(max = 150, message = "{validation.student.name.size}")
    String name;

    @NotBlank(message = "{validation.student.email.required}")
    @Email(message = "{validation.student.email.invalid}")
    @Size(max = 150, message = "{validation.student.email.size}")
    String email;

    @Pattern(regexp = "^$|^[0-9+ .-]{8,20}$", message = "{validation.student.phone.invalid}")
    String phone;

    @NotEmptyFile(message = "{validation.student.avatar.required}")
    MultipartFile avatar;
}
