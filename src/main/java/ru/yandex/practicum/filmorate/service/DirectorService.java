package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.director.DirectorStorage.DirectorStorage;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.dto.DirectorFilmsDto;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DirectorService {

    private final DirectorStorage directorStorage;

    public Collection<DirectorDto> getAllDirectors() {
        return directorStorage.getAll()
                .stream()
                .map(DirectorMapper::mapToDirectorDto)
                .collect(Collectors.toList());
    }

    public DirectorDto getDirectorById(Long id) {
        final Director director = directorStorage.getDirectorById(id);

        if (director == null) {
            log.warn("Режиссер с id={} не найден", id);
            throw new NotFoundException(String.format("Режиссер с id=%d не найден", id));
        }
        return DirectorMapper.mapToDirectorDto(director);
    }

    public DirectorDto createDirector(DirectorDto director) {
        Director newDirector = directorStorage.create(DirectorMapper.mapToDirector(director));
        log.info("Добавлен новый режиссёр: {}", newDirector);
        return DirectorMapper.mapToDirectorDto(newDirector);
    }

    public DirectorDto updateDirector(DirectorDto director) {
        Long id = director.getId();
        if (directorStorage.getDirectorById(id) == null) {
            throw new NotFoundException("Not found");
        }
        Director updatedDirector = directorStorage.update(DirectorMapper.mapToDirector(director));
        log.info("ОБновлены данные режиссёра: {}", updatedDirector);
        return DirectorMapper.mapToDirectorDto(updatedDirector);
    }

    public String deleteDirector(Long id) {
        getDirectorById(id);
        directorStorage.deleteById(id);
        final String message = String.format("Удалён режиссер с id=%d.", id);
        log.info(message);
        return message;
    }

    public void addDirectorsToFilm(FilmDto film) {
        final Long filmId = film.getId();
        final List<Long> directorsIds = film.getDirectors().stream()
                .map(DirectorDto::getId)
                .toList();
        directorStorage.addDirectors(filmId, directorsIds);
    }

    public Collection<DirectorFilmsDto> getAllFilmDirectors() {
        return directorStorage.getAllFilmsDirector();
    }

    public Collection<DirectorDto> getFilmDirectorByFilmId(Long filmId) {
        return directorStorage.findDirectorsByFilmId(filmId)
                .stream()
                .map(DirectorMapper::mapToDirectorDto)
                .collect(Collectors.toList());
    }

    public void deleteDirectorsFromFilm(Long id) {
        directorStorage.deleteFilmDirectorsByFilmId(id);
    }
}
