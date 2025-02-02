package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DirectorFilmsDto {
    private Long filmId;
    private Long directorId;
    private String directorName;
}
