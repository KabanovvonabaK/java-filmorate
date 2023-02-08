package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
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

    @GetMapping
    public List<User> getUsers() {
        return users;
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        if (userValidation(user)) {
            if (user.getName() == null || user.getName().isEmpty()) {
                user.setName(user.getLogin());
            }

            setUserId(user);
            formatBirthday(user);

            users.add(user);
            log.info("USER SUCCESSFULLY ADDED: " + user.toString());
        }
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (users.contains(user)) {
            users.remove(user);
            formatBirthday(user);
            users.add(user);
            log.info("USER SUCCESSFULLY UPDATED: " + user.toString());
        } else {
            throw new ValidationException("User with such id " + user.getId() + " don't exist");
        }
        return user;
    }

    private boolean userValidation(User user) {
        if (user.getLogin().contains(" ")) {
            log.error("User login can not be empty, null or contain space symbol(s): {}", user.toString());
            throw new ValidationException("User login can not be empty, null or contain space symbol(s)");
        } else {
            return true;
        }
    }

    private void setUserId(User user) {
        user.setId(userId);
        userId++;
    }

    private void formatBirthday(User user) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        user.setBirthday((Date.valueOf(sdf.format(user.getBirthday()))));
    }
}