package ru.yandex.practicum.filmorate.exceptions;

public class UserNotFoundException extends RuntimeException {
    int userId;

    public UserNotFoundException(int id) {
        this.userId = id;
    }

    public int getUserId() {
        return userId;
    }
}