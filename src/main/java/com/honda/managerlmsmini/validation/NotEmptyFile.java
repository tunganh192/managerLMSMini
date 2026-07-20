package com.honda.managerlmsmini.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NotEmptyFileValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotEmptyFile {
    String message() default "{validation.file.required}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
