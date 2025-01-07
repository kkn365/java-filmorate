package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.film.FilmStorage.FilmStorage;
import ru.yandex.practicum.filmorate.dto.FilmGenresDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class FilmService {

    @Autowired
    private FilmStorage filmStorage;
    @Autowired
    private UserService userService;
    @Autowired
    private MpaService mpaService;
    @Autowired
    private GenreService genreService;

    public Collection<Film> getFilms() {
        Collection<Film> films = filmStorage.getAllFilms();
        Collection<MPA> mpas = mpaService.getAllMPAs();
        Collection<FilmGenresDto> allFilmsGenres = genreService.getAllFilmsGenres();

        for (Film film : films) {

            final String mpaName = mpas.stream()
                    .filter(mpa -> mpa.getId().equals(film.getMpa().getId()))
                    .findFirst()
                    .map(MPA::getName)
                    .get();
            film.getMpa().setName(mpaName);

            final Set<Genre> genres = allFilmsGenres
                    .stream()
                    .filter(genre -> genre.getFilmId().equals(film.getId()))
                    .map(genre -> Genre.builder().id(genre.getGenreId()).name(genre.getGenreName()).build())
                    .collect(Collectors.toSet());
            film.setGenres(genres);
        }

        return films;
    }

    public Film addNewFilm(Film film) {

        final Integer mpaId = film.getMpa().getId();
        if (mpaId != null) {
            mpaService.getMPAbyIdForNewFilm(mpaId);
        }

        if (film.getGenres() != null) {
            final Set<Genre> incomingFilmGenres = film.getGenres();

            final List<Integer> currentFilmGenresIds = genreService.getAllGenres()
                    .stream()
                    .map(Genre::getId)
                    .toList();

            for (Integer genreId : incomingFilmGenres.stream().map(Genre::getId).toList()) {
                if (!currentFilmGenresIds.contains(genreId)) {
                    log.error("Не найден жанр с id={}", genreId);
                    throw new ValidationException(String.format("Жанр с id=%d не найден.", genreId));
                }
            }

        }

        return filmStorage.addNewFilm(film);
    }

    public Film updateFilmData(Film film) {
        final Long id = film.getId();

        if (id == null) {
            log.error("Не указан id в теле запроса: {}", film);
            throw new ValidationException("Должен быть указан id фильма.");
        }

        if (filmStorage.getFilmById(id) == null) {
            log.error("Не найден фильм с id={}", id);
            throw new NotFoundException(String.format("Фильм с id=%d не найден.", id));
        }

        return filmStorage.updateCurrentFilm(film);
    }

    public Film putLike(Long filmId, Long userId) {

        filmStorage.getFilmById(filmId);
        userService.getUserById(userId);

        if (filmStorage.getLike(filmId, userId) != null) {
            log.warn("Установка повторного лайка пользователем с id={} на фильм с id={}", userId, filmId);
        }

        return filmStorage.putLike(filmId, userId);

    }

    public Film deleteLike(Long filmId, Long userId) {
        filmStorage.getFilmById(filmId);
        userService.getUserById(userId);

        if (filmStorage.getLike(filmId, userId) != null) {
            return filmStorage.deleteLike(filmId, userId);
        }

        log.warn("Попытка удаления несуществующего лайка пользователем с id={} на фильм с id{}", userId, filmId);
        return null;
    }

    public Collection<Film> getPopular(Integer count) {
        return filmStorage.getPopularFilms(count);
    }

    public Film getFilmById(Long id) {
        Film film = filmStorage.getFilmById(id);
        Set<Genre> filmGenres = new HashSet<>(genreService.getFilmGenresByFilmId(id));
        film.setGenres(filmGenres);
        film.setMpa(mpaService.getMPAbyId(film.getMpa().getId()));
        return film;
    }
}
