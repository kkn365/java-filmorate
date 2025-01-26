package ru.yandex.practicum.filmorate.dto.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.dto.annotation.MinimumFilmReleaseDate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MinimumDateValidator implements ConstraintValidator<MinimumFilmReleaseDate, LocalDate> {
    private LocalDate minimumDate;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void initialize(MinimumFilmReleaseDate constraintAnnotation) {
        minimumDate = LocalDate.parse(constraintAnnotation.value().formatted(DATE_TIME_FORMATTER));
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        return localDate == null || !localDate.isBefore(minimumDate);
    }

}