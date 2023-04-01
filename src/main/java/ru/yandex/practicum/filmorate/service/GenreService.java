package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class GenreService {

    private final Storage<Genre> genreStorage;

    public List<Genre> findAll() {
        return genreStorage.findAll();
    }

    public Genre findById(int genreId) {
        return genreStorage.findById(genreId);
    }

    public Genre create(Genre genre) {
        return genreStorage.create(genre);
    }

    public Genre update(Genre genre) {
        return genreStorage.update(genre);
    }

}