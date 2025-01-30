package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.film.FilmStorage.FilmStorage;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.mapper.MPAMapper;
import ru.yandex.practicum.filmorate.model.*;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;
    private final MpaService mpaService;
    private final GenreService genreService;
    private final DirectorService directorService;

    public Collection<FilmDto> getFilms() {
        Collection<Film> films = filmStorage.getAllFilms();
        Collection<MpaDto> mpas = mpaService.getAllMPAs();
        Collection<FilmGenresDto> allFilmsGenres = genreService.getAllFilmsGenres();
        Collection<DirectorFilmsDto> allDirectors = directorService.getAllFilmDirectors();


        for (Film film : films) {

            final String mpaName = mpas.stream()
                    .filter(mpa -> mpa.getId().equals(film.getMpa().getId()))
                    .findFirst()
                    .map(MpaDto::getName)
                    .get();
            film.getMpa().setName(mpaName);

            final Set<Genre> genres = allFilmsGenres
                    .stream()
                    .filter(genre -> genre.getFilmId().equals(film.getId()))
                    .map(genre -> Genre.builder()
                            .id(genre.getGenreId())
                            .name(genre.getGenreName())
                            .build())
                    .collect(Collectors.toSet());
            film.setGenres(genres);

            final Set<Director> directors = allDirectors
                    .stream()
                    .filter(director -> director.getDirectorId().equals(film.getId()))
                    .map(director -> Director.builder()
                            .id(director.getDirectorId())
                            .name(director.getDirectorName())
                            .build())
                    .collect(Collectors.toSet());
            film.setDirectors(directors);

        }

        return films
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public FilmDto addNewFilm(FilmDto film) {

        final Integer mpaId = film.getMpa().getId();
        if (mpaId != null) {
            mpaService.getMPAbyId(mpaId);
        }

        if (film.getGenres() != null) {

            final Set<Genre> incomingFilmGenres = film.getGenres();
            final Set<Director> incomingFilmDirector = film.getDirectors();
            final Collection<GenreDto> currentFilmGenres = genreService.getAllGenres();
            final Collection<DirectorDto> currentFilmDirector = directorService.getAllDirectors();

            final Set<Integer> currentFilmGenresIds = currentFilmGenres
                    .stream()
                    .map(GenreDto::getId)
                    .collect(Collectors.toSet());

            final Set<Integer> incomingFilmGenresIds = incomingFilmGenres
                    .stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());

            final Set<Long> currentFilmDirectorsIds = currentFilmDirector
                    .stream()
                    .map(DirectorDto::getId)
                    .collect(Collectors.toSet());

            final Set<Long> incomingFilmDirectorsIds = incomingFilmDirector
                    .stream()
                    .map(Director::getId)
                    .collect(Collectors.toSet());

            for (Integer genreId : incomingFilmGenresIds) {
                if (!currentFilmGenresIds.contains(genreId)) {
                    log.warn("Не найден жанр с id={}", genreId);
                    throw new NotFoundException(String.format("Жанр с id=%d не найден.", genreId));
                }
            }

            for (Long directorId : incomingFilmDirectorsIds) {
                if (!currentFilmDirectorsIds.contains(directorId)) {
                    log.warn("Не найден режиссер с id={}", directorId);
                    throw new NotFoundException(String.format("Режиссер с id=%d не найден.", directorId));
                }
            }

        }

        Film newFilm = filmStorage.addNewFilm(FilmMapper.mapToFilm(film));

        return FilmMapper.mapToFilmDto(newFilm);
    }

    public FilmDto updateFilmData(FilmDto film) {
        final Long id = film.getId();

        if (id == null) {
            log.warn("Не указан id в теле запроса: {}", film);
            throw new ValidationException("Должен быть указан id фильма.");
        }

        if (filmStorage.getFilmById(id) == null) {
            log.warn("Не найден фильм с id={}", id);
            throw new NotFoundException(String.format("Фильм с id=%d не найден.", id));
        }

        Film updatedFilm = filmStorage.updateCurrentFilm(FilmMapper.mapToFilm(film));

        return FilmMapper.mapToFilmDto(updatedFilm);
    }

    public FilmDto putLike(Long filmId, Long userId) {
        filmStorage.getFilmById(filmId);
        userService.getUserById(userId);
        Film likedFilm = filmStorage.putLike(filmId, userId);
        return FilmMapper.mapToFilmDto(likedFilm);
    }

    public FilmDto deleteLike(Long filmId, Long userId) {
        filmStorage.getFilmById(filmId);
        userService.getUserById(userId);
        Film deletedLikeFilm = filmStorage.deleteLike(filmId, userId);
        return FilmMapper.mapToFilmDto(deletedLikeFilm);
    }

    public Collection<FilmDto> getPopular(Integer count, Integer genreId, Integer year) {
        return filmStorage.getPopularFilms(count, genreId, year)
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public FilmDto getFilmById(Long id) {

        Film film = filmStorage.getFilmById(id);

        final Set<Genre> filmGenres = genreService.getFilmGenresByFilmId(id)
                .stream()
                .map(GenreMapper::mapToGenre)
                .collect(Collectors.toSet());

        film.setGenres(filmGenres);

        final int mpaId = film.getMpa().getId();
        final MPA filmMpa = MPAMapper.mapToMPA(mpaService.getMPAbyId(mpaId));
        film.setMpa(filmMpa);

        final Set<Director> filmDirectors = directorService.getFilmDirectorByFilmId(id)
                .stream()
                .map(DirectorMapper::mapToDirector)
                .collect(Collectors.toSet());
        film.setDirectors(filmDirectors);

        return FilmMapper.mapToFilmDto(film);
    }

    public Collection<Like> getDataField(Long userId) {
        return filmStorage.getDataField(userId);
    }

    public Collection<FilmDto> getCommonFilmsWithFriend(Long userId, Long friendId) {
        if (userId == null || userId <= 0 || friendId == null || friendId <= 0) {
            throw new ValidationException("ID пользователя должно быть выше 0");
        }

        UserDto user = Optional.ofNullable(userService.getUserById(userId))
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
        UserDto friend = Optional.ofNullable(userService.getUserById(friendId))
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + friendId + " не найден"));

        Collection<Film> films = filmStorage.getCommonFilmsWithFriend(userId, friendId);
        Collection<MpaDto> mpas = mpaService.getAllMPAs();
        Collection<FilmGenresDto> allFilmsGenres = genreService.getAllFilmsGenres();

        for (Film film : films) {
            final String mpaName = mpas.stream()
                    .filter(mpa -> mpa.getId().equals(film.getMpa().getId()))
                    .findFirst()
                    .map(MpaDto::getName)
                    .get();
            film.getMpa().setName(mpaName);

            final Set<Genre> genres = allFilmsGenres
                    .stream()
                    .filter(genre -> genre.getFilmId().equals(film.getId()))
                    .map(genre -> Genre.builder()
                            .id(genre.getGenreId())
                            .name(genre.getGenreName())
                            .build())
                    .collect(Collectors.toSet());
            film.setGenres(genres);
        }

        return films
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }
}
