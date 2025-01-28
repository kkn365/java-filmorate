package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public Collection<FilmDto> getAll() {
        return filmService.getFilms();
    }

    @PostMapping
    public FilmDto createFilm(@Valid @RequestBody FilmDto newFilm) {
        return filmService.addNewFilm(newFilm);
    }

    @PutMapping
    public FilmDto updateFilm(@Valid @RequestBody FilmDto film) {
        return filmService.updateFilmData(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public FilmDto putLike(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.putLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public FilmDto deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<FilmDto> getPopular(
            @RequestParam(name = "count", defaultValue = "10") Integer count,
            @RequestParam(name = "genreId", required = false, defaultValue = "0") Integer genreId,
            @RequestParam(name = "year", required = false, defaultValue = "0") Integer year) {

        return filmService.getPopular(count, genreId ,year);
    }

    @GetMapping("/{id}")
    public FilmDto getFilm(@PathVariable Long id) {
        return filmService.getFilmById(id);
    }

}
