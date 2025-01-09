package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Genre;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GenreMapper {

    public static Genre mapToGenre(GenreDto request) {

        Genre genre = Genre.builder()
                .id(request.getId())
                .name(request.getName())
                .build();

        return genre;
    }

    public static GenreDto mapToGenreDto(Genre genre) {

        GenreDto dto = GenreDto.builder()
                .id(genre.getId())
                .name(genre.getName())
                .build();

        return dto;
    }

}
