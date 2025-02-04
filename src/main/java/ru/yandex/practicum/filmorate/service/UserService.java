package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.event.eventStorage.EventStorage;
import ru.yandex.practicum.filmorate.dal.user.UserStorage.UserStorage;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.assistanceForEvent.EventType;
import ru.yandex.practicum.filmorate.model.assistanceForEvent.Operation;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserService {

    private final UserStorage userStorage;
    private final EventStorage eventStorage;

    public UserDto getUserById(Long userId) {
        final User user = userStorage.getUserById(userId);
        if (user == null) {
            log.warn("Не найден пользователь с id={}", userId);
            throw new NotFoundException(String.format("Пользователь с id=%d не найден.", userId));
        }
        return UserMapper.mapToUserDto(user);
    }

    public Collection<UserDto> getAllUsers() {
        return userStorage.getAllUsers()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public UserDto addNewUser(UserDto user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        User newUser = userStorage.addNewUser(UserMapper.mapToUser(user));
        log.info("Добавлен новый пользователь: {}", newUser);
        return UserMapper.mapToUserDto(newUser);
    }

    public UserDto updateUserData(UserDto user) {
        final Long id = user.getId();
        getUserById(id);
        userStorage.updateCurrentUser(UserMapper.mapToUser(user));
        UserDto updatedUser = getUserById(id);
        log.info("Обновлены данные пользователя: {}", updatedUser);
        return updatedUser;
    }

    public String addFriend(Long userId, Long friendId) {
        getUserById(userId);
        getUserById(friendId);
        final Long friendshipId = userStorage.getFriendShipId(userId, friendId);
        if (friendshipId == null) {
            userStorage.addFriend(userId, friendId);
        } else {
            final String message = String.format("Пользователь с id=%d уже является другом пользователя с id=%d.",
                    userId, friendId);
            log.warn(message);
            throw new ValidationException(message);
        }
        final String message = String.format("Добавлена дружба пользователя id=%d с пользователем id=%d.",
                userId, friendId);

        eventStorage.save(userId, friendId, EventType.FRIEND, Operation.ADD);

        log.info(message);
        return message;
    }

    public String deleteFriend(Long userId, Long friendId) {
        getUserById(userId);
        getUserById(friendId);
/*
        Тесты "Not friend remove" и "Friend reciprocity" ждут код 200 даже если пользователи не друзья.
        Оставим это пока здесь, на случай если тесты поменяют

        final Long friendshipId = userStorage.getFriendShipId(userId, friendId);
        if (friendshipId == null) {
            final String message = String.format("Пользователь с id=%d не является другом пользователя с id=%d.",
                    userId, friendId);
            log.warn(message);
            throw new NotFoundException(message);
        } else {
            userStorage.deleteFriend(userId, friendId);
        }
*/
        eventStorage.save(userId, friendId, EventType.FRIEND, Operation.REMOVE);

        userStorage.deleteFriend(userId, friendId);
        final String message = String.format("Удалена дружба пользователя id=%d с пользователем id=%d.",
                userId, friendId);
        log.info(message);
        return message;
    }

    public Collection<UserDto> getFriends(Long userId) {
        getUserById(userId);
        return userStorage.getFriends(userId).stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public Collection<UserDto> getCommonFriends(Long id, Long otherId) {
        getUserById(id);
        getUserById(otherId);
        return userStorage.getCommonFriends(id, otherId).stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public String deleteUserById(Long userId) {
        getUserById(userId);
        eventStorage.deleteEvent(userId);
        userStorage.deleteUserById(userId);
        final String message = String.format("Удалён пользователь с id=%d.", userId);
        log.info(message);
        return message;
    }

    public List<Event> getEvents(Long userId) {

        List<Event> events = eventStorage.getEvent(userId);
        if (events.isEmpty()) {
            throw new NotFoundException("Этот пользователь еще не совершил никаких действий");
        }

        return events;
    }
}
