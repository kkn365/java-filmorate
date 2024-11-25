package ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private Long filmIdGenerator = 0L;

    public Long getNextId() {
        return ++filmIdGenerator;
    }

    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    @Override
    public Film put(Film newFilm) {
        Film film = Film.builder()
                .id(getNextId())
                .name(newFilm.getName())
                .description(newFilm.getDescription())
                .releaseDate(newFilm.getReleaseDate())
                .duration(newFilm.getDuration())
                .build();
        films.put(film.getId(), film);

        return film;
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film get(Long id) {
        return films.get(id);
    }

}
