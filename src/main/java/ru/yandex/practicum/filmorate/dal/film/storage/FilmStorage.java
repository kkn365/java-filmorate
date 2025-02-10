package ru.yandex.practicum.filmorate.dal.film.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.Collection;

public interface FilmStorage {
    Film addNewFilm(Film film);

    Film updateCurrentFilm(Film film);

    Film getFilmById(Long id);

    Collection<Film> getAllFilms();

    Film putLike(Long filmId, Long userId);

    Like getLike(Long filmId, Long userId);

    Film deleteLike(Long filmId, Long userId);

    Collection<Film> getPopularFilms(Integer limit, Integer genreId, Integer year);

    Collection<Like> getDataField(Long userId);

    Collection<Film> getCommonFilmsWithFriend(Long userId, Long friendId);

    void deleteFilmById(Long filmId);
}
