package ru.yandex.practicum.filmorate.dal.director.DirectorStorage;

import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.dto.DirectorFilmsDto;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.List;


public interface DirectorStorage {

    Collection<Director> getAll();

    Director getDirectorById(Long id);

    Collection<Director> findDirectorsByFilmId(Long filmId);

    Director create(Director director);

    Director update(Director director);

    void deleteAll();

    void deleteById(Long id);

    void addDirectors(Long filmId, List<Long> director);

    Collection<DirectorFilmsDto> getAllFilmsDirector();
}

