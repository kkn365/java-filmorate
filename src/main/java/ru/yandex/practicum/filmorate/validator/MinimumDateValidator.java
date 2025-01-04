package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.annotation.*;

import java.time.LocalDate;

import static ru.yandex.practicum.filmorate.utility.Constants.DATE_TIME_FORMATTER;

public class MinimumDateValidator implements ConstraintValidator<MinimumFilmReleaseDate, LocalDate> {
    private LocalDate minimumDate;

    @Override
    public void initialize(MinimumFilmReleaseDate constraintAnnotation) {
        minimumDate = LocalDate.parse(constraintAnnotation.value().toString().formatted(DATE_TIME_FORMATTER));
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        return localDate == null || !localDate.isBefore(minimumDate);
    }

}