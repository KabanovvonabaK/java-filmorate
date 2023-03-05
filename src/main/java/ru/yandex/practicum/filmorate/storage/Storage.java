package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface Storage<T> {
    T create(T entity);
    T update(T entity);
    T findById(int id);
    List<T> findAll();
}
