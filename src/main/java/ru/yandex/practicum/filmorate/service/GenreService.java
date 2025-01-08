package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.genre.GenreStorage.GenreStorage;
import ru.yandex.practicum.filmorate.dto.FilmGenresDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenreService {

    @Autowired
    private GenreStorage genreStorage;

    public Collection<GenreDto> getAllGenres() {
        return genreStorage.getAllGenres()
                .stream()
                .map(GenreMapper::mapToGenreDto)
                .collect(Collectors.toList());
    }

    public GenreDto getGenreById(int id) {
        final Genre genre = genreStorage.getGenreById(id);

        if (genre == null) {
            log.warn("Не найден жанр с id={}.", id);
            throw new NotFoundException(String.format("Жанр с id=%d не найден.", id));
        }

        return GenreMapper.mapToGenreDto(genre);
    }

    public Collection<FilmGenresDto> getAllFilmsGenres() {
        return genreStorage.getAllFilmsGenres();
    }

    public Collection<GenreDto> getFilmGenresByFilmId(Long filmId) {
        return genreStorage.getFilmGenres(filmId)
                .stream()
                .map(GenreMapper::mapToGenreDto)
                .collect(Collectors.toList());
    }
}
