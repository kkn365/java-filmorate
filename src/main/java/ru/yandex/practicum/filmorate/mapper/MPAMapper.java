package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.model.MPA;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MPAMapper {

    public static MPA mapToMPA(MpaDto request) {
        return MPA.builder()
                .id(request.getId())
                .name(request.getName())
                .build();
    }

    public static MpaDto mapToMpaDto(MPA mpa) {
        return MpaDto.builder()
                .id(mpa.getId())
                .name(mpa.getName())
                .build();
    }
}
