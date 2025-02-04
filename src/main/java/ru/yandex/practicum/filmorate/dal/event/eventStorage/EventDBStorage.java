package ru.yandex.practicum.filmorate.dal.event.eventStorage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.event.mapper.EventRowMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.assistanceForEvent.EventType;
import ru.yandex.practicum.filmorate.model.assistanceForEvent.Operation;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class EventDBStorage implements EventStorage {
    private final JdbcOperations jdbcOperations;

    private final String SAVE_EVENT = "INSERT INTO events (user_id, entity_id, event_type, operation, event_timestamp)" +
            "VALUES (?, ?, ?, ?, ?)";
    private final String GET_EVENTS = "SELECT * FROM events WHERE user_id = ?";




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
    }

    @Override
    public void saveForReview(Long userId, Long entityId, EventType eventType, Operation operation) {

    }

    @Override
    public List<Event> getEvent(Long userid) {
        return jdbcOperations.query(GET_EVENTS, new EventRowMapper(), userid).stream().toList();
    }
}
