package ru.yandex.practicum.filmorate.dal.user.UserStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.user.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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

    private final JdbcOperations jdbcOperations;
    private final UserRowMapper userRowMapper;

    private static final String INSERT_INTO_USERS = """
            INSERT INTO users(login, name, email, birthday)
            VALUES (?, ?, ?, ?)
            """;
    private static final String UPDATE_USER = """
            UPDATE users
            SET login = ?, name = ?, email = ?, birthday = ?
            WHERE user_id = ?
            """;
    private static final String GET_USER_BY_ID = """
            SELECT user_id, login, name, email, birthday
            FROM users
            WHERE user_id = ?
            """;
    private static final String GET_ALL_USERS = """
            SELECT user_id, login, name, email, birthday
            FROM users
            ORDER BY user_id
            """;
    private static final String GET_FRIENDS = """
            SELECT u.user_id, u.login, u.name, u.email, u.birthday
            FROM friendships AS f
            JOIN users AS u ON u.user_id = f.friend_id
            WHERE f.user_id = ?
            ORDER BY u.user_id
            """;
    private static final String INSERT_INTO_FRIENDSHIPS = """
            INSERT INTO friendships(user_id, friend_id)
            VALUES (?, ?)
            """;
    private static final String DELETE_FRIEND = """
            DELETE FROM friendships
            WHERE user_id = ? AND friend_id = ?
            """;
    private static final String GET_COMMON_FRIENDS = """
            SELECT user_id, email, login, name, birthday
            FROM users
            WHERE user_id IN (SELECT f1.friend_id
                              FROM friendships f1
                              WHERE f1.user_id = ?
                              INTERSECT
                              SELECT f2.friend_id
                              FROM friendships f2
                              WHERE f2.user_id = ? )
            """;
    private static final String GET_FRIENDSHIP_ID = """
            SELECT friendship_id
            FROM friendships
            WHERE user_id = ? AND friend_id = ?
            """;
    private static final String DELETE_USER = """
            DELETE FROM users u
            WHERE u.user_id = ?
            """;
    private static final String DOWNGRADE_FILM_RATING = """
            UPDATE films f
            SET f.liked = f.liked - 1
            WHERE f.film_id IN (SELECT l.film_id
                                FROM likes l
                                WHERE l.user_id = ? )
            """;

    private static final String UPDATE_REVIEW_RATING = """
            UPDATE reviews r
            SET r.useful = r.useful - (
                SELECT COUNT(*) FROM review_marks rm
                WHERE rm.review_id = r.review_id
                AND rm.user_id = ?
                AND rm.is_useful = TRUE
            ) + (
                SELECT COUNT(*) FROM review_marks rm
                WHERE rm.review_id = r.review_id
                AND rm.user_id = ?
                AND rm.is_useful = FALSE
            )
            WHERE EXISTS (
                SELECT 1 FROM review_marks rm
                WHERE rm.review_id = r.review_id
                AND rm.user_id = ?
            );
            """;


    @Override
    public User addNewUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator preparedStatementCreator = con -> {
            PreparedStatement stmt = con.prepareStatement(INSERT_INTO_USERS, new String[]{"user_id"});
            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getEmail());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        };
        jdbcOperations.update(preparedStatementCreator, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return user;
    }

    @Override
    public User updateCurrentUser(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        jdbcOperations.update(UPDATE_USER,
                user.getLogin(),
                user.getName(),
                user.getEmail(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    @Override
    public User getUserById(Long id) {
        try {
            return jdbcOperations.queryForObject(GET_USER_BY_ID, userRowMapper, id);
        } catch (EmptyResultDataAccessException ignored) {
            log.warn("Не найден пользователь с id={}", id);
            return null;
        }
    }

    @Override
    public Collection<User> getAllUsers() {
        return jdbcOperations.queryForStream(GET_ALL_USERS, userRowMapper).toList();
    }

    @Override
    public Collection<User> getFriends(Long id) {
        return jdbcOperations.queryForStream(GET_FRIENDS, userRowMapper, id).toList();
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator preparedStatementCreator = con -> {
            PreparedStatement stmt = con.prepareStatement(INSERT_INTO_FRIENDSHIPS, new String[]{"friend_id"});
            stmt.setLong(1, userId);
            stmt.setLong(2, friendId);
            return stmt;
        };
        jdbcOperations.update(preparedStatementCreator, keyHolder);
        Long id = keyHolder.getKeyAs(Long.class);
        if (id == null) {
            log.warn("Не удалось сохранить данные.");
            throw new InternalServerException("Не удалось сохранить данные.");
        }
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        jdbcOperations.update(DELETE_FRIEND, userId, friendId);
    }

    @Override
    public Collection<User> getCommonFriends(Long userId, Long friendId) {
        return jdbcOperations.queryForStream(GET_COMMON_FRIENDS, userRowMapper, userId, friendId).toList();
    }

    @Override
    public Long getFriendShipId(Long userId, Long friendId) {
        try {
            return jdbcOperations.queryForObject(GET_FRIENDSHIP_ID, Long.class, userId, friendId);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    @Override
    public void deleteUserById(Long userId) {
        if (getUserById(userId) == null) {
            throw new NotFoundException("Пользователь не найден в базе данных");
        }
        jdbcOperations.update(DOWNGRADE_FILM_RATING, userId);
        jdbcOperations.update(UPDATE_REVIEW_RATING, userId, userId, userId);
        jdbcOperations.update(DELETE_USER, userId);
    }
}
