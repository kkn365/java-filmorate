package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final Map<Long, User> users = new HashMap<>();
    private long userIdGenerator = 0;

    @GetMapping
    public Collection<User> findAll() {
        log.info("Пришел GET-запрос /users");

        log.info("Отправлен ответ на GET-запрос /users");

        return users.values();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User newUser) {

        log.info("Пришел POST-запрос /users с телом: {}", newUser);

        final Long id = getNextId();

        User user = User.builder()
                .id(id)
                .email(newUser.getEmail())
                .login(newUser.getLogin())
                .name(newUser.getName() == null ? newUser.getLogin() : newUser.getName())
                .birthday(newUser.getBirthday())
                .build();

        users.put(id, user);

        log.info("Отправлен ответ на POST-запрос /users с телом: {}", user);

        return user;

    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {

        log.info("Пришел PUT-запрос /users с телом: {}", user);

        final Long id = user.getId();

        if (id == null) {
            log.error("Не указан id пользователя.");
            throw new ValidationException("Должен быть указан id пользователя.");
        }

        if (!users.containsKey(id)) {
            log.error("Не найден пользователь с id={}", id);
            throw new NotFoundException("Пользователь с id=" + id + " не найден.");
        }

        User newUser = User.builder()
                .id(id)
                .email(user.getEmail())
                .login(user.getLogin())
                .name(user.getName() == null ? user.getLogin() : user.getName())
                .birthday(user.getBirthday())
                .build();

        users.put(id, newUser);

        log.info("Отправлен ответ на PUT-запрос /users с телом: {}", newUser);

        return newUser;

    }

    private long getNextId() {
        return ++userIdGenerator;
    }

}
