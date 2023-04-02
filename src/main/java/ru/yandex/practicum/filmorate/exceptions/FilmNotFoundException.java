package ru.yandex.practicum.filmorate.exceptions;

public class FilmNotFoundException extends RuntimeException {
    int filmId;

    public FilmNotFoundException(int filmId) {
        this.filmId = filmId;
    }

    public int getFilmId() {
        return filmId;
    }
}