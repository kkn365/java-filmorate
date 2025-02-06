package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.model.assistance.EventType;
import ru.yandex.practicum.filmorate.model.assistance.Operation;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {
    Long eventId;
    Long userId;
    Long timestamp;
    Long entityId;
    EventType eventType;
    Operation operation;
}
