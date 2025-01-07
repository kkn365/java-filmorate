package ru.yandex.practicum.filmorate.dal.mpa.MPAStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mpa.mappers.MPARowMapper;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.Collection;

@Repository
@RequiredArgsConstructor
@Slf4j
public class InDataBaseMPAStorage implements MPAStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MPARowMapper mpaRowMapper;

    @Override
    public MPA getMpaById(int id) {
        String sqlQuery = "SELECT mpa_id, name FROM mpas WHERE mpa_id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, mpaRowMapper, id);
        } catch (EmptyResultDataAccessException ignored) {
            log.error("Не найден MPA с id={}", id);
            return null;
        }
    }

    @Override
    public Collection<MPA> getAllMpas() {
        String sqlQuery = "SELECT mpa_id, name FROM mpas ORDER BY mpa_id";
        return jdbcTemplate.queryForStream(sqlQuery, mpaRowMapper).toList();
    }
}
