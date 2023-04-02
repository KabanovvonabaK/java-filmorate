package ru.yandex.practicum.filmorate.exceptions;

public class GenreNotFoundException extends RuntimeException {
    private final int genreId;

    public GenreNotFoundException(int genreId) {
        this.genreId = genreId;
    }

    public int getGenreId() {
        return genreId;
    }
}