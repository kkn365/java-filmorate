package ru.yandex.practicum.filmorate.dto.annotation;

import jakarta.validation.Constraint;
import ru.yandex.practicum.filmorate.dto.validator.MinimumDateValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MinimumDateValidator.class)
public @interface MinimumFilmReleaseDate {

    String MIN_RELEASE_DATE = "1895-12-28";

    String message() default "The release date of the film cannot be earlier {value}";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};

    String value() default MIN_RELEASE_DATE;

}