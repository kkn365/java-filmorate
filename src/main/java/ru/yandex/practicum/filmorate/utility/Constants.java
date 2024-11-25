package ru.yandex.practicum.filmorate.utility;

import org.springframework.beans.factory.annotation.Value;

import java.time.format.DateTimeFormatter;

public final class Constants {

    public static final String MIN_RELEASE_DATE = "1895-12-28";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Value("${filmorate.default.popular-list-count}")
    public static Integer DEFAULT_FILM_COUNT;

}
