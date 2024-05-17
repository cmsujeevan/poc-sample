package com.cmsujeevan.cdp.exception;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.*;

@Slf4j
@ControllerAdvice
public class NotEmptyJsonNodeValidator implements ConstraintValidator<NotEmptyJsonNodeValidator.NotEmptyJsonNode, JsonNode> {

    @Override
    public void initialize(NotEmptyJsonNode constraintAnnotation) {
    }

    @Override
    public boolean isValid(JsonNode jsonNode, ConstraintValidatorContext context) {
        return jsonNode != null && !jsonNode.isNull() && !jsonNode.isEmpty();
    }


    @Documented
    @Constraint(validatedBy = NotEmptyJsonNodeValidator.class)
    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface NotEmptyJsonNode {
        String message() default "JsonNode must not be empty";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }
}
