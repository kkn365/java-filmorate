package ru.yandex.practicum.filmorate.controller;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();
    private long filmIdGenerator = 0;

    @GetMapping
    public Collection<Film> findAll() {

        log.info("Пришел GET-запрос /films");

        log.info("Отправлен ответ на GET-запрос /films");

        return films.values();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film newFilm) {

        log.info("Пришел POST-запрос /films с телом: {}", newFilm);

        Film film = Film.builder()
                    .id(getNextId())
                    .name(newFilm.getName())
                    .description(newFilm.getDescription())
                    .releaseDate(newFilm.getReleaseDate())
                    .duration(newFilm.getDuration())
                    .build();

        films.put(film.getId(), film);

        log.info("Отправлен ответ на POST-запрос /films с телом: {}", film);

        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {

        log.info("Пришел PUT-запрос /films с телом: {}", film);

        final Long id = film.getId();

        if (id == null) {
            log.error("Не указан id фильма.");
            throw new ValidationException("Должен быть указан id фильма.");
        }

        if (!films.containsKey(id)) {
            log.error("Не найден фильм с id={}", id);
            throw new NotFoundException("Фильм с id=" + id + " не найден.");
        }

        films.put(id, film);

        log.info("Отправлен ответ на PUT-запрос /films с телом: {}", film);

        return film;

    }

    private long getNextId() {
        return ++filmIdGenerator;
    }

}
