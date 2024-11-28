package ru.yandex.practicum.filmorate.storage.user.UserStorage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

@Component
public interface UserStorage {

    User put(User user);

    User update(User user);

    User get(Long id);

    Collection<User> getAll();

}
