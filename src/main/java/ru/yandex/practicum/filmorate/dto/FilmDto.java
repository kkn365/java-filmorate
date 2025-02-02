package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.dto.annotation.MinimumFilmReleaseDate;
import ru.yandex.practicum.filmorate.model.MPA;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class FilmDto {
    private Long id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    @MinimumFilmReleaseDate
    private LocalDate releaseDate;
    @Positive
    private Integer duration;
    private Long liked;
    private MPA mpa;
    private Set<GenreDto> genres;
    private Set<DirectorDto> directors;
}
