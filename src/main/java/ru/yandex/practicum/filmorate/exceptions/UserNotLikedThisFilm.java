package ru.yandex.practicum.filmorate.exceptions;

public class UserNotLikedThisFilm extends RuntimeException {
    private final int userId;
    private final int filmId;

    public UserNotLikedThisFilm(int userId, int filmId) {
        this.userId = userId;
        this.filmId = filmId;
    }

    public int getUserId() {
        return userId;
    }

    public int getFilmId() {
        return filmId;
    }
}