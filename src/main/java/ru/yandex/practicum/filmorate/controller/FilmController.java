package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
@Slf4j
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public Collection<Film> getAll() {
        log.info("Получен запрос 'GET /films'");

        Collection<Film> allFilms = filmService.getFilms();
        log.info("Отправлен ответ на запрос 'GET /films' с телом: {}", allFilms);
        return allFilms;
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film newFilm) {
        log.info("Получен запрос 'POST /films' с телом: {}", newFilm);
        Film film = filmService.addNewFilm(newFilm);
        log.info("Отправлен ответ на запрос 'POST /films' с телом: {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос 'PUT /films' с телом: {}", film);
        Film updatedFilm = filmService.updateFilmData(film);
        log.info("Отправлен ответ на запрос 'PUT /films' с телом: {}", updatedFilm);
        return updatedFilm;
    }

    @PutMapping("/{id}/like/{userId}")
    public Film putLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Получен запрос 'PUT /films/{}/like/{}'", id, userId);
        Film film = filmService.putLike(id, userId);
        log.info("Отправлен ответ на запрос 'PUT /films/{}/like/{}' с телом: {}", id, userId, film);
        return film;
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Получен запрос 'DELETE /films/{}/like/{}'", id, userId);
        Film film = filmService.deleteLike(id, userId);
        log.info("Отправлен ответ на запрос 'DELETE /films/{}/like/{}' с телом: {}", id, userId, film);
        return film;
    }

    @GetMapping("/popular")
    public Collection<Film> getPopular(
            @RequestParam(defaultValue = "${filmorate.default.popular-list-count}") Integer count
    ) {
        log.info("Получен запрос 'GET /films/popular?count={}'", count);
        Collection<Film> popularFilmsList = filmService.getPopular(count);
        log.info("Отправлен ответ на запрос 'GET /films/popular?count={}' с телом: {}", count, popularFilmsList);
        return popularFilmsList;
    }
}
