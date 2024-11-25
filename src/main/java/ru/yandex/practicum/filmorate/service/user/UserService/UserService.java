package ru.yandex.practicum.filmorate.service.user.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private UserStorage userStorage;

    public Collection<User> getAllUsers() {
        return userStorage.getAll();
    }

    public User addNewUser(User user) {
        return userStorage.put(user);
    }

    public User updateUserData(User user) {
        final Long id = user.getId();

        if (id == null) {
            throw new ValidationException("Должен быть указан id пользователя.");
        }

        if (userStorage.get(id) == null) {
            throw new NotFoundException(String.format("Пользователь с id=%d не найден.", id));
        }

        return userStorage.update(user);
    }

    public User addFriend(Long userId, Long friendId) {
        final User user = userStorage.get(userId);
        if (user == null) {
            throw new NotFoundException(String.format("Пользователь с id=%d не найден.", userId));
        }

        final User friend = userStorage.get(friendId);
        if (friend == null) {
            throw new NotFoundException(String.format("Пользователь с id=%d не найден.", friendId));
        }

        Set<Long> userFriends = user.getFriends();
        if (userFriends == null) {
            userFriends = new HashSet<>();
        }
        userFriends.add(friendId);
        user.setFriends(userFriends);
        userStorage.update(user);

        Set<Long> friendFriends = friend.getFriends();
        if (friendFriends == null) {
            friendFriends = new HashSet<>();
        }
        friendFriends.add(userId);
        friend.setFriends(friendFriends);
        userStorage.update(friend);

        return user;
    }

    public User deleteFriend(Long userId, Long friendId) {
        final User user = userStorage.get(userId);
        if (user == null) {
            throw new NotFoundException(String.format("Пользователь с id=%d не найден.", userId));
        }

        final User friend = userStorage.get(friendId);
        if (friend == null) {
            throw new NotFoundException(String.format("Пользователь с id=%d не найден.", friendId));
        }

        Set<Long> userFriends = user.getFriends();
        if (userFriends != null) {
            userFriends.remove(friendId);
            user.setFriends(userFriends);
            userStorage.update(user);
        }

        Set<Long> friendFriends = friend.getFriends();
        if (friendFriends != null) {
            friendFriends.remove(userId);
            friend.setFriends(friendFriends);
            userStorage.update(friend);
        }

        return user;
    }

    public Set<Long> getFriends(Long userId) {
        final User user = userStorage.get(userId);
        if (user == null) {
            throw new NotFoundException(String.format("Пользователь с id=%d не найден.", userId));
        }
        return user.getFriends();
    }

    public Set<Long> getCommonFriends(Long id, Long otherId) {
        final User user = userStorage.get(id);
        if (user == null) {
            throw new NotFoundException(String.format("Пользователь с id=%d не найден.", id));
        }

        final User otherUser = userStorage.get(otherId);
        if (otherUser == null) {
            throw new NotFoundException(String.format("Пользователь с id=%d не найден.", otherId));
        }

        return user.getFriends().stream()
                .filter(otherUser.getFriends()::contains)
                .collect(Collectors.toSet());
    }

}
