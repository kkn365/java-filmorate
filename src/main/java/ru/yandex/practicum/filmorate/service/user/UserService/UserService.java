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
        userFriends.add(friendId);
        user.setFriends(userFriends);
        userStorage.update(user);

        Set<Long> friendFriends = friend.getFriends();
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
        userFriends.remove(friendId);
        user.setFriends(userFriends);
        userStorage.update(user);

        Set<Long> friendFriends = friend.getFriends();
        friendFriends.remove(userId);
        friend.setFriends(friendFriends);
        userStorage.update(friend);

        return user;
    }

    public Collection<User> getFriends(Long userId) {
        final User user = userStorage.get(userId);
        final Collection<User> tmpUserList = new HashSet<>();

        if (user == null) {
            throw new NotFoundException(String.format("Пользователь с id=%d не найден.", userId));
        }

        for (Long id : user.getFriends()) {
            tmpUserList.add(userStorage.get(id));
        }

        return tmpUserList;
    }

    public Collection<User> getCommonFriends(Long id, Long otherId) {
        final User user = userStorage.get(id);
        final User otherUser = userStorage.get(otherId);
        final Collection<User> tmpUserList = new HashSet<>();

        if (user == null) {
            throw new NotFoundException(String.format("Пользователь с id=%d не найден.", id));
        }

        if (otherUser == null) {
            throw new NotFoundException(String.format("Пользователь с id=%d не найден.", otherId));
        }

        final Set<Long> commonFriendsIdsList = user.getFriends().stream()
                .filter(otherUser.getFriends()::contains)
                .collect(Collectors.toSet());

        for (Long userId : commonFriendsIdsList) {
            tmpUserList.add(userStorage.get(userId));
        }

        return tmpUserList;
    }

}
