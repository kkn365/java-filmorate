package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.model.MPA;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MPAMapper {

    public static MpaDto mapToMpaDto(MPA mpa) {

        MpaDto dto = MpaDto.builder()
                .id(mpa.getId())
                .name(mpa.getName())
                .build();

        return dto;
    }
}
