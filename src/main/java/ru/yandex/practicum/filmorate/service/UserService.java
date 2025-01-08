package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.user.UserStorage.UserStorage;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    @Autowired
    private UserStorage userStorage;

    public Collection<UserDto> getAllUsers() {
        return userStorage.getAllUsers()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public UserDto addNewUser(UserDto user) {
        User newUser = userStorage.addNewUser(UserMapper.mapToUser(user));
        return UserMapper.mapToUserDto(newUser);
    }

    public UserDto updateUserData(UserDto user) {

        final User userForUpdate = UserMapper.mapToUser(user);
        final Long id = user.getId();

        if (id == null) {
            log.warn("Не указан id в теле запроса: {}", user);
            throw new ValidationException("Должен быть указан id пользователя.");
        }

        if (userStorage.getUserById(id) == null) {
            log.warn("Не найден пользователь с id={}", id);
            throw new NotFoundException(String.format("Пользователь с id=%d не найден.", id));
        }

        User updatedUser = userStorage.updateCurrentUser(userForUpdate);

        return UserMapper.mapToUserDto(updatedUser);
    }

    public Collection<UserDto> addFriend(Long userId, Long friendId) {
        final List<User> friends = new ArrayList<>();
        final User user = userStorage.getUserById(userId);

        if (user == null) {
            log.warn("Не найден пользователь с id={}", userId);
            throw new NotFoundException(String.format("Пользователь с id=%d не найден.", userId));
        }

        final User friend = userStorage.getUserById(friendId);
        if (friend == null) {
            log.warn("Не найден пользователь с id={}", friendId);
            throw new NotFoundException(String.format("Пользователь с id=%d не найден.", friendId));
        }

        userStorage.addFriend(userId, friendId);
        friends.add(user);
        friends.add(friend);

        return friends.stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public Collection<UserDto> deleteFriend(Long userId, Long friendId) {
        final List<User> friends = new ArrayList<>();

        final User user = userStorage.getUserById(userId);
        if (user == null) {
            log.warn("Не найден пользователь с id={}", userId);
            throw new NotFoundException(String.format("Пользователь с id=%d не найден.", userId));
        }

        final User friend = userStorage.getUserById(friendId);
        if (friend == null) {
            log.warn("Не найден пользователь с id={}", friendId);
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

        return friends.stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public Collection<UserDto> getFriends(Long userId) {
        final User user = userStorage.getUserById(userId);

        if (user == null) {
            log.warn("Не найден пользователь с id={}", userId);
            throw new NotFoundException(String.format("Пользователь с id=%d не найден.", userId));
        }

        return userStorage.getFriends(userId).stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public Collection<UserDto> getCommonFriends(Long id, Long otherId) {
        final User user = userStorage.getUserById(id);
        final User otherUser = userStorage.getUserById(otherId);

        if (user == null) {
            log.warn("Не найден пользователь с id={}", id);
            throw new NotFoundException(String.format("Пользователь с id=%d не найден.", id));
        }

        if (otherUser == null) {
            log.warn("Не найден пользователь с id={}", otherId);
            throw new NotFoundException(String.format("Пользователь с id=%d не найден.", otherId));
        }

        return userStorage.getCommonFriends(id, otherId).stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long userId) {
        final User user = userStorage.getUserById(userId);

        if (user == null) {
            log.warn("Не найден пользователь с id={}", userId);
            throw new NotFoundException(String.format("Пользователь с id=%d не найден.", userId));
        }

        return UserMapper.mapToUserDto(user);
    }
}
