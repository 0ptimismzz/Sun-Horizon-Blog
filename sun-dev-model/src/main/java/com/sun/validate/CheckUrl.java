package com.sun.validate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CheckUrlValidate.class)
public @interface CheckUrl {

    String message() default "Url不正确";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}