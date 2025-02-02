package ru.yandex.practicum.filmorate.dal.director.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.DirectorFilmsDto;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class DirectorFilmsRowMapper implements RowMapper<DirectorFilmsDto> {
    @Override
    public DirectorFilmsDto mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return DirectorFilmsDto.builder()
                .filmId(resultSet.getLong("film_id"))
                .directorId(resultSet.getLong("director_id"))
                .directorName(resultSet.getString("director_name"))
                .build();
    }
}
