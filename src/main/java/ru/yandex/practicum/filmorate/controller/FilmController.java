package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exception.*;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        log.info("The list of films has been sent.");
        return films.values();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film newFilm) {

        Film film = Film.builder()
                    .id(getNextId())
                    .name(newFilm.getName())
                    .description(newFilm.getDescription())
                    .releaseDate(newFilm.getReleaseDate())
                    .duration(newFilm.getDuration())
                    .build();

        films.put(film.getId(), film);
        log.info("The film has been added to the list: {}", film);
        return film;
    }

    @PutMapping
    public ResponseEntity<?> updateFilm(@Valid @RequestBody Film film) {
        try {
            return new ResponseEntity<>(update(film), HttpStatus.OK);
        } catch (NotFoundException e) {
            log.error("Not found error: {}", e.getMessage());
            return new ResponseEntity<>(new ErrorMessage(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (ValidationException e) {
            log.error("Validation error: {}", e.getMessage());
            return new ResponseEntity<>(new ErrorMessage(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Film update(Film film) throws ValidationException, NotFoundException {

        final Long id = film.getId();

        if (id == null) {
            throw new ValidationException("Id must be set");
        }

        Film oldFilm = films.get(id);

        if (oldFilm == null) {
            throw new NotFoundException("Film with id = " + film.getId() + " not found");
        }

        final String oldName = oldFilm.getName();
        final String newName = film.getName();
        if (!oldName.equals(newName)) {
            oldFilm.setName(newName);
            log.info("Film id={} field 'name' updated. Old value: [{}]. New value: [{}]", id, oldName, newName);
        }

        final String oldDescription = oldFilm.getDescription();
        final String newDescription = film.getDescription();
        if (!oldDescription.equals(newDescription)) {
            oldFilm.setDescription(newDescription);
            log.info("Film id={} field 'description' updated. Old value: [{}]. New value: [{}]",
                    id, oldDescription, newDescription);
        }

        final LocalDate oldReleaseDate = oldFilm.getReleaseDate();
        final LocalDate newReleaseDate = film.getReleaseDate();
        if (!oldReleaseDate.equals(newReleaseDate)) {
            oldFilm.setReleaseDate(newReleaseDate);
            log.info("Film id={} field 'releaseDate' updated. Old value: [{}]. New value: [{}]",
                    id, oldReleaseDate, newReleaseDate);
        }

        final Integer oldDuration = oldFilm.getDuration();
        final Integer newDuration = film.getDuration();
        if (!oldDuration.equals(newDuration)) {
            oldFilm.setDuration(newDuration);
            log.info("Film id={} field 'duration' updated. Old value: [{}]. New value: [{}]",
                    id, oldDuration, newDuration);
        }

        return oldFilm;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
