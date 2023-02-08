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

    @GetMapping
    public List<Film> getFilmList() {
        return films;
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        if (filmValidation(film)) {
            film.setId(filmId);
            filmId++;
            formatReleaseDate(film);
            films.add(film);
            log.info("FILM SUCCESSFULLY ADDED: {}", film.toString());
        }
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (films.contains(film)) {
            films.remove(film);
            formatReleaseDate(film);
            films.add(film);
            log.info("FILM SUCCESSFULLY UPDATED: " + film.toString());
        } else {
            throw new ValidationException("Film with such id " + film.getId() + " don't exist");
        }
        return film;
    }

    private boolean filmValidation(Film film) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Date notOlder = Date.valueOf(LocalDate.parse("1895-12-28").format(dateTimeFormatter));
        if (film.getDescription().length() > 200) {
            log.error("Film description length should be less than 200 symbols");
            throw new ValidationException("Film description length should be less than 200 symbols");
        } else if (film.getReleaseDate().before(notOlder)) {
            log.error("Film can not be earlier than 28.12.1895: {}", film.toString());
            throw new ValidationException("Film can not be earlier than 28.12.1895");
        } else {
            return true;
        }
    }

    private void setFilmId(Film film) {
        film.setId(filmId);
        filmId++;
    }

    private void formatReleaseDate(Film film) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        film.setReleaseDate((Date.valueOf(sdf.format(film.getReleaseDate()))));
    }
}