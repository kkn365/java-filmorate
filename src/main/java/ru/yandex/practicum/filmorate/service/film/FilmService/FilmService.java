package ru.yandex.practicum.filmorate.service.film.FilmService;

import lombok.RequiredArgsConstructor;
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
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.utility.Constants.DEFAULT_FILM_COUNT;

@Service
@RequiredArgsConstructor
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
            throw new ValidationException("Должен быть указан id фильма.");
        }

        if (filmStorage.get(id) == null) {
            throw new NotFoundException(String.format("Фильм с id=%d не найден.", id));
        }

        return filmStorage.update(film);
    }

    public Film putLike(Long id, Long userId) {
        Film film = filmStorage.get(id);
        if (film == null) {
            throw new NotFoundException(String.format("Фильм с id=%d не найден.", userId));
        }

        User user = userStorage.get(userId);
        if (user == null) {
            throw new NotFoundException(String.format("Пользователь с id=%d не найден.", userId));
        }

        Set<Long> liked = film.getLiked();
        if (liked == null) {
            liked = new HashSet<>();
        }
        liked.add(userId);
        film.setLiked(liked);
        return filmStorage.update(film);
    }

    public Film deleteLike(Long id, Long userId) {
        Film film = filmStorage.get(id);
        if (film == null) {
            throw new NotFoundException(String.format("Фильм с id=%d не найден.", userId));
        }

        User user = userStorage.get(userId);
        if (user == null) {
            throw new NotFoundException(String.format("Пользователь с id=%d не найден.", userId));
        }

        Set<Long> liked = film.getLiked();
        if (liked != null) {
            liked.remove(userId);
            film.setLiked(liked);
        }
        return filmStorage.update(film);
    }

    public Collection<Film> getPopular(Integer count) {
        if (count == null) {
            count = DEFAULT_FILM_COUNT;
        }
        Comparator<Film> filmPopularityComparator = new Comparator<Film>() {
            @Override
            public int compare(Film f1, Film f2) {
                Integer param1 = f1.getLiked().size();
                Integer param2 = f2.getLiked().size();
                return (param2).compareTo(param1);
            }
        };
        return filmStorage.getAll().stream()
                .filter(film -> film.getLiked() != null)
                .sorted(filmPopularityComparator)
                .limit(count)
                .collect(Collectors.toList());
    }

}
