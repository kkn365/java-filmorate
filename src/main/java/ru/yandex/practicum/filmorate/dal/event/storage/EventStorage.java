package ru.yandex.practicum.filmorate.dal.event.storage;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.assistance.EventType;
import ru.yandex.practicum.filmorate.model.assistance.Operation;
import java.util.List;

public interface EventStorage {
    void save(Long userId, Long entityId, EventType eventType, Operation operation);

    List<Event> getEvent(Long userId);

    void deleteEvent(Long userId);
}
