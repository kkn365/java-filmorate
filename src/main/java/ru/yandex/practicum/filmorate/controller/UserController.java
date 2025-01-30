package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.ResponseMessage;
import ru.yandex.practicum.filmorate.service.RecommendationService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RecommendationService recommendationService;

    @GetMapping
    public Collection<UserDto> getAll() {
        return userService.getAllUsers();
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto newUser) {
        return userService.addNewUser(newUser);
    }

    @PutMapping
    public UserDto updateUser(@Valid @RequestBody UserDto user) {
        return userService.updateUserData(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseMessage addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        return new ResponseMessage(userService.addFriend(id, friendId));
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseMessage deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        return new ResponseMessage(userService.deleteFriend(id, friendId));
    }

    @GetMapping("/{id}/friends")
    public Collection<UserDto> getFriends(@PathVariable Long id) {
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<UserDto> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping("/{id}/recommendations")
    public Collection<FilmDto> getUserRecommendations(@PathVariable Long id) {
        return recommendationService.getRecommendedFilmsList(id);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseMessage deleteUser(@PathVariable Long userId) {
        return new ResponseMessage(userService.deleteUserById(userId));
    }

}
