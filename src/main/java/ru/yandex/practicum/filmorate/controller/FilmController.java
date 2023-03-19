package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SuccessResponse;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {

    final FilmService filmService;
    private static final int DEFAULT_COUNT_FOR_TOP_FILMS = 10;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getFilmList() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {
        return filmService.getFilmById(id);
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public SuccessResponse addLike(@PathVariable int id, @PathVariable int userId) {
        filmService.addLike(id, userId);
        return new SuccessResponse(String.format("User with id %s like film with id %s", userId, id));
    }

    @DeleteMapping("/{id}/like/{userId}")
    public SuccessResponse deleteLike(@PathVariable int id, @PathVariable int userId) {
        filmService.removeLike(id, userId);
        return new SuccessResponse(String.format("User with id %s remove like from film with id %s", userId, id));
    }

    @GetMapping("/popular")
    public List<Film> getTopFilms(@RequestParam(required = false) Integer count) {
        if (count == null || count < 0) {
            return filmService.getTopFilms(DEFAULT_COUNT_FOR_TOP_FILMS);
        } else {
            return filmService.getTopFilms(count);
        }
    }
}