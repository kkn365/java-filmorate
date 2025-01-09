package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FilmGenresDto {

    private Long filmId;
    private Integer genreId;
    private String genreName;

}
