package ru.yandex.practicum.filmorate.dal.film.FilmStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.film.mappers.FilmLikeRowMapper;
import ru.yandex.practicum.filmorate.dal.film.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dto.LikeDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
@Primary
@Repository
public class InDataBaseFilmStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;
    private final FilmLikeRowMapper filmLikeRowMapper;

    @Override
    public Film addNewFilm(Film film) {
        String sqlQuery = "INSERT INTO films(name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        PreparedStatementCreator preparedStatementCreator = con -> {
            PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        };

        jdbcTemplate.update(preparedStatementCreator, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        if (film.getGenres() != null) {
            addNewFilmGenres(film);
        }

        return film;
    }


    private void addNewFilmGenres(Film film) {
        final Long filmId = film.getId();
        StringBuilder builder = new StringBuilder();
        builder.append("INSERT INTO films_genres(film_id, genre_id) VALUES ");
        for (Integer genreId : film.getGenres().stream().map(Genre::getId).toList()) {
            builder.append("(").append(filmId).append(", ").append(genreId).append("), ");
        }
        String sqlQuery = builder.toString().replaceAll(", *$", "");
        jdbcTemplate.execute(sqlQuery);
    }

    @Override
    public Film updateCurrentFilm(Film film) {
        String sqlQuery = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                "WHERE film_id = ?";

        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        return getFilmById(film.getId());
    }

    @Override
    public Film getFilmById(Long id) {
        String sqlQuery = "SELECT film_id, name, description, release_date, duration, mpa_id FROM films " +
                "WHERE film_id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, filmRowMapper, id);
        } catch (EmptyResultDataAccessException ignored) {
            log.error("Не найден фильм с id={}", id);
            return null;
        }
    }

    @Override
    public Collection<Film> getAllFilms() {
        String sqlQuery = "SELECT film_id, name, description, release_date, duration, mpa_id FROM films";
        return jdbcTemplate.queryForStream(sqlQuery, filmRowMapper).toList();
    }

    @Override
    public Film putLike(Long filmId, Long userId) {
        String sqlQuery = "INSERT INTO likes(user_id, film_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
        increaseFilmRank(filmId);
        return getFilmById(filmId);
    }

    @Override
    public LikeDto getLike(Long filmId, Long userId) {
        String sqlQuery = "SELECT user_id, film_id FROM likes WHERE user_id = ? AND film_id = ?";

        try {
            return jdbcTemplate.queryForObject(sqlQuery, filmLikeRowMapper, userId, filmId);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }

    }

    @Override
    public Film deleteLike(Long filmId, Long userId) {
        String sqlQuery = "DELETE FROM public.likes WHERE user_id = ? AND film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
        decreaseFilmRank(filmId);
        return getFilmById(filmId);
    }

    @Override
    public Collection<Film> getPopularFilms(Integer limit) {
        String sqlQuery = "SELECT film_id, name, description, release_date, duration, mpa_id FROM films " +
                "ORDER BY rank DESC LIMIT ?";
        return jdbcTemplate.queryForStream(sqlQuery, filmRowMapper, limit).toList();
    }

    private void increaseFilmRank(Long filmId) {
        String sqlQuery = "UPDATE films SET rank = rank + 1 WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    private void decreaseFilmRank(Long filmId) {
        String sqlQuery = "UPDATE films SET rank = rank - 1 WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

}
