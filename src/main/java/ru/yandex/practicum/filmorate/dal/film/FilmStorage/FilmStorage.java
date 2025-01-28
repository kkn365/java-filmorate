package ru.yandex.practicum.filmorate.dal.film.FilmStorage;

import ru.yandex.practicum.filmorate.dto.LikeDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Film addNewFilm(Film film);

    Film updateCurrentFilm(Film film);

    Film getFilmById(Long id);

    Collection<Film> getAllFilms();

    Film putLike(Long filmId, Long userId);

    LikeDto getLike(Long filmId, Long userId);

    Film deleteLike(Long filmId, Long userId);

    public Collection<Film> getPopularFilms(Integer limit, Integer genreId, Integer year);

}
