package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
@Slf4j
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public Collection<FilmDto> getAll() {
        log.info("Получен запрос 'GET /films'");

        Collection<FilmDto> allFilms = filmService.getFilms().stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());

        log.info("Отправлен ответ на запрос 'GET /films' с телом: {}", allFilms);
        return allFilms;
    }

    @PostMapping
    public FilmDto createFilm(@Valid @RequestBody FilmDto newFilm) {
        log.info("Получен запрос 'POST /films' с телом: {}", newFilm);
        FilmDto film = FilmMapper.mapToFilmDto(filmService.addNewFilm(FilmMapper.mapToFilm(newFilm)));
        log.info("Отправлен ответ на запрос 'POST /films' с телом: {}", film);
        return film;
    }

    @PutMapping
    public FilmDto updateFilm(@Valid @RequestBody FilmDto film) {
        log.info("Получен запрос 'PUT /films' с телом: {}", film);
        FilmDto updatedFilm = FilmMapper.mapToFilmDto(filmService.updateFilmData(FilmMapper.mapToFilm(film)));
        log.info("Отправлен ответ на запрос 'PUT /films' с телом: {}", updatedFilm);
        return updatedFilm;
    }

    @PutMapping("/{id}/like/{userId}")
    public FilmDto putLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Получен запрос 'PUT /films/{}/like/{}'", id, userId);
        FilmDto film = FilmMapper.mapToFilmDto(filmService.putLike(id, userId));
        log.info("Отправлен ответ на запрос 'PUT /films/{}/like/{}' с телом: {}", id, userId, film);
        return film;
    }

    @DeleteMapping("/{id}/like/{userId}")
    public FilmDto deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Получен запрос 'DELETE /films/{}/like/{}'", id, userId);
        FilmDto film = FilmMapper.mapToFilmDto(filmService.deleteLike(id, userId));
        log.info("Отправлен ответ на запрос 'DELETE /films/{}/like/{}' с телом: {}", id, userId, film);
        return film;
    }

    @GetMapping("/popular")
    public Collection<FilmDto> getPopular(
            @RequestParam(defaultValue = "${filmorate.default.popular-list-count}") Integer count
    ) {
        log.info("Получен запрос 'GET /films/popular?count={}'", count);

        Collection<FilmDto> popularFilmsList = filmService.getPopular(count).stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());

        log.info("Отправлен ответ на запрос 'GET /films/popular?count={}' с телом: {}", count, popularFilmsList);
        return popularFilmsList;
    }

    @GetMapping("/{id}")
    public FilmDto getFilm(@PathVariable Long id) {
        log.info("Получен запрос 'GET /films/{}'", id);
        FilmDto film = FilmMapper.mapToFilmDto(filmService.getFilmById(id));
        log.info("Отправлен ответ на запрос 'GET /films/{}' с телом: {}", id, film);
        return film;
    }

}
