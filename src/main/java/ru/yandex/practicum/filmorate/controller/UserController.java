package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.info("The list of users has been sent.");
        return users.values();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User newUser) {

        final Long id = getNextId();

        User user = User.builder()
                .id(id)
                .email(newUser.getEmail())
                .login(newUser.getLogin())
                .name(newUser.getName() == null ? newUser.getLogin() : newUser.getName())
                .birthday(newUser.getBirthday())
                .build();

        users.put(id, user);

        log.info("User has been added to the list: {}", user);

        return user;

    }

    @PutMapping
    public ResponseEntity<?> updateUser(@Valid @RequestBody User user) {

        try {
            return new ResponseEntity<>(update(user), HttpStatus.OK);
        } catch (NotFoundException e) {
            log.error("Not found error: {}", e.getMessage());
            return new ResponseEntity<>(new ErrorMessage(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (ValidationException e) {
            log.error("Validation error: {}", e.getMessage());
            return new ResponseEntity<>(new ErrorMessage(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private User update(User user) throws ValidationException, NotFoundException {

        final Long id = user.getId();

        if (id == null) {
            throw new ValidationException("Id must be set");
        }

        User oldUser = users.get(id);

        if (oldUser == null) {
            throw new NotFoundException("User with id = " + id + " not found");
        }

        final String oldEmail = oldUser.getEmail();
        final String newEmail = user.getEmail();
        if (!oldEmail.equals(newEmail)) {
            oldUser.setEmail(newEmail);
            log.info("User id={} field 'email' updated. Old value: [{}]. New value: [{}]", id, oldEmail, newEmail);
        }

        final String oldLogin = oldUser.getLogin();
        final String newLogin = user.getLogin();
        if (!oldLogin.equals(newLogin)) {
            oldUser.setLogin(newLogin);
            log.info("User id={} field 'login' updated. Old value: [{}]. New value: [{}]", id, oldLogin, newLogin);
        }

        final String oldName = oldUser.getName();
        final String newName = user.getName() == null ? user.getLogin() : user.getName();
        if (!oldName.equals(newName)) {
            oldUser.setName(newName);
            log.info("User id={} field 'name' updated. Old value: [{}]. New value: [{}]", id, oldName, newName);
        }

        final LocalDate oldBirthday = oldUser.getBirthday();
        final LocalDate newBirthday = user.getBirthday();
        if (!oldBirthday.equals(newBirthday)) {
            oldUser.setBirthday(newBirthday);
            log.info("User id={} field 'birthday' updated. Old value: [{}]. New value: [{}]",
                    id, oldBirthday, newBirthday);
        }

        return oldUser;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
