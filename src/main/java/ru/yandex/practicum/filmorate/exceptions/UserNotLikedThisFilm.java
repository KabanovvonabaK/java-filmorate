package ru.yandex.practicum.filmorate.exceptions;

public class UserNotLikedThisFilm extends RuntimeException {
    int userId;
    int filmId;

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