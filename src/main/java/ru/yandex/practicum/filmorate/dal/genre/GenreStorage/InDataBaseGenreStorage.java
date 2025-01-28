package ru.yandex.practicum.filmorate.dal.genre.GenreStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.genre.mappers.FilmsGenresRowMapper;
import ru.yandex.practicum.filmorate.dal.genre.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dto.FilmGenresDto;
import ru.yandex.practicum.filmorate.model.Genre;
import java.util.Collection;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class InDataBaseGenreStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreRowMapper genreRowMapper;
    private final FilmsGenresRowMapper filmsGenresRowMapper;

    private static final String GET_GENRE_BY_ID = """
            SELECT genre_id, name
            FROM genres
            WHERE genre_id = ?
            """;
    private static final String GET_ALL_GENRES = """
            SELECT genre_id, name
            FROM genres
            ORDER BY genre_id
            """;
    private static final String GET_ALL_FILMS_GENRES = """
            SELECT f.film_id, f.genre_id, g.name
            FROM films_genres f
            JOIN genres g ON f.genre_id = g.genre_id
            """;
    private static final String GET_FILM_GENRES = """
            SELECT f.genre_id, g.name
            FROM films_genres f
            JOIN genres g ON f.genre_id = g.genre_id
            WHERE f.film_id = ?
            ORDER BY f.genre_id
            """;

    @Override
    public Genre getGenreById(int id) {
        try {
            return jdbcTemplate.queryForObject(GET_GENRE_BY_ID, genreRowMapper, id);
        } catch (EmptyResultDataAccessException ignored) {
            log.warn("Не найден жанр с id={}", id);
            return null;
        }
    }

    @Override
    public Collection<Genre> getAllGenres() {
        return jdbcTemplate.queryForStream(GET_ALL_GENRES, genreRowMapper).toList();
    }

    @Override
    public Collection<FilmGenresDto> getAllFilmsGenres() {
        return jdbcTemplate.queryForStream(GET_ALL_FILMS_GENRES, filmsGenresRowMapper).toList();
    }

    @Override
    public Collection<Genre> getFilmGenres(Long filmId) {
        return jdbcTemplate.queryForStream(GET_FILM_GENRES, genreRowMapper, filmId)
                .collect(Collectors.toSet());
    }

}
