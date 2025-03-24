package com.example.tileshop.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TextLengthValidator implements ConstraintValidator<ValidTextLength, String> {

    private int min;
    private int max;

    @Override
    public void initialize(ValidTextLength constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        return value.length() >= min && value.length() <= max;
    }
}
