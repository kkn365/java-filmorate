package ru.yandex.practicum.filmorate.dto.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.constraints.Past;
import ru.yandex.practicum.filmorate.dto.validator.MinimumDateValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.format.DateTimeFormatter;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MinimumDateValidator.class)
@Past
public @interface MinimumFilmReleaseDate {

    public static final String MIN_RELEASE_DATE = "1895-12-28";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    String message() default "The release date of the film cannot be earlier {value}";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};

    String value() default MIN_RELEASE_DATE;

}