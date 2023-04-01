package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;

import java.sql.Date;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {

    private final UserDbStorage userDbStorage;

    @Test
    @Order(1)
    void addValidUserTest() {
        User user = User.builder()
                .email("email@yandex.ru")
                .login("login")
                .name("name")
                .birthday(Date.valueOf("2000-01-01").toLocalDate())
                .build();

        userDbStorage.create(user);
        assertTrue(userDbStorage.findAll().contains(user));
    }

    @Test
    @Order(2)
    void addUserWithNullEmailTest() {
        User user = User.builder()
                .email(null)
                .login("login")
                .name("name")
                .birthday(Date.valueOf("2000-01-01").toLocalDate())
                .build();

        assertThrows(ValidationException.class, () -> {
            userDbStorage.create(user);
        });
    }

    @Test
    @Order(3)
    void addUserWithEmptyEmailTest() {
        User user = User.builder()
                .email("")
                .login("login")
                .name("name")
                .birthday(Date.valueOf("2000-01-01").toLocalDate())
                .build();

        assertThrows(ValidationException.class, () -> {
            userDbStorage.create(user);
        });
    }

    @Test
    @Order(4)
    void addUserWithBlankEmailTest() {
        User user = User.builder()
                .email(" ")
                .login("login")
                .name("name")
                .birthday(Date.valueOf("2000-01-01").toLocalDate())
                .build();

        assertThrows(ValidationException.class, () -> {
            userDbStorage.create(user);
        });
    }

    @Test
    @Order(5)
    void addUserWithoutAtSignEmailTest() {
        User user = User.builder()
                .email("noAtSign")
                .login("login")
                .name("name")
                .birthday(Date.valueOf("2000-01-01").toLocalDate())
                .build();

        assertThrows(ValidationException.class, () -> {
            userDbStorage.create(user);
        });
    }

    @Test
    @Order(6)
    void addUserWithNullLoginTest() {
        User user = User.builder()
                .email("email@yandex.ru")
                .login(null)
                .name("name")
                .birthday(Date.valueOf("2000-01-01").toLocalDate())
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> {
            userDbStorage.create(user);
        });
    }

    @Test
    @Order(7)
    void addUserWithEmptyLoginTest() {
        User user = User.builder()
                .email("email@yandex.ru")
                .login("")
                .name("name")
                .birthday(Date.valueOf("2000-01-01").toLocalDate())
                .build();

        userDbStorage.create(user);
        assertTrue(userDbStorage.findAll().contains(user));
    }
}