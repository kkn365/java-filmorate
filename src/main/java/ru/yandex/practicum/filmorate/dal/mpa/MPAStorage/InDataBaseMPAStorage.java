package ru.yandex.practicum.filmorate.dal.mpa.MPAStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mpa.mappers.MPARowMapper;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.Collection;

@Repository
@RequiredArgsConstructor
@Slf4j
public class InDataBaseMPAStorage implements MPAStorage {

    private final JdbcOperations jdbcOperations;
    private final MPARowMapper mpaRowMapper;

    private static final String GET_MPA_BY_ID = """
            SELECT mpa_id, name
            FROM mpas
            WHERE mpa_id = ?
            """;
    private static final String GET_ALL_MPAS = """
            SELECT mpa_id, name
            FROM mpas
            ORDER BY mpa_id
            """;

    @Override
    public MPA getMpaById(int id) {
        try {
            return jdbcOperations.queryForObject(GET_MPA_BY_ID, mpaRowMapper, id);
        } catch (EmptyResultDataAccessException ignored) {
            log.warn("Не найден MPA с id={}", id);
            return null;
        }
    }

    @Override
    public Collection<MPA> getAllMpas() {
        return jdbcOperations.queryForStream(GET_ALL_MPAS, mpaRowMapper).toList();
    }
}
