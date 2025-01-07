package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.genre.GenreStorage.GenreStorage;
import ru.yandex.practicum.filmorate.dto.FilmGenresDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenreService {

    @Autowired
    private GenreStorage genreStorage;

    public Collection<Genre> getAllGenres() {
        return genreStorage.getAllGenres();
    }

    public Genre getGenreById(int id) {
        final Genre genre = genreStorage.getGenreById(id);

        if (genre == null) {
            log.error("Не найден жанр с id={}.", id);
            throw new NotFoundException(String.format("Жанр с id=%d не найден.", id));
        }

        return genre;
    }

    public Collection<FilmGenresDto> getAllFilmsGenres() {
        return genreStorage.getAllFilmsGenres();
    }

    public Collection<Genre> getFilmGenresByFilmId(Long filmId) {
        return genreStorage.getFilmGenres(filmId);
    }
}
