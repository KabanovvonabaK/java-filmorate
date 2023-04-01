package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.db.FilmDbStorage;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {

    private final FilmDbStorage filmDbStorage;

    @Test
    @Order(1)
    void addValidFilm() {
        Film film = Film.builder()
                .name("name")
                .description("desc")
                .duration(55)
                .releaseDate(Date.valueOf("2000-01-01").toLocalDate())
                .mpa(new Mpa(1, "G"))
                .build();

        filmDbStorage.create(film);
        assertTrue(filmDbStorage.findAll().contains(film));
    }

    @Test
    @Order(2)
    void addNullNameFilm() {
        Film film = Film.builder()
                .name(null)
                .description("desc")
                .duration(55)
                .releaseDate(Date.valueOf("2000-01-01").toLocalDate())
                .mpa(new Mpa(1, "G"))
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> {
            filmDbStorage.create(film);
        });
    }

    @Test
    @Order(3)
    void addEmptyNameFilm() {
        Film film = Film.builder()
                .name("")
                .description("desc")
                .duration(55)
                .releaseDate(Date.valueOf("2000-01-01").toLocalDate())
                .mpa(new Mpa(1, "G"))
                .build();

        filmDbStorage.create(film);
        assertTrue(filmDbStorage.findAll().contains(film));
    }

    @Test
    @Order(4)
    void addLongDescriptionFilm() {
        String longDescription = RandomString.make(201);
        Film film = Film.builder()
                .name("name")
                .description(longDescription)
                .duration(55)
                .releaseDate(Date.valueOf("2000-01-01").toLocalDate())
                .mpa(new Mpa(1, "G"))
                .build();

        assertThrows(ValidationException.class, () -> {
            filmDbStorage.create(film);
        });
    }

    @Test
    @Order(5)
    void addOldFilm() {
        Film film = Film.builder()
                .name("oldMovie")
                .description("description")
                .duration(55)
                .releaseDate(Date.valueOf("1895-12-27").toLocalDate())
                .mpa(new Mpa(1, "G"))
                .build();

        assertThrows(ValidationException.class, () -> {
            filmDbStorage.create(film);
        });
    }

    @Test
    @Order(6)
    void addNegativeDurationFilm() {
        Film film = Film.builder()
                .name("name")
                .description("description")
                .duration(0)
                .releaseDate(Date.valueOf("2000-01-01").toLocalDate())
                .mpa(new Mpa(1, "G"))
                .build();

        assertThrows(ValidationException.class, () -> {
            filmDbStorage.create(film);
        });
    }

    @Test
    @Order(7)
    void updateFilm() {
        Film filmForAnUpdate = Film.builder()
                .id(1)
                .name("newName")
                .description("newDescription")
                .duration(2)
                .releaseDate(Date.valueOf("2001-01-01").toLocalDate())
                .mpa(new Mpa(1, "G"))
                .build();

        filmDbStorage.update(filmForAnUpdate);
        assertEquals(filmForAnUpdate,
                filmDbStorage.findById(1));
    }

    @Test
    @Order(8)
    void getFilmList() {
        Film film = Film.builder()
                .name("name")
                .description("desc")
                .duration(55)
                .releaseDate(Date.valueOf("2000-01-01").toLocalDate())
                .mpa(new Mpa(1, "G"))
                .build();

        filmDbStorage.create(film);
        assertEquals(3, filmDbStorage.findAll().size());
    }
}