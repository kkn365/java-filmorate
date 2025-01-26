package ru.yandex.practicum.filmorate.dal.genre.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.FilmGenresDto;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmsGenresRowMapper implements RowMapper<FilmGenresDto>  {
    @Override
    public FilmGenresDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return FilmGenresDto.builder()
                .filmId(rs.getLong("film_id"))
                .genreId(rs.getInt("genre_id"))
                .genreName(rs.getString("name"))
                .build();
    }
}
