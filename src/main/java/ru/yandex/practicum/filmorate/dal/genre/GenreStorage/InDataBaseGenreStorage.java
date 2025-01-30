package ru.yandex.practicum.filmorate.dal.genre.GenreStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.genre.mappers.FilmsGenresRowMapper;
import ru.yandex.practicum.filmorate.dal.genre.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dto.FilmGenresDto;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class InDataBaseGenreStorage implements GenreStorage {

    private final JdbcOperations jdbcOperations;
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
    private static final String INSERT_FILM_GENRES = """
            INSERT INTO films_genres(film_id, genre_id)
            VALUES (?, ?)
            """;
    private static final String DELETE_GENRES_BY_FILM_ID = """
            DELETE FROM films_genres f
            WHERE f.film_id = ?
            """;

    @Override
    public Genre getGenreById(int id) {
        try {
            return jdbcOperations.queryForObject(GET_GENRE_BY_ID, genreRowMapper, id);
        } catch (EmptyResultDataAccessException ignored) {
            log.warn("Не найден жанр с id={}", id);
            return null;
        }
    }

    @Override
    public Collection<Genre> getAllGenres() {
        return jdbcOperations.queryForStream(GET_ALL_GENRES, genreRowMapper).toList();
    }

    @Override
    public Collection<FilmGenresDto> getAllFilmsGenres() {
        return jdbcOperations.queryForStream(GET_ALL_FILMS_GENRES, filmsGenresRowMapper).toList();
    }

    @Override
    public Collection<Genre> getFilmGenres(Long filmId) {
        return jdbcOperations.queryForStream(GET_FILM_GENRES, genreRowMapper, filmId).collect(Collectors.toSet());
    }

    @Override
    public void addGenres(Long filmId, List<Integer> genresIds) {
        jdbcOperations.batchUpdate(INSERT_FILM_GENRES, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, filmId);
                ps.setInt(2, genresIds.get(i));
            }

            @Override
            public int getBatchSize() {
                return genresIds.size();
            }
        });
    }

    @Override
    public void deleteFilmGenresByFilmId(Long id) {
        jdbcOperations.update(DELETE_GENRES_BY_FILM_ID, id);
    }
}
