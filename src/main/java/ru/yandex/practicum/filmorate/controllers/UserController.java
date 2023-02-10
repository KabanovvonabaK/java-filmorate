package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private List<User> users = new ArrayList<>();
    private int userId = 1;

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @GetMapping
    public List<User> getUsers() {
        return users;
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        if (user.getLogin().contains(" ")) {
            log.error("User login can not be empty, null or contain space symbol(s): {}", user.toString());
            throw new ValidationException("User login can not be empty, null or contain space symbol(s)");
        }
        if (!StringUtils.hasText(user.getName())) {
            user.setName(user.getLogin());
        }

        setUserId(user);
        formatBirthday(user);

        users.add(user);
        log.info("USER SUCCESSFULLY ADDED: " + user.toString());
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (!users.contains(user)) {
            throw new ValidationException("User with such id " + user.getId() + " don't exist");
        }
        users.remove(user);
        formatBirthday(user);
        users.add(user);
        log.info("USER SUCCESSFULLY UPDATED: " + user.toString());
        return user;
    }

    private void setUserId(User user) {
        user.setId(userId);
        userId++;
    }

    private void formatBirthday(User user) {
        user.setBirthday((Date.valueOf(SIMPLE_DATE_FORMAT.format(user.getBirthday()))));
    }
}