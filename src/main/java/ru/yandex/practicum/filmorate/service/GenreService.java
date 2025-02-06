package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.genre.storage.GenreStorage;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.FilmGenresDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreService {

    private final GenreStorage genreStorage;

    public Collection<GenreDto> getAllGenres() {
        return genreStorage.getAllGenres()
                .stream()
                .map(GenreMapper::mapToGenreDto)
                .sorted(Comparator.comparingInt(GenreDto::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
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
                .collect(Collectors.toSet());
    }

    public void addGenresToFilm(FilmDto film) {
        final Long filmId = film.getId();
        final List<Integer> genresIds = film.getGenres().stream()
                .map(GenreDto::getId)
                .toList();
        genreStorage.addGenres(filmId, genresIds);
    }

    public void deleteGenresFromFilm(Long id) {
        genreStorage.deleteFilmGenresByFilmId(id);
    }
}
