package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private List<Film> films = new ArrayList<>();
    private int filmId = 1;

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Date NOT_OLDER = Date.valueOf(LocalDate.parse("1895-12-28").format(DATE_TIME_FORMATTER));

    private static final int MAX_DESCRIPTION_LENGTH = 200;

    @GetMapping
    public List<Film> getFilmList() {
        return films;
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        if (film.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            log.error("Film description length should be less than " + MAX_DESCRIPTION_LENGTH + " symbols");
            throw new ValidationException("Film description length should be less than " +
                    MAX_DESCRIPTION_LENGTH + " symbols");
        } else if (film.getReleaseDate().before(NOT_OLDER)) {
            log.error("Film can not be earlier than 28.12.1895: {}", film.toString());
            throw new ValidationException("Film can not be earlier than 28.12.1895");
        }
        setFilmId(film);
        formatReleaseDate(film);
        films.add(film);
        log.info("FILM SUCCESSFULLY ADDED: {}", film.toString());

        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (!films.contains(film)) {
            throw new ValidationException("Film with such id " + film.getId() + " don't exist");
        }
        films.remove(film);
        formatReleaseDate(film);
        films.add(film);
        log.info("FILM SUCCESSFULLY UPDATED: " + film.toString());

        return film;
    }

    private void setFilmId(Film film) {
        film.setId(filmId);
        filmId++;
    }

    private void formatReleaseDate(Film film) {
        film.setReleaseDate((Date.valueOf(SIMPLE_DATE_FORMAT.format(film.getReleaseDate()))));
    }
}