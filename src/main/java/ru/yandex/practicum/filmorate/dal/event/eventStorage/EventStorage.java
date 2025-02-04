package ru.yandex.practicum.filmorate.dal.event.eventStorage;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.assistanceForEvent.EventType;
import ru.yandex.practicum.filmorate.model.assistanceForEvent.Operation;
import java.util.List;

public interface EventStorage {
    void save(Long userId, Long entityId, EventType eventType, Operation operation);

    List<Event> getEvent(Long userId);

    void deleteEvent(Long Id);
}
