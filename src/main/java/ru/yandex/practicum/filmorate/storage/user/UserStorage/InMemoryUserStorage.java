package ru.yandex.practicum.filmorate.storage.user.UserStorage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long userIdGenerator = 0L;

    private Long getNextId() {
        return ++userIdGenerator;
    }

    @Override
    public User put(User user) {
        final Long id = getNextId();

        User newUser = User.builder()
                .id(id)
                .email(user.getEmail())
                .login(user.getLogin())
                .name(user.getName() == null ? user.getLogin() : user.getName())
                .birthday(user.getBirthday())
                .friends(user.getFriends() == null ? user.getFriends() : new HashSet<>())
                .build();

        users.put(id, newUser);

        return newUser;
    }

    @Override
    public User update(User user) {

        User newUser = User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .login(user.getLogin())
                .name(user.getName() == null ? user.getLogin() : user.getName())
                .birthday(user.getBirthday())
                .friends(user.getFriends() == null ? user.getFriends() : new HashSet<>())
                .build();

        users.put(user.getId(), newUser);

        return newUser;
    }

    @Override
    public User get(Long id) {
        return users.get(id);
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

}
