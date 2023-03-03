package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SuccessResponse;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    final FilmStorage filmStorage;
    final FilmService filmService;
    private final int defaultCountForTopFilms = 10;

    @Autowired
    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getFilmList() {
        return filmStorage.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {
        return filmStorage.getFilmById(id);
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmStorage.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmStorage.updateFilm(film);
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
            return filmService.getTopFilms(defaultCountForTopFilms);
        } else {
            return filmService.getTopFilms(count);
        }
    }
}