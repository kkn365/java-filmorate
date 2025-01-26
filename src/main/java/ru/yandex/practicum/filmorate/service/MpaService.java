package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.mpa.MPAStorage.MPAStorage;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.MPAMapper;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaService {

    private final MPAStorage mpaStorage;

    public Collection<MpaDto> getAllMPAs() {
        return mpaStorage.getAllMpas()
                .stream()
                .map(MPAMapper::mapToMpaDto)
                .collect(Collectors.toList());
    }

    public MpaDto getMPAbyId(int id) {
        final MPA mpa = mpaStorage.getMpaById(id);

        if (mpa == null) {
            log.warn("Не найден рейтинг с id={}.", id);
            throw new NotFoundException(String.format("Рейтинг с id=%d не найден.", id));
        }

        return MPAMapper.mapToMpaDto(mpa);
    }

    // Из-за того, что тесты Postman в разных местах ждут разных ошибок
    public MpaDto getMPAbyIdForNewFilm(Integer mpaId) {
        final MPA mpa = mpaStorage.getMpaById(mpaId);

        if (mpa == null) {
            log.warn("Не найден рейтинг с id={}.", mpaId);
            throw new ValidationException(String.format("Рейтинг с id=%d не найден.", mpaId));
        }

        return MPAMapper.mapToMpaDto(mpa);
    }

}
