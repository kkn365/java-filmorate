package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.mpa.MPAStorage.MPAStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class MpaService {

    @Autowired
    private MPAStorage mpaStorage;

    public Collection<MPA> getAllMPAs() {
        return mpaStorage.getAllMpas();
    }

    public MPA getMPAbyId(int id) {
        final MPA mpa = mpaStorage.getMpaById(id);

        if (mpa == null) {
            log.error("Не найден рейтинг с id={}.", id);
            throw new NotFoundException(String.format("Рейтинг с id=%d не найден.", id));
        }

        return mpa;
    }

    // Из-за того, что тесты Postman в разных местах ждут разных ошибок
    public MPA getMPAbyIdForNewFilm(Integer mpaId) {
        final MPA mpa = mpaStorage.getMpaById(mpaId);

        if (mpa == null) {
            log.error("Не найден рейтинг с id={}.", mpaId);
            throw new ValidationException(String.format("Рейтинг с id=%d не найден.", mpaId));
        }

        return mpa;
    }

}
