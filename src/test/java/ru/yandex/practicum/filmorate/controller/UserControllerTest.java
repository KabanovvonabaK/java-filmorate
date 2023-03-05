package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Order(1)
    void addValidUserTest() {
        User user = User.builder()
                .email("email@yandex.ru")
                .login("login")
                .name("name")
                .birthday(Date.valueOf("2000-01-01"))
                .build();

        restTemplate.postForEntity(getUrl(), user, User.class);
        assertEquals(1, restTemplate.getForObject(getUrl(),
                List.class).size());
    }

    @Test
    @Order(2)
    void addUserWithNullEmailTest() {
        User user = User.builder()
                .email(null)
                .login("login")
                .name("name")
                .birthday(Date.valueOf("2000-01-01"))
                .build();

        restTemplate.postForEntity(getUrl(), user, User.class);
        assertEquals(1, restTemplate.getForObject(getUrl(),
                List.class).size());
    }

    @Test
    @Order(3)
    void addUserWithEmptyEmailTest() {
        User user = User.builder()
                .email("")
                .login("login")
                .name("name")
                .birthday(Date.valueOf("2000-01-01"))
                .build();

        restTemplate.postForEntity(getUrl(), user, User.class);
        assertEquals(1, restTemplate.getForObject(getUrl(),
                List.class).size());
    }

    @Test
    @Order(4)
    void addUserWithBlankEmailTest() {
        User user = User.builder()
                .email(" ")
                .login("login")
                .name("name")
                .birthday(Date.valueOf("2000-01-01"))
                .build();

        restTemplate.postForEntity(getUrl(), user, User.class);
        assertEquals(1, restTemplate.getForObject(getUrl(),
                List.class).size());
    }

    @Test
    @Order(5)
    void addUserWithoutAtSignEmailTest() {
        User user = User.builder()
                .email("noAtSign")
                .login("login")
                .name("name")
                .birthday(Date.valueOf("2000-01-01"))
                .build();

        restTemplate.postForEntity(getUrl(), user, User.class);
        assertEquals(1, restTemplate.getForObject(getUrl(),
                List.class).size());
    }

    @Test
    @Order(6)
    void addUserWithNullLoginTest() {
        User user = User.builder()
                .email("email@yandex.ru")
                .login(null)
                .name("name")
                .birthday(Date.valueOf("2000-01-01"))
                .build();

        restTemplate.postForEntity(getUrl(), user, User.class);
        assertEquals(1, restTemplate.getForObject(getUrl(),
                List.class).size());
    }

    @Test
    @Order(7)
    void addUserWithEmptyLoginTest() {
        User user = User.builder()
                .email("email@yandex.ru")
                .login("")
                .name("name")
                .birthday(Date.valueOf("2000-01-01"))
                .build();

        restTemplate.postForEntity(getUrl(), user, User.class);
        assertEquals(1, restTemplate.getForObject(getUrl(),
                List.class).size());
    }

    @Test
    @Order(8)
    void addUserWithSpacesInLoginTest() {
        User user = User.builder()
                .email("email@yandex.ru")
                .login(" ")
                .name("name")
                .birthday(Date.valueOf("2000-01-01"))
                .build();

        restTemplate.postForEntity(getUrl(), user, User.class);
        assertEquals(1, restTemplate.getForObject(getUrl(),
                List.class).size());
    }

    @Test
    @Order(9)
    void addUserWithNullNameTest() {
        User user = User.builder()
                .email("email@yandex.ru")
                .login("login")
                .name(null)
                .birthday(Date.valueOf("2000-01-01"))
                .build();

        restTemplate.postForEntity(getUrl(), user, User.class);
        assertEquals(2, restTemplate.getForObject(getUrl(),
                List.class).size());
    }

    @Test
    @Order(10)
    void addUserWithEmptyNameTest() {
        User user = User.builder()
                .email("email@yandex.ru")
                .login("login")
                .name("")
                .birthday(Date.valueOf("2000-01-01"))
                .build();

        restTemplate.postForEntity(getUrl(), user, User.class);
        assertEquals(3, restTemplate.getForObject(getUrl(),
                List.class).size());
    }

    @Test
    @Order(11)
    void addUserWithBirthDayInTheFutureNameTest() {
        User user = User.builder()
                .email("email@yandex.ru")
                .login("login")
                .name("name")
                .birthday(Date.valueOf("3000-01-01"))
                .build();

        restTemplate.postForEntity(getUrl(), user, User.class);
        assertEquals(3, restTemplate.getForObject(getUrl(),
                List.class).size());
    }

    @Test
    @Order(12)
    void updateUserTest() {
        User userForAnUpdate = User.builder()
                .id(1)
                .email("newEmail@yandex.ru")
                .login("newLogin")
                .name("newName")
                .birthday(Date.valueOf("2001-01-01"))
                .build();

        restTemplate.put(getUrl(), userForAnUpdate, User.class);
        System.out.println(restTemplate.getForObject(getUrl(), List.class));
        assertEquals("{id=1, email=newEmail@yandex.ru, login=newLogin, name=newName, birthday=2001-01-01, " +
                        "friends=[], likedFilms=[]}",
                restTemplate.getForObject(getUrl(),
                        List.class).get(2).toString());
    }

    @Test
    @Order(13)
    void updateUserUnknownTest() {
        User userForAnUpdate = User.builder()
                .id(2)
                .email("newEmail@yandex.ru")
                .login("newLogin")
                .name("newName")
                .birthday(Date.valueOf("2001-01-01"))
                .build();

        restTemplate.postForEntity(getUrl(), userForAnUpdate, User.class);
        assertEquals(4, restTemplate.getForObject(getUrl(),
                List.class).size());
    }

    @Test
    @Order(14)
    void getUsersTest() {
        User user = User.builder()
                .email("email@yandex.ru")
                .login("login")
                .name("name")
                .birthday(Date.valueOf("3000-01-01"))
                .build();

        restTemplate.postForEntity(getUrl(), user, User.class);
        assertEquals(4, restTemplate.getForObject(getUrl(),
                List.class).size());
    }

    private String getUrl() {
        return "http://localhost:" + port + "/users";
    }
}