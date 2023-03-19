package ru.yandex.practicum.filmorate.exceptions;

public class UserAlreadyLikedFilm extends RuntimeException {
    private final int filmId;
    private final int userId;

    public UserAlreadyLikedFilm(int filmId, int userId) {
        this.filmId = filmId;
        this.userId = userId;
    }

    public int getFilmId() {
        return filmId;
    }

    public int getUserId() {
        return userId;
    }
}
