package ru.yandex.practicum.filmorate.controller;

import net.bytebuddy.utility.RandomString;
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
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FilmControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Order(1)
    void addValidFilm() {
        Film film = Film.builder()
                .name("name")
                .description("desc")
                .duration(55)
                .releaseDate(Date.valueOf("2000-01-01"))
                .build();

        restTemplate.postForEntity(getUrl(), film, Film.class);
        assertEquals(1, restTemplate.getForObject(getUrl(),
                List.class).size());
    }

    @Test
    @Order(2)
    void addNullNameFilm() {
        Film film = Film.builder()
                .name(null)
                .description("desc")
                .duration(55)
                .releaseDate(Date.valueOf("2000-01-01"))
                .build();

        restTemplate.postForEntity(getUrl(), film, Film.class);
        assertEquals(1, restTemplate.getForObject(getUrl(),
                List.class).size());
    }

    @Test
    @Order(3)
    void addEmptyNameFilm() {
        Film film = Film.builder()
                .name("")
                .description("desc")
                .duration(55)
                .releaseDate(Date.valueOf("2000-01-01"))
                .build();

        restTemplate.postForEntity(getUrl(), film, Film.class);
        assertEquals(1, restTemplate.getForObject(getUrl(),
                List.class).size());
    }

    @Test
    @Order(4)
    void addLongDescriptionFilm() {
        String longDescription = RandomString.make(201);
        Film film = Film.builder()
                .name("name")
                .description(longDescription)
                .duration(55)
                .releaseDate(Date.valueOf("2000-01-01"))
                .build();

        restTemplate.postForEntity(getUrl(), film, Film.class);
        assertEquals(1, restTemplate.getForObject(getUrl(),
                List.class).size());
    }

    @Test
    @Order(5)
    void addOldFilm() {
        Film film = Film.builder()
                .name("oldMovie")
                .description("description")
                .duration(55)
                .releaseDate(Date.valueOf("1895-12-27"))
                .build();

        restTemplate.postForEntity(getUrl(), film, Film.class);
        assertEquals(1, restTemplate.getForObject(getUrl(),
                List.class).size());
    }

    @Test
    @Order(6)
    void addNegativeDurationFilm() {
        Film film = Film.builder()
                .name("name")
                .description("description")
                .duration(0)
                .releaseDate(Date.valueOf("2000-01-01"))
                .build();

        restTemplate.postForEntity(getUrl(), film, Film.class);
        assertEquals(1, restTemplate.getForObject(getUrl(),
                List.class).size());
    }

    @Test
    @Order(7)
    void updateFilm() {
        Film filmForAnUpdate = Film.builder()
                .id(1)
                .name("newName")
                .description("newDescription")
                .duration(2)
                .releaseDate(Date.valueOf("2001-01-01"))
                .build();

        restTemplate.put(getUrl(), filmForAnUpdate, Film.class);
        assertEquals("{id=1, name=newName, description=newDescription, " +
                        "releaseDate=2001-01-01, duration=2.0, rate=0}",
                restTemplate.getForObject(getUrl(),
                        List.class).get(0).toString());
    }

    @Test
    @Order(8)
    void getFilmList() {
        Film film = Film.builder()
                .name("name")
                .description("desc")
                .duration(55)
                .releaseDate(Date.valueOf("2000-01-01"))
                .build();

        restTemplate.postForEntity(getUrl(), film, Film.class);
        assertEquals(2, restTemplate.getForObject(getUrl(),
                List.class).size());
    }

    private String getUrl() {
        return "http://localhost:" + port + "/films";
    }
}