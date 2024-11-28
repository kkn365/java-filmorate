package ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Film put(Film film);

    Film update(Film film);

    Film get(Long id);

    Collection<Film> getAll();

}
