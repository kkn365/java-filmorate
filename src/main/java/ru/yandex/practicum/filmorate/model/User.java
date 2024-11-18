package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
public class User {

    private Long id;

    @Email
    private String email;

    @NotBlank
    private String login;

    private String name;

    @Past
    private LocalDate birthday;

}
