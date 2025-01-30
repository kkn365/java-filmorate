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
import ru.yandex.practicum.filmorate.dal.genre.GenreStorage.GenreStorage;
import ru.yandex.practicum.filmorate.dal.user.UserStorage.UserStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
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

    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;
    private final FilmLikeRowMapper filmLikeRowMapper;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;

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
            SELECT film_id, name, description, release_date, duration, mpa_id
            FROM films
            WHERE film_id = ?
            """;
    private static final String GET_ALL_FILMS = """
            SELECT film_id, name, description, release_date, duration, mpa_id
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
    private static final String GET_POPULAR_FILMS = """
            SELECT film_id, name, description, release_date, duration, mpa_id
            FROM films
            ORDER BY rank DESC
            LIMIT ?
            """;
    private static final String INSERT_INTO_FILM_GENRES = """
            INSERT INTO films_genres(film_id, genre_id) VALUES
            """;
    private static final String INCREASE_FILM_RANK = """
            UPDATE films
            SET rank = rank + 1
            WHERE film_id = ?
            """;
    private static final String DECREASE_FILM_RANK = """
            UPDATE films
            SET rank = rank - 1
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
        jdbcTemplate.update(preparedStatementCreator, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        if (film.getGenres() != null) {
            addNewFilmGenres(film);
        }
        return film;
    }

    @Override
    public Film updateCurrentFilm(Film film) {
        jdbcTemplate.update(UPDATE_FILMS,
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
            return jdbcTemplate.queryForObject(GET_FILM_BY_ID, filmRowMapper, id);
        } catch (EmptyResultDataAccessException ignored) {
            log.warn("Не найден фильм с id={}", id);
            return null;
        }
    }

    @Override
    public Collection<Film> getAllFilms() {
        return jdbcTemplate.queryForStream(GET_ALL_FILMS, filmRowMapper).toList();
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
        jdbcTemplate.update(preparedStatementCreator, keyHolder);
        increaseFilmRank(filmId);
        return getFilmById(filmId);
    }

    @Override
    public Like getLike(Long filmId, Long userId) {
        try {
            return jdbcTemplate.queryForObject(GET_LIKE, filmLikeRowMapper, userId, filmId);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    @Override
    public Film deleteLike(Long filmId, Long userId) {
        jdbcTemplate.update(DELETE_LIKE, filmId, userId);
        decreaseFilmRank(filmId);
        return getFilmById(filmId);
    }

     @Override
     public Collection<Film> getPopularFilms(Integer limit, Integer genreId, Integer year) {
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT f.* FROM films AS f WHERE true");

            if (genreId != null && genreId > 0) {
                sql.append(" AND f.FILM_ID IN (SELECT FILMS_GENRES.FILM_ID FROM FILMS_GENRES WHERE GENRE_ID = ?)");
                params.add(genreId);
            }

            if (year != null && year > 0) {
                sql.append(" AND YEAR(f.release_date) = ?");
                params.add(year);
            }

            sql.append(" ORDER BY f.rank DESC LIMIT ?");
            params.add(limit);

            List<Film> films = jdbcTemplate.query(sql.toString(), params.toArray(), filmRowMapper);

            films.forEach(film -> film.setGenres(new HashSet<>(genreStorage.getFilmGenres(film.getId()))));

            return films;
        }

    @Override
    public Collection<Like> getDataField(Long userId) {
        return jdbcTemplate.queryForStream(GET_DATA_FIELD, filmLikeRowMapper, userId).toList();
    }

    @Override
    public Collection<Film> getCommonFilmsWithFriend(Long userId, Long friendId) {
        String sql = "SELECT * " +
                "FROM likes " +
                "WHERE user_id = ? ";

        List<Like> userLikes = jdbcTemplate.query(sql, filmLikeRowMapper, userId).stream().toList();
        List<Like> friendLikes = jdbcTemplate.query(sql, filmLikeRowMapper, friendId).stream().toList();

        Set<Long> friendFilmIds = friendLikes.stream()
                .map(Like::getFilmId)
                .collect(Collectors.toSet());

        Set<Long> filmIds = userLikes.stream()
                .map(Like::getFilmId)
                .filter(friendFilmIds::contains)
                .collect(Collectors.toSet());

        return filmIds.stream()
                .map(this::getFilmById)
                .toList();
    }

    private void addNewFilmGenres(Film film) {
        final Long filmId = film.getId();
        StringBuilder builder = new StringBuilder();
        builder.append(InDataBaseFilmStorage.INSERT_INTO_FILM_GENRES);
        for (Integer genreId : film.getGenres().stream().map(Genre::getId).toList()) {
            builder.append("(").append(filmId).append(", ").append(genreId).append("), ");
        }

        String sqlQuery = builder.toString().replaceAll(", *$", "");
        jdbcTemplate.execute(sqlQuery);
    }

    private void increaseFilmRank(Long filmId) {
        jdbcTemplate.update(INCREASE_FILM_RANK, filmId);
    }

    private void decreaseFilmRank(Long filmId) {
        jdbcTemplate.update(InDataBaseFilmStorage.DECREASE_FILM_RANK, filmId);
    }

}
