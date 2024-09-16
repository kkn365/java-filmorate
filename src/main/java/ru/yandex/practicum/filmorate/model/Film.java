package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.MinimumFilmReleaseDate;
import java.time.LocalDate;

@Data
@Builder
public class Film {

    private Long id;

    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;

    @MinimumFilmReleaseDate
    private LocalDate releaseDate;

    @Positive
    private Integer duration;

}
