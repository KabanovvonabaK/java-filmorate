package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final List<User> users = new ArrayList<>();
    private int userId = 1;
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public User addUser(User user) {
        if (user.getLogin().contains(" ")) {
            log.error("User login can not be empty, null or contain space symbol(s): {}", user);
            throw new ValidationException("User login can not be empty, null or contain space symbol(s)");
        }
        if (!StringUtils.hasText(user.getName())) {
            user.setName(user.getLogin());
        }

        setUserId(user);
        formatBirthday(user);

        users.add(user);
        log.info("USER SUCCESSFULLY ADDED: " + user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.contains(user)) {
            throw new ValidationException("User with such id " + user.getId() + " don't exist");
        }
        users.remove(user);
        formatBirthday(user);
        users.add(user);
        log.info("USER SUCCESSFULLY UPDATED: " + user);
        return user;
    }

    @Override
    public User findUser(int id) {
        if (checkUserExistById(id)) {
            log.info(String.format("User with id %s was requested", id));
            return users.stream().filter(x -> x.getId() == id).findFirst().get();
        } else {
            log.error(String.format("User with id %s not found.", id));
            throw new UserNotFoundException(id);
        }
    }

    @Override
    public List<User> getAllUsers() {
        return users;
    }


    private void setUserId(User user) {
        user.setId(userId);
        userId++;
    }

    private void formatBirthday(User user) {
        user.setBirthday((Date.valueOf(SIMPLE_DATE_FORMAT.format(user.getBirthday()))));
    }

    private boolean checkUserExistById(int id) {
        for (User user : users) {
            if (user.getId() == id) {
                return true;
            }
        }
        return false;
    }
}