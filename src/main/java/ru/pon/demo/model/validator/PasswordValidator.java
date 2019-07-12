package ru.pon.demo.model.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {
    private Pattern pattern;
    private Matcher matcher;
    private static final String PASSWORD_PATTERN = "(?=^.{8,}$)((?=.*\\d)(?=.*[!@#$%]+))(?=.*[A-Z])(?=.*[a-z]).*$";

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context){
        return (validateEmail(email));
    }

    private boolean validateEmail(String email) {
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
