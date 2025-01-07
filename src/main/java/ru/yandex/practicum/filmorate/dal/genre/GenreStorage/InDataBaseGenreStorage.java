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

@Repository
@RequiredArgsConstructor
@Slf4j
public class InDataBaseGenreStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreRowMapper genreRowMapper;
    private final FilmsGenresRowMapper filmsGenresRowMapper;

    @Override
    public Genre getGenreById(int id) {
        String sqlQuery = "SELECT genre_id, name FROM genres WHERE genre_id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, genreRowMapper, id);
        } catch (EmptyResultDataAccessException ignored) {
            log.error("Не найден жанр с id={}", id);
            return null;
        }
    }

    @Override
    public Collection<Genre> getAllGenres() {
        String sqlQuery = "SELECT genre_id, name FROM genres ORDER BY genre_id";
        return jdbcTemplate.queryForStream(sqlQuery, genreRowMapper).toList();
    }

    @Override
    public Collection<FilmGenresDto> getAllFilmsGenres() {
        String sqlQuery = "SELECT f.film_id, f.genre_id, g.name FROM films_genres f " +
                "JOIN genres g ON f.genre_id = g.genre_id ";
        return jdbcTemplate.queryForStream(sqlQuery, filmsGenresRowMapper).toList();
    }

    @Override
    public Collection<Genre> getFilmGenres(Long filmId) {
        String sqlQuery = "SELECT f.genre_id, g.name FROM films_genres f " +
                "JOIN genres g ON f.genre_id = g.genre_id WHERE f.film_id = ? " +
                "ORDER BY f.genre_id";
        return jdbcTemplate.queryForStream(sqlQuery, genreRowMapper, filmId).toList();
    }

}
