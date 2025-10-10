package com.bytogether.userservice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {
    private static final String PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,}$";

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context){
        if(password == null || password.isEmpty()) return false;
        return password.matches(PASSWORD_REGEX);
    }
}
