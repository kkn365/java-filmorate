package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<User> getAll() {
        log.info("Получен запрос 'GET /users'");
        Collection<User> allUsers = userService.getAllUsers();
        log.info("Отправлен ответ на запрос 'GET /users' с телом: {}", allUsers);
        return allUsers;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User newUser) {
        log.info("Получен запрос 'POST /users' с телом: {}", newUser);
        User user = userService.addNewUser(newUser);
        log.info("Отправлен ответ на запрос 'POST /users' с телом: {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Получен запрос 'PUT /users' с телом: {}", user);
        User updatedUser = userService.updateUserData(user);
        log.info("Отправлен ответ на запрос 'PUT /users' с телом: {}", updatedUser);
        return updatedUser;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Получен запрос 'PUT /users/{}/friends/{}'", id, friendId);
        User user = userService.addFriend(id, friendId);
        log.info("Отправлен ответ на запрос 'PUT /users/{}/friends/{}' с телом: {}", id, friendId, user);
        return user;
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Получен запрос 'DELETE /users/{}/friends/{}'", id, friendId);
        User user = userService.deleteFriend(id, friendId);
        log.info("Отправлен ответ на запрос 'DELETE /users/{}/friends/{}' с телом: {}", id, friendId, user);
        return user;
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable Long id) {
        log.info("Получен запрос 'GET /users/{}/friends'", id);
        Collection<User> friendsList = userService.getFriends(id);
        log.info("Отправлен ответ на запрос 'GET /users/{}/friends' с телом: {}", id, friendsList);
        return friendsList;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Получен запрос 'GET /users/{}/friends/common/{}'", id, otherId);
        Collection<User> commonFriendsList = userService.getCommonFriends(id, otherId);
        log.info("отправлен ответ на запрос 'GET /users/{}/friends/common/{}' с телом: {}",
                id, otherId, commonFriendsList);
        return commonFriendsList;
    }

}
