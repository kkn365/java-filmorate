package ru.yandex.practicum.filmorate.service.film.FilmService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    @Autowired
    private FilmStorage filmStorage;

    @Autowired
    private UserStorage userStorage;

    public Collection<Film> getFilms() {
        return filmStorage.getAll();
    }

    public Film addNewFilm(Film film) {
        return filmStorage.put(film);
    }

    public Film updateFilmData(Film film) {
        final Long id = film.getId();

        if (id == null) {
            log.error("Не указан id в теле запроса: {}", film);
            throw new ValidationException("Должен быть указан id фильма.");
        }

        if (filmStorage.get(id) == null) {
            log.error("Не найден фильм с id={}", id);
            throw new NotFoundException(String.format("Фильм с id=%d не найден.", id));
        }

        return filmStorage.update(film);
    }

    public Film putLike(Long id, Long userId) {
        Film film = filmStorage.get(id);
        if (film == null) {
            log.error("Не найден фильм с id={}", id);
            throw new NotFoundException(String.format("Фильм с id=%d не найден.", id));
        }

        User user = userStorage.get(userId);
        if (user == null) {
            log.error("Не найден пользователь с id={}", userId);
            throw new NotFoundException(String.format("Пользователь с id=%d не найден.", userId));
        }

        film.addLike(userId);
        return filmStorage.update(film);
    }

    public Film deleteLike(Long id, Long userId) {
        Film film = filmStorage.get(id);
        if (film == null) {
            log.error("Не найден фильм с id={}", id);
            throw new NotFoundException(String.format("Фильм с id=%d не найден.", userId));
        }

        User user = userStorage.get(userId);
        if (user == null) {
            log.error("Не найден пользователь с id={}", userId);
            throw new NotFoundException(String.format("Пользователь с id=%d не найден.", userId));
        }

        film.removeLike(userId);
        return filmStorage.update(film);
    }

    public Collection<Film> getPopular(Integer count) {
        Comparator<Film> filmPopularityComparator = (f1, f2) -> f2.getRate() - f1.getRate();
        return filmStorage.getAll().stream()
                .sorted(filmPopularityComparator)
                .limit(count)
                .collect(Collectors.toList());
    }

}
