package com.sun.validate;

import com.sun.utils.UrlUtil;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CheckUrlValidate implements ConstraintValidator<CheckUrl, String> {

    @Override
    public boolean isValid(String url, ConstraintValidatorContext context) {
        return UrlUtil.verifyUrl(url.trim());
    }
}
