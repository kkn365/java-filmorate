package ru.yandex.practicum.filmorate.dal.genre.GenreStorage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.FilmGenresDto;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;

@Component
public interface GenreStorage {
    Genre getGenreById(int id);

    Collection<Genre> getAllGenres();

    Collection<FilmGenresDto> getAllFilmsGenres();

    Collection<Genre> getFilmGenres(Long filmId);

    void addGenres(Long filmId, List<Integer> genresIds);

    void deleteFilmGenresByFilmId(Long id);
}
