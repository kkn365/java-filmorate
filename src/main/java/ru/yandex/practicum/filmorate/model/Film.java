package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.MinimumFilmReleaseDate;

import java.time.LocalDate;
import java.util.Set;

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

    @JsonIgnore
    private Set<Long> userIds;

    private int rate;

    public void addLike(long userId) {
        userIds.add(userId);
        rate = userIds.size();
    }

    public void removeLike(long userId) {
        userIds.remove(userId);
        rate = userIds.size();
    }

}
