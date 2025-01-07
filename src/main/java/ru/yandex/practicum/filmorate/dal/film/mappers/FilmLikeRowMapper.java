package ru.yandex.practicum.filmorate.dal.film.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.LikeDto;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmLikeRowMapper implements RowMapper<LikeDto> {
    @Override
    public LikeDto mapRow(ResultSet rs, int rowNum) throws SQLException {

        LikeDto dto = LikeDto.builder()
                .userId(rs.getLong("user_id"))
                .filmId(rs.getLong("film_id"))
                .build();

        return dto;

    }
}
