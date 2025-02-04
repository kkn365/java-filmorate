package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class Review {
    private Long reviewId;
    @NotBlank
    private String content;
    private Boolean isPositive;
    @NonNull
    private Long userId;
    @NonNull
    private Long filmId;
    @JsonProperty(required = false, defaultValue = "0")
    private Integer useful;
}
