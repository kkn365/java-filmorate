package ru.yandex.practicum.filmorate.dal.film.FilmStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.film.mappers.FilmLikeRowMapper;
import ru.yandex.practicum.filmorate.dal.film.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Primary
@Repository
@RequiredArgsConstructor
public class InDataBaseFilmStorage implements FilmStorage {

    private final JdbcOperations jdbcOperations;
    private final FilmRowMapper filmRowMapper;
    private final FilmLikeRowMapper filmLikeRowMapper;

    private static final String INSERT_INTO_FILMS = """
            INSERT INTO films(name, description, release_date, duration, mpa_id)
            VALUES (?, ?, ?, ?, ?)
            """;
    private static final String UPDATE_FILMS = """
            UPDATE films
            SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?
            WHERE film_id = ?
            """;
    private static final String GET_FILM_BY_ID = """
            SELECT film_id, name, description, release_date, duration, mpa_id, liked
            FROM films
            WHERE film_id = ?
            """;
    private static final String GET_ALL_FILMS = """
            SELECT film_id, name, description, release_date, duration, mpa_id, liked
            FROM films
            ORDER BY film_id
            """;
    private static final String INSERT_INTO_LIKES = """
            INSERT INTO likes(user_id, film_id)
            VALUES (?, ?)
            """;
    private static final String GET_LIKE = """
            SELECT user_id, film_id
            FROM likes
            WHERE user_id = ? AND film_id = ?
            """;
    private static final String DELETE_LIKE = """
            DELETE FROM likes
            WHERE film_id = ? AND user_id = ?
            """;
    private static final String INCREASE_FILM_LIKES_COUNT = """
            UPDATE films
            SET liked = liked + 1
            WHERE film_id = ?
            """;
    private static final String DECREASE_FILM_LIKES_COUNT = """
            UPDATE films
            SET liked = liked - 1
            WHERE film_id = ?
            """;
    private static final String GET_DATA_FIELD = """
            WITH user_liked_films AS (
                SELECT fl.film_id
                FROM likes fl
                WHERE fl.user_id = ?
            )
            SELECT user_id, film_id
            FROM likes
            WHERE user_id IN (SELECT fl1.user_id
                              FROM likes fl1
                              WHERE fl1.user_id IN (SELECT DISTINCT fl2.user_id
                                                    FROM likes fl2
                                                    WHERE fl2.film_id IN (SELECT film_id
                                                                          FROM user_liked_films)
                                                   )
                              GROUP BY fl1.user_id
                              HAVING count(fl1.film_id) >= (SELECT count(film_id) FROM user_liked_films) / 2
                             )
            """;
    private static final String DELETE_FILM = """
            DELETE FROM films f
            WHERE f.film_id = ?
            """;
    private static final String GET_USER_LIKES = """
            SELECT *
            FROM likes
            WHERE user_id = ?
            """;

    @Override
    public Film addNewFilm(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator preparedStatementCreator = con -> {
            PreparedStatement stmt = con.prepareStatement(INSERT_INTO_FILMS, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        };
        jdbcOperations.update(preparedStatementCreator, keyHolder);
        Long generatedId = keyHolder.getKeyAs(Long.class);
        if (generatedId != null) {
            film.setId(generatedId);
        } else {
            throw new RuntimeException("Не удалось сохранить фильм");
        }
        return film;
    }

    @Override
    public Film updateCurrentFilm(Film film) {
        jdbcOperations.update(UPDATE_FILMS,
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
        try {
            return jdbcOperations.queryForObject(GET_FILM_BY_ID, filmRowMapper, id);
        } catch (EmptyResultDataAccessException ignored) {
            log.warn("Не найден фильм с id={}", id);
            return null;
        }
    }

    @Override
    public Collection<Film> getAllFilms() {
        return jdbcOperations.queryForStream(GET_ALL_FILMS, filmRowMapper).toList();
    }

    @Override
    public Film putLike(Long filmId, Long userId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator preparedStatementCreator = con -> {
            PreparedStatement stmt = con.prepareStatement(INSERT_INTO_LIKES, new String[]{"like_id"});
            stmt.setLong(1, userId);
            stmt.setLong(2, filmId);
            return stmt;
        };
        jdbcOperations.update(preparedStatementCreator, keyHolder);
        increaseFilmLikesCount(filmId);
        return getFilmById(filmId);
    }

    @Override
    public Like getLike(Long filmId, Long userId) {
        try {
            return jdbcOperations.queryForObject(GET_LIKE, filmLikeRowMapper, userId, filmId);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    @Override
    public Film deleteLike(Long filmId, Long userId) {
        jdbcOperations.update(DELETE_LIKE, filmId, userId);
        decreaseFilmLikesCount(filmId);
        return getFilmById(filmId);
    }

    @Override
    public Collection<Film> getPopularFilms(Integer limit, Integer genreId, Integer year) {
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT f.* FROM films AS f WHERE true");

        if (genreId != null && genreId > 0) {
            sql.append(" AND f.FILM_ID IN (SELECT FILMS_GENRES.FILM_ID FROM FILMS_GENRES WHERE GENRE_ID = ? )");
            params.add(genreId);
        }

        if (year != null && year > 0) {
            sql.append(" AND YEAR(f.release_date) = ? ");
            params.add(year);
        }

        sql.append(" ORDER BY f.liked DESC LIMIT ? ");
        params.add(limit);

        return jdbcOperations.query(sql.toString(), params.toArray(), filmRowMapper);
    }

    @Override
    public Collection<Like> getDataField(Long userId) {
        return jdbcOperations.queryForStream(GET_DATA_FIELD, filmLikeRowMapper, userId).toList();
    }

    @Override
    public Collection<Film> getCommonFilmsWithFriend(Long userId, Long friendId) {
        final List<Like> userLikes = jdbcOperations.query(GET_USER_LIKES, filmLikeRowMapper, userId).stream().toList();
        final List<Like> friendLikes = jdbcOperations.query(GET_USER_LIKES, filmLikeRowMapper, friendId).stream().toList();
        final Set<Long> friendFilmIds = friendLikes.stream()
                .map(Like::getFilmId)
                .collect(Collectors.toSet());
        final Set<Long> filmIds = userLikes.stream()
                .map(Like::getFilmId)
                .filter(friendFilmIds::contains)
                .collect(Collectors.toSet());

        return filmIds.stream()
                .map(this::getFilmById)
                .toList();
    }

    @Override
    public void deleteFilmById(Long filmId) {
        jdbcOperations.update(DELETE_FILM, filmId);
    }

    private void increaseFilmLikesCount(Long filmId) {
        jdbcOperations.update(INCREASE_FILM_LIKES_COUNT, filmId);
    }

    private void decreaseFilmLikesCount(Long filmId) {
        jdbcOperations.update(InDataBaseFilmStorage.DECREASE_FILM_LIKES_COUNT, filmId);
    }

}
