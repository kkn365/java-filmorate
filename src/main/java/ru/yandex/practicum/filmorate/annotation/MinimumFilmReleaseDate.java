package ru.yandex.practicum.filmorate.annotation;

import ru.yandex.practicum.filmorate.validator.MinimumDateValidator;

import jakarta.validation.Constraint;
import jakarta.validation.constraints.Past;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static ru.yandex.practicum.filmorate.utility.Constants.MIN_RELEASE_DATE;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MinimumDateValidator.class)
@Past
public @interface MinimumFilmReleaseDate {

    String message() default "The release date of the film cannot be earlier {value}";
    Class<?>[] groups() default {};
    Class<?>[] payload() default {};
    String value() default MIN_RELEASE_DATE;

}