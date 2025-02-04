package ru.yandex.practicum.filmorate.dal.event.eventStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.event.mapper.EventRowMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.assistanceForEvent.EventType;
import ru.yandex.practicum.filmorate.model.assistanceForEvent.Operation;

import java.sql.PreparedStatement;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class EventDBStorage implements EventStorage {
    private final JdbcOperations jdbcOperations;

    private static final String SAVE_EVENT = "INSERT INTO events (user_id, entity_id, event_type, operation, event_timestamp)" +
            "VALUES (?, ?, ?, ?, ?)";

    @Override
    public void save(Long userId, Long entityId, EventType eventType, Operation operation) {

        jdbcOperations.update(con -> {
            PreparedStatement ps = con.prepareStatement(SAVE_EVENT);
            ps.setLong(1, userId);
            ps.setLong(2, entityId);
            ps.setString(3, eventType.toString());
            ps.setString(4, operation.toString());
            ps.setLong(5, System.currentTimeMillis());
            return ps;
        });
        log.info("Добавлено событие: {} {} {} {}", userId, entityId, eventType, operation);
    }

    @Override
    public List<Event> getEvent(Long userId) {
        String getEvents = "SELECT * FROM events WHERE user_id = ? ORDER BY event_id ASC, entity_id DESC";
        return jdbcOperations.query(getEvents, new EventRowMapper(), userId).stream().toList();
    }

    @Override
    public void deleteEvent(Long userId) {
        String delete = "DELETE FROM events WHERE user_id = ?";
        jdbcOperations.update(delete, userId);
    }

}
