package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@RequestMapping("/genres")
@AllArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public List<Genre> findAll() {
        return genreService.findAll();
    }

    @GetMapping("/{id}")
    public Genre findById(@PathVariable("id") int genreId) {
        return genreService.findById(genreId);
    }

    @PostMapping
    public Genre create(@RequestBody Genre genre) {
        return genreService.create(genre);
    }

    @PutMapping
    public Genre update(@RequestBody Genre genre) {
        return genreService.update(genre);
    }
}