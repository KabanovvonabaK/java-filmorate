package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class MpaService {

    private final Storage<Mpa> mpaStorage;


    public List<Mpa> findAll() {
        return mpaStorage.findAll();
    }

    public Mpa findById(int mpaId) {
        return mpaStorage.findById(mpaId);
    }

    public Mpa create(Mpa mpa) {
        return mpaStorage.create(mpa);
    }

    public Mpa update(Mpa mpa) {
        return mpaStorage.update(mpa);
    }
}