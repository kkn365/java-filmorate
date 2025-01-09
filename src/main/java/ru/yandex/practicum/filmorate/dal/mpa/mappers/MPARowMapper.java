package ru.yandex.practicum.filmorate.dal.mpa.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MPARowMapper implements RowMapper<MPA> {
    @Override
    public MPA mapRow(ResultSet resultSet, int rowNum) throws SQLException {

        MPA mpa = MPA.builder()
                .id(resultSet.getInt("mpa_id"))
                .name(resultSet.getString("name"))
                .build();

        return mpa;

    }
}
