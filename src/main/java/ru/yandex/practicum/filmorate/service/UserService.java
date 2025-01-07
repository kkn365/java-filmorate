package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.user.UserStorage.UserStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    @Autowired
    private UserStorage userStorage;

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User addNewUser(User user) {
        return userStorage.addNewUser(user);
    }

    public User updateUserData(User user) {
        final Long id = user.getId();

        if (id == null) {
            log.error("Не указан id в теле запроса: {}", user);
            throw new ValidationException("Должен быть указан id пользователя.");
        }

        if (userStorage.getUserById(id) == null) {
            log.error("Не найден пользователь с id={}", id);
            throw new NotFoundException(String.format("Пользователь с id=%d не найден.", id));
        }

        return userStorage.updateCurrentUser(user);
    }

    public Collection<User> addFriend(Long userId, Long friendId) {
        final List<User> friends = new ArrayList<>();
        final User user = userStorage.getUserById(userId);

        if (user == null) {
            log.error("Не найден пользователь с id={}", userId);
            throw new NotFoundException(String.format("Пользователь с id=%d не найден.", userId));
        }

        final User friend = userStorage.getUserById(friendId);
        if (friend == null) {
            log.error("Не найден пользователь с id={}", friendId);
            throw new NotFoundException(String.format("Пользователь с id=%d не найден.", friendId));
        }

        userStorage.addFriend(userId, friendId);
        friends.add(user);
        friends.add(friend);

        return friends;
    }

    public Collection<User> deleteFriend(Long userId, Long friendId) {
        final List<User> friends = new ArrayList<>();

        final User user = userStorage.getUserById(userId);
        if (user == null) {
            log.error("Не найден пользователь с id={}", userId);
            throw new NotFoundException(String.format("Пользователь с id=%d не найден.", userId));
        }

        final User friend = userStorage.getUserById(friendId);
        if (friend == null) {
            log.error("Не найден пользователь с id={}", friendId);
            throw new NotFoundException(String.format("Пользователь с id=%d не найден.", friendId));
        }

        final Long friendshipId = userStorage.getFriendShipId(userId, friendId);
        if (friendshipId == null) {
            log.warn("Пользователь с id={} не является другом пользователя с id={}.", friendId, userId);
        } else {
            userStorage.deleteFriend(userId, friendId);
            friends.add(user);
            friends.add(friend);
        }

        return friends;
    }

    public Collection<User> getFriends(Long userId) {
        final User user = userStorage.getUserById(userId);

        if (user == null) {
            log.error("Не найден пользователь с id={}", userId);
            throw new NotFoundException(String.format("Пользователь с id=%d не найден.", userId));
        }

        return userStorage.getFriends(userId);
    }

    public Collection<User> getCommonFriends(Long id, Long otherId) {
        final User user = userStorage.getUserById(id);
        final User otherUser = userStorage.getUserById(otherId);

        if (user == null) {
            log.error("Не найден пользователь с id={}", id);
            throw new NotFoundException(String.format("Пользователь с id=%d не найден.", id));
        }

        if (otherUser == null) {
            log.error("Не найден пользователь с id={}", otherId);
            throw new NotFoundException(String.format("Пользователь с id=%d не найден.", otherId));
        }

        return userStorage.getCommonFriends(id, otherId);
    }

    public User getUserById(Long userId) {
        final User user = userStorage.getUserById(userId);

        if (user == null) {
            log.error("Не найден пользователь с id={}", userId);
            throw new NotFoundException(String.format("Пользователь с id=%d не найден.", userId));
        }

        return user;
    }
}
