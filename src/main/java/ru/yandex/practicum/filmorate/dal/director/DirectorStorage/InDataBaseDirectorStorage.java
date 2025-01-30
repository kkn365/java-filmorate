package ru.yandex.practicum.filmorate.dal.director.DirectorStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.director.mappers.DirectorFilmsRowMapper;
import ru.yandex.practicum.filmorate.dal.director.mappers.DirectorRowMapper;
import ru.yandex.practicum.filmorate.dto.DirectorFilmsDto;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class InDataBaseDirectorStorage implements DirectorStorage {


    private static final String FIND_ALL_QUERY = "SELECT * FROM DIRECTORS ORDER BY DIRECTOR_ID"; //список всех режиссеров
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM DIRECTORS WHERE DIRECTOR_ID = ?"; //получение режиссера по айди
    private static final String FIND_DIRECTORS_BY_FILM = "SELECT d.DIRECTOR_ID, d.DIRECTOR_NAME FROM DIRECTORS AS" +
            " d LEFT JOIN DIRECTOR_FILMS fd ON d.DIRECTOR_ID = fd.DIRECTOR_ID WHERE fd.FILM_ID = ? ORDER BY d.DIRECTOR_ID";
    private static final String INSERT_QUERY = "INSERT INTO DIRECTORS (DIRECTOR_NAME) VALUES (?)"; //создание режиссера
    private static final String UPDATE_QUERY = "UPDATE DIRECTORS SET DIRECTOR_NAME = ? WHERE DIRECTOR_ID = ?"; // изменение режиссера
    private static final String DELETE_ALL_QUERY = "DELETE FROM DIRECTORS"; //удалить режиссера
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM DIRECTORS WHERE DIRECTOR_ID = ?"; //удалить режиссера по айди фильма
    private static final String INSERT_FILM_DIRECTORS_QUERY = "INSERT INTO DIRECTOR_FILMS (FILM_ID, DIRECTOR_ID)" //Добавить связь режиссера и фильма
            + "VALUES (?, ?) ";
    private static final String GET_ALL_FILMS_DIRECTORS = "SELECT df.film_id, df.director_id, d.director_name" +
            " FROM director_films df JOIN directors d ON df.director_id = d.director_id"; //получение всех фильмов и связанных с ними режиссеров

    private final JdbcTemplate jdbcTemplate;
    private final DirectorRowMapper directorRowMapper;
    private final DirectorFilmsRowMapper directorFilmsRowMapper;

    @Override
    public Collection<Director> getAll() {
        return jdbcTemplate.queryForStream(FIND_ALL_QUERY, directorRowMapper).toList();
    }

    @Override
    public Director getDirectorById(Long id) {
        try {
            return jdbcTemplate.queryForObject(FIND_BY_ID_QUERY, directorRowMapper, id);
        } catch (EmptyResultDataAccessException ignored) {
            log.warn("Режиссер с id={} не найден", id);
            return null;
        }
    }

    @Override
    public Collection<Director> findDirectorsByFilmId(Long filmId) {
        return jdbcTemplate.queryForStream(FIND_DIRECTORS_BY_FILM,
                directorRowMapper, filmId).toList();
    }

    @Override
    public Director create(Director director) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con
                    .prepareStatement(INSERT_QUERY, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);

        Long generatedId = keyHolder.getKeyAs(Long.class);
        if (generatedId != null) {
            director.setId(generatedId);
        } else {
            throw new RuntimeException("Не удалось сохранить директора");
        }
        return director;
    }


    @Override
    public Director update(Director director) {
        jdbcTemplate.update(UPDATE_QUERY, director.getName(), director.getId());
        return getDirectorById(director.getId());
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update(DELETE_ALL_QUERY);
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update(DELETE_BY_ID_QUERY, id);
    }

    @Override
    public void addDirectors(Long filmId, List<Long> director) {
        jdbcTemplate.batchUpdate(INSERT_FILM_DIRECTORS_QUERY, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, filmId); //
                ps.setLong(2, director.get(i));
            }

            @Override
            public int getBatchSize() {
                return director.size();
            }
        });
    }

    @Override
    public Collection<DirectorFilmsDto> getAllFilmsDirector() {
        return jdbcTemplate.queryForStream(GET_ALL_FILMS_DIRECTORS, directorFilmsRowMapper).toList();
    }

}
