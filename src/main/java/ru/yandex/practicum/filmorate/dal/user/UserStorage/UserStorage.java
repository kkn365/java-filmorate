package ru.yandex.practicum.filmorate.dal.user.UserStorage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

@Component
public interface UserStorage {
    User addNewUser(User user);

    User updateCurrentUser(User user);

    User getUserById(Long id);

    Collection<User> getAllUsers();

    Collection<User> getFriends(Long id);

    void addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    Collection<User> getCommonFriends(Long userId, Long friendId);

    Long getFriendShipId(Long userId, Long friendId);

    void deleteUserById(Long userId);
}
