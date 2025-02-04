package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.event.eventStorage.EventStorage;
import ru.yandex.practicum.filmorate.dal.film.FilmStorage.FilmStorage;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.mapper.MPAMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.assistanceForEvent.EventType;
import ru.yandex.practicum.filmorate.model.assistanceForEvent.Operation;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
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
    private final EventStorage eventStorage;

    private static final Comparator<FilmDto> comparatorByYear = Comparator.comparing(FilmDto::getReleaseDate);
    private static final Comparator<FilmDto> comparatorByLikes = Comparator.comparing(FilmDto::getLiked).reversed();

    public FilmDto getFilmById(Long id) {
        Film film = filmStorage.getFilmById(id);
        if (film == null) {
            final String warnMessage = String.format("Фильм c id=%d не найден в базе данных.", id);
            log.warn(warnMessage);
            throw new NotFoundException(warnMessage);
        }

        final int mpaId = film.getMpa().getId();
        final MPA filmMpa = MPAMapper.mapToMPA(mpaService.getMPAbyId(mpaId));
        film.setMpa(filmMpa);

        final Set<Genre> filmGenres = genreService.getFilmGenresByFilmId(id)
                .stream()
                .map(GenreMapper::mapToGenre)
                .collect(Collectors.toSet());
        film.setGenres(filmGenres);

        final Set<Director> filmDirectors = directorService.getFilmDirectorByFilmId(id)
                .stream()
                .map(DirectorMapper::mapToDirector)
                .collect(Collectors.toSet());
        film.setDirectors(filmDirectors);

        return FilmMapper.mapToFilmDto(film);
    }

    public Collection<FilmDto> getAllFilms() {
        final Collection<Film> allFilms = filmStorage.getAllFilms();
        final Collection<MpaDto> allMPAs = mpaService.getAllMPAs();
        final Collection<FilmGenresDto> allGenres = genreService.getAllFilmsGenres();
        final Collection<DirectorFilmsDto> allDirectors = directorService.getAllFilmDirectors();

        return allFilms.stream()
                .peek(film -> {
                            film.setMpa(allMPAs.stream()
                                    .filter(mpa -> mpa.getId().equals(film.getMpa().getId()))
                                    .findFirst()
                                    .map(MPAMapper::mapToMPA)
                                    .orElse(null)
                            );
                            film.setGenres(allGenres.stream()
                                    .filter(genre -> genre.getFilmId().equals(film.getId()))
                                    .map(genre -> Genre.builder()
                                            .id(genre.getGenreId())
                                            .name(genre.getGenreName())
                                            .build())
                                    .collect(Collectors.toSet())
                            );
                            film.setDirectors(allDirectors.stream()
                                    .filter(director -> director.getFilmId().equals(film.getId()))
                                    .map(director -> Director.builder()
                                            .id(director.getDirectorId())
                                            .name(director.getDirectorName())
                                            .build())
                                    .collect(Collectors.toSet())
                            );
                        }
                )
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public FilmDto addNewFilm(FilmDto film) {
        final Integer mpaId = film.getMpa().getId();
        if (mpaId != null) {
            mpaService.getMPAbyId(mpaId);
        }
        FilmDto newFilm = FilmMapper.mapToFilmDto(filmStorage.addNewFilm(FilmMapper.mapToFilm(film)));
        if (!newFilm.getGenres().isEmpty()) {
            final Collection<GenreDto> currentGenres = genreService.getAllGenres();
            final Set<Integer> incomingGenresIds = newFilm.getGenres()
                    .stream()
                    .map(GenreDto::getId)
                    .collect(Collectors.toSet());
            for (Integer genreId : incomingGenresIds) {
                if (!(currentGenres.stream().map(GenreDto::getId).toList()).contains(genreId)) {
                    log.warn("Не найден жанр с id={}", genreId);
                    throw new NotFoundException(String.format("Жанр с id=%d не найден.", genreId));
                }
            }
            final Set<GenreDto> incomingGenres = currentGenres
                    .stream()
                    .filter(genre -> incomingGenresIds.contains(genre.getId()))
                    .collect(Collectors.toSet());
            newFilm.setGenres(incomingGenres);
            genreService.addGenresToFilm(newFilm);
        }
        if (!newFilm.getDirectors().isEmpty()) {
            final Collection<DirectorDto> currentDirectors = directorService.getAllDirectors();
            final Set<Long> incomingDirectorsIds = newFilm.getDirectors()
                    .stream()
                    .map(DirectorDto::getId)
                    .collect(Collectors.toSet());
            for (Long directorId : incomingDirectorsIds) {
                if (!(currentDirectors.stream().map(DirectorDto::getId).toList()).contains(directorId)) {
                    log.warn("Не найден режиссёр с id={}", directorId);
                    throw new NotFoundException(String.format("Режиссёр с id=%d не найден.", directorId));
                }
            }
            final Set<DirectorDto> incomingDirectors = currentDirectors
                    .stream()
                    .filter(director -> incomingDirectorsIds.contains(director.getId()))
                    .collect(Collectors.toSet());
            newFilm.setDirectors(incomingDirectors);
            directorService.addDirectorsToFilm(newFilm);
        }
        log.info("Добавлен новый фильм: {}", newFilm);
        return getFilmById(newFilm.getId());
    }

    public FilmDto updateFilmData(FilmDto film) {
        final Long id = film.getId();
        if (id == null) {
            log.warn("Не указан id в теле запроса: {}", film);
            throw new ValidationException("Должен быть указан id фильма.");
        }
        getFilmById(id);
        filmStorage.updateCurrentFilm(FilmMapper.mapToFilm(film));
        genreService.deleteGenresFromFilm(id);
        if (film.getGenres() != null) {
            genreService.addGenresToFilm(film);
        }
        directorService.deleteDirectorsFromFilm(id);
        if (film.getDirectors() != null) {
            directorService.addDirectorsToFilm(film);
        }
        FilmDto updatedFilm = getFilmById(id);
        log.info("Обновлены данные фильма: {}", updatedFilm);
        return updatedFilm;
    }

    public String putLike(Long filmId, Long userId) {
        getFilmById(filmId);
        userService.getUserById(userId);
        eventStorage.save(userId, filmId, EventType.LIKE, Operation.ADD);
        filmStorage.putLike(filmId, userId);
        final String message = String.format("Пользователь с id=%d поставил лайк фильму с id=%d.", userId, filmId);
        log.info(message);
        return message;
    }

    public String deleteLike(Long filmId, Long userId) {
        getFilmById(filmId);
        userService.getUserById(userId);
        eventStorage.save(userId, filmId, EventType.LIKE, Operation.REMOVE);
        filmStorage.deleteLike(filmId, userId);
        final String message = String.format("Пользователь с id=%d удалил лайк фильму с id=%d.", userId, filmId);
        log.info(message);
        return message;
    }

    public Collection<FilmDto> getPopular(Integer count, Integer genreId, Integer year) {
        final Set<Long> popularFilmsIds = filmStorage.getPopularFilms(count, genreId, year).stream()
                .map(Film::getId)
                .collect(Collectors.toSet());
        // В тестах add-search ожидается сортировка по убыванию популярности
//        return getAllFilms().stream()
//                .filter(film -> popularFilmsIds.contains(film.getId()))
//                .sorted(Comparator.comparing(FilmDto::getLiked).reversed())
//                .toList();

        // В тестах develop ожидается сортировка по id.
        return getAllFilms().stream()
                .filter(film -> popularFilmsIds.contains(film.getId()))
                .sorted(Comparator.comparing(FilmDto::getId))
                .toList();
    }

    public Collection<Like> getDataField(Long userId) {
        return filmStorage.getDataField(userId);
    }

    public Collection<FilmDto> getCommonFilmsWithFriend(Long userId, Long friendId) {
        if (userId == null || userId <= 0 || friendId == null || friendId <= 0) {
            throw new ValidationException("ID пользователя должно быть выше 0");
        }
        userService.getUserById(userId);
        userService.getUserById(friendId);
        final Collection<Long> filmsIds = filmStorage.getCommonFilmsWithFriend(userId, friendId)
                .stream()
                .map(Film::getId)
                .sorted()
                .toList();
        return getAllFilms()
                .stream()
                .filter(film -> filmsIds.contains(film.getId()))
                .sorted(Comparator.comparingLong(FilmDto::getLiked).reversed())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public String deleteFilmById(Long filmId) {
        getFilmById(filmId);
        filmStorage.deleteFilmById(filmId);
        final String message = String.format("Удален фильм с id=%d.", filmId);
        log.info(message);
        return message;
    }

    public Collection<FilmDto> searchFilms(String query, List<String> by) {
        final Pattern pattern = Pattern.compile(query, Pattern.CASE_INSENSITIVE);
        return getAllFilms()
                .stream()
                .filter(film -> {
                    if (by.contains("title") && (pattern.matcher(film.getName()).find())) return true;
                    if (by.contains("director")) {
                        return film.getDirectors()
                                .stream()
                                .map(DirectorDto::getName)
                                .anyMatch(name -> pattern.matcher(name).find());
                    }
                    return false;
                })
                .sorted(Comparator.comparingLong(FilmDto::getLiked).reversed())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Collection<FilmDto> getFilmsByDirectors(Long directorId, String sortBy) {
        directorService.getDirectorById(directorId);
        final Comparator<FilmDto> comparator = sortBy.equals("year") ? comparatorByYear : comparatorByLikes;
        return getAllFilms()
                .stream()
                .filter(film -> film.getDirectors()
                        .stream()
                        .anyMatch(directorDto -> directorDto.getId().equals(directorId)))
                .sorted(comparator)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
