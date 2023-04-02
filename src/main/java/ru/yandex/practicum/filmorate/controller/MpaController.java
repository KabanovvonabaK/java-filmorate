package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@AllArgsConstructor
public class MpaController {

    private final MpaService mpaService;

    @GetMapping
    public List<Mpa> findAll() {
        return mpaService.findAll();
    }

    @GetMapping("/{id}")
    public Mpa findById(@PathVariable("id") int mpaId) {
        return mpaService.findById(mpaId);
    }

    @PostMapping
    public Mpa create(@RequestBody Mpa mpa) {
        return mpaService.create(mpa);
    }

    @PutMapping
    public Mpa update(@RequestBody Mpa mpa) {
        return mpaService.update(mpa);
    }
}