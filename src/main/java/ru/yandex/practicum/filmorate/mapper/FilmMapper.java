package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilmMapper {

    public static Film mapToFilm(FilmDto request) {
        return Film.builder()
                .id(request.getId())
                .name(request.getName())
                .description(request.getDescription())
                .releaseDate(request.getReleaseDate())
                .duration(request.getDuration())
                .liked(request.getLiked() == null ? 0 : request.getLiked())
                .mpa(request.getMpa())
                .genres(Optional.ofNullable(request.getGenres())
                        .orElseGet(Collections::emptySet)
                        .stream()
                        .map(GenreMapper::mapToGenre)
                        .sorted(Comparator.comparingInt(Genre::getId))
                        .collect(Collectors.toCollection(LinkedHashSet::new)))
                .directors(Optional.ofNullable(request.getDirectors())
                        .orElseGet(Collections::emptySet)
                        .stream()
                        .map(DirectorMapper::mapToDirector)
                        .sorted(Comparator.comparingLong(Director::getId))
                        .collect(Collectors.toCollection(LinkedHashSet::new)))
                .build();
    }

    public static FilmDto mapToFilmDto(Film film) {
        return FilmDto.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .liked(film.getLiked() == null ? 0 : film.getLiked())
                .mpa(film.getMpa())
                .genres(Optional.ofNullable(film.getGenres())
                        .orElseGet(Collections::emptySet)
                        .stream()
                        .map(GenreMapper::mapToGenreDto)
                        .sorted(Comparator.comparingInt(GenreDto::getId))
                        .collect(Collectors.toCollection(LinkedHashSet::new)))
                .directors(Optional.ofNullable(film.getDirectors())
                        .orElseGet(Collections::emptySet)
                        .stream()
                        .map(DirectorMapper::mapToDirectorDto)
                        .sorted(Comparator.comparingLong(DirectorDto::getId))
                        .collect(Collectors.toCollection(LinkedHashSet::new)))
                .build();
    }
}
