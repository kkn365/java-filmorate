package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
@Slf4j
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public Collection<GenreDto> getAll() {
        log.info("Получен запрос 'GET /genres'");
        Collection<GenreDto> allGenres = genreService.getAllGenres()
                .stream()
                .map(GenreMapper::mapToGenreDto)
                .collect(Collectors.toList());
        log.info("Отправлен ответ на запрос 'GET /genres' с телом: {}", allGenres);
        return allGenres;
    }

    @GetMapping("/{id}")
    public GenreDto getGenreById(@PathVariable int id) {
        log.info("Получен запрос 'GET /genres/{}'", id);
        GenreDto genre = GenreMapper.mapToGenreDto(genreService.getGenreById(id));
        log.info("Отправлен ответ на запрос 'GET /genres/{}' с телом: {}", id, genre);
        return genre;
    }

}
