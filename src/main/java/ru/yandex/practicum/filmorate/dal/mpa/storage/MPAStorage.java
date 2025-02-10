package ru.yandex.practicum.filmorate.dal.mpa.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.Collection;

@Component
public interface MPAStorage {
    MPA getMpaById(int id);

    Collection<MPA> getAllMpas();
}
