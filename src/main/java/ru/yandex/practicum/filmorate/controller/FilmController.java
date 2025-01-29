package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.ResponseMessage;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public Collection<FilmDto> getAll() {
        return filmService.getAllFilms();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDto createFilm(@Valid @RequestBody FilmDto newFilm) {
        return filmService.addNewFilm(newFilm);
    }

    @PutMapping
    public FilmDto updateFilm(@Valid @RequestBody FilmDto film) {
        return filmService.updateFilmData(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseMessage putLike(@PathVariable Long id, @PathVariable Long userId) {
        return new ResponseMessage(filmService.putLike(id, userId));
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseMessage deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        return new ResponseMessage(filmService.deleteLike(id, userId));
    }

    @GetMapping("/popular")
    public Collection<FilmDto> getPopular(
            @RequestParam(name = "count", defaultValue = "10") Integer count,
            @RequestParam(name = "genreId", required = false, defaultValue = "0") Integer genreId,
            @RequestParam(name = "year", required = false, defaultValue = "0") Integer year) {

        return filmService.getPopular(count, genreId, year);
    }

    @GetMapping("/{id}")
    public FilmDto getFilm(@PathVariable Long id) {
        return filmService.getFilmById(id);
    }

    @GetMapping("/common")
    public Collection<FilmDto> getCommonFilmsWithFriend(@RequestParam Long userId, @RequestParam Long friendId) {

        return filmService.getCommonFilmsWithFriend(userId, friendId);
    }

    @DeleteMapping("/{filmId}")
    public ResponseMessage deleteFilmById(@PathVariable Long filmId) {
        return new ResponseMessage(filmService.deleteFilmById(filmId));
    }

    @GetMapping("/search")
    public Collection<FilmDto> searchFilms(
            @RequestParam String query,
            @RequestParam List<String> by
    ) {
        return filmService.searchFilms(query, by);
    }

    @GetMapping("/director/{directorId}")
    public Collection<FilmDto> getFilmsByDirectors(
            @PathVariable Long directorId,
            @RequestParam String sortBy
    ) {
        return filmService.getFilmsByDirectors(directorId, sortBy);
    }
}
