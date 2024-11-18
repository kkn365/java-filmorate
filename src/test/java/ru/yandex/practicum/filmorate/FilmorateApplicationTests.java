package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;


@SpringBootTest
class FilmorateApplicationTests {

    @Autowired
    FilmController filmController;

    @Test
    void shouldCreateNewFilm() {
        Film film = filmController.createFilm(Film.builder()
                .name(" ")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100)
                .build());
        // Тест должен бросать исклчение валидации, т.к. поле name помечено аннотацией @NotEmpty но не делает этого
    }

}
