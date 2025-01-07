package ru.yandex.practicum.filmorate.dal.user.UserStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.user.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
@Primary
@Repository
public class InDataBaseUserStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;

    @Override
    public User addNewUser(User user) {
        String sqlQuery = "INSERT INTO users(login, name, email, birthday) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        if (user.getName() == null) {
            user.setName(user.getLogin());
        }

        PreparedStatementCreator preparedStatementCreator = con -> {
            PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"user_id"});
            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getEmail());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        };

        jdbcTemplate.update(preparedStatementCreator, keyHolder);

        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        return user;
    }

    @Override
    public User updateCurrentUser(User user) {
        String sqlQuery = "UPDATE users SET login = ?, name = ?, email = ?, birthday = ? WHERE user_id = ?";

        if (user.getName() == null) {
            user.setName(user.getLogin());
        }

        jdbcTemplate.update(sqlQuery,
                user.getLogin(),
                user.getName(),
                user.getEmail(),
                user.getBirthday(),
                user.getId());

        return user;
    }

    @Override
    public User getUserById(Long id) {
        String sqlQuery = "SELECT user_id, login, name, email, birthday FROM users WHERE user_id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, userRowMapper, id);
        } catch (EmptyResultDataAccessException ignored) {
            log.error("Не найден пользователь с id={}", id);
            return null;
        }
    }

    @Override
    public Collection<User> getAllUsers() {
        String sqlQuery = "SELECT user_id, login, name, email, birthday FROM users";
        return jdbcTemplate.queryForStream(sqlQuery, userRowMapper).toList();
    }

    @Override
    public Collection<User> getFriends(Long id) {
        String sqlQuery = "SELECT u.user_id, u.login, u.name, u.email, u.birthday FROM friendships AS f " +
                "JOIN users AS u ON u.user_id = f.friend_id " +
                "WHERE f.user_id = ?";
        return jdbcTemplate.queryForStream(sqlQuery, userRowMapper, id).toList();
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        String sqlQuery = "INSERT INTO friendships(user_id, friend_id) VALUES (?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        PreparedStatementCreator preparedStatementCreator = con -> {
            PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"friend_id"});
            stmt.setLong(1, userId);
            stmt.setLong(2, friendId);
            return stmt;
        };

        jdbcTemplate.update(preparedStatementCreator, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);
        if (id == null) {
            log.error("Не удалось сохранить данные.");
            throw new InternalServerException("Не удалось сохранить данные.");
        }

    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        String sqlQuery = "DELETE FROM friendships where user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public Collection<User> getCommonFriends(Long userId, Long friendId) {
        String sqlQuery = "SELECT user_id, email, login, name, birthday FROM users " +
                "WHERE user_id IN ( " +
                "SELECT friend_id FROM friendships f1 " +
                "WHERE f1.user_id = ? " +
                "INTERSECT " +
                "SELECT friend_id FROM friendships f2 " +
                "WHERE f2.user_id = ? )";
        return jdbcTemplate.queryForStream(sqlQuery, userRowMapper, userId, friendId).toList();
    }

    @Override
    public Long getFriendShipId(Long userId, Long friendId) {
        String sqlQuery = "SELECT friendship_id FROM friendships WHERE user_id = ? AND friend_id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, Long.class, userId, friendId);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

}
