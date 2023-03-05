package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class InMemoryFilmStorage implements Storage<Film> {

    private final List<Film> films = new ArrayList<>();
    private int filmId = 1;
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Date NOT_OLDER = Date.valueOf(LocalDate.parse("1895-12-28").format(DATE_TIME_FORMATTER));
    private static final int MAX_DESCRIPTION_LENGTH = 200;


    @Override
    public Film create(Film film) {
        if (film.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            log.error("Film description length should be less than {} symbols", MAX_DESCRIPTION_LENGTH);
            throw new ValidationException("Film description length should be less than " +
                    MAX_DESCRIPTION_LENGTH + " symbols");
        } else if (film.getReleaseDate().before(NOT_OLDER)) {
            log.error("Film can not be earlier than 28.12.1895: {}", film);
            throw new ValidationException("Film can not be earlier than 28.12.1895");
        }
        setFilmId(film);
        formatReleaseDate(film);
        films.add(film);
        log.info("FILM SUCCESSFULLY ADDED: {}", film);

        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.contains(film)) {
            throw new ValidationException("Film with such id " + film.getId() + " don't exist");
        }
        films.remove(film);
        formatReleaseDate(film);
        films.add(film);
        log.info("FILM SUCCESSFULLY UPDATED: " + film);

        return film;
    }

    @Override
    public Film findById(int id) {
        for (Film film : films) {
            if (film.getId() == id) {
                return film;
            }
        }
        throw new FilmNotFoundException(id);
    }

    @Override
    public List<Film> findAll() {
        return films;
    }

    private void setFilmId(Film film) {
        film.setId(filmId);
        filmId++;
    }

    private void formatReleaseDate(Film film) {
        film.setReleaseDate((Date.valueOf(SIMPLE_DATE_FORMAT.format(film.getReleaseDate()))));
    }
}