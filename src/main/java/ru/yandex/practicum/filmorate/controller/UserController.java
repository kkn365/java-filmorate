package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<UserDto> getAll() {
        log.info("Получен запрос 'GET /users'");
        Collection<UserDto> allUsers = userService.getAllUsers().stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
        log.info("Отправлен ответ на запрос 'GET /users' с телом: {}", allUsers);
        return allUsers;
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto newUser) {
        log.info("Получен запрос 'POST /users' с телом: {}", newUser);
        UserDto user = UserMapper.mapToUserDto(userService.addNewUser(UserMapper.mapToUser(newUser)));
        log.info("Отправлен ответ на запрос 'POST /users' с телом: {}", user);
        return user;
    }

    @PutMapping
    public UserDto updateUser(@Valid @RequestBody UserDto user) {
        log.info("Получен запрос 'PUT /users' с телом: {}", user);
        UserDto updatedUser = UserMapper.mapToUserDto(userService.updateUserData(UserMapper.mapToUser(user)));
        log.info("Отправлен ответ на запрос 'PUT /users' с телом: {}", updatedUser);
        return updatedUser;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public Collection<UserDto> addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Получен запрос 'PUT /users/{}/friends/{}'", id, friendId);
        Collection<UserDto> user = userService.addFriend(id, friendId).stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
        log.info("Отправлен ответ на запрос 'PUT /users/{}/friends/{}' с телом: {}", id, friendId, user);
        return user;
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public Collection<UserDto> deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Получен запрос 'DELETE /users/{}/friends/{}'", id, friendId);
        Collection<UserDto> users = userService.deleteFriend(id, friendId).stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
        log.info("Отправлен ответ на запрос 'DELETE /users/{}/friends/{}' с телом: {}", id, friendId, users);
        return users;
    }

    @GetMapping("/{id}/friends")
    public Collection<UserDto> getFriends(@PathVariable Long id) {
        log.info("Получен запрос 'GET /users/{}/friends'", id);
        Collection<UserDto> friendsList = userService.getFriends(id).stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
        log.info("Отправлен ответ на запрос 'GET /users/{}/friends' с телом: {}", id, friendsList);
        return friendsList;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<UserDto> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Получен запрос 'GET /users/{}/friends/common/{}'", id, otherId);
        Collection<UserDto> commonFriendsList = userService.getCommonFriends(id, otherId).stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
        log.info("Отправлен ответ на запрос 'GET /users/{}/friends/common/{}' с телом: {}",
                id, otherId, commonFriendsList);
        return commonFriendsList;
    }

}
