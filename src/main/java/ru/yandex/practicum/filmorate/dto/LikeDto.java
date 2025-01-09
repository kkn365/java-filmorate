package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LikeDto {
    private Long userId;
    private Long filmId;
}
