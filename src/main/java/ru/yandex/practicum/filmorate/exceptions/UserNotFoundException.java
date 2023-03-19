package ru.yandex.practicum.filmorate.exceptions;

public class UserNotFoundException extends RuntimeException {
    private final int userId;

    public UserNotFoundException(int id) {
        this.userId = id;
    }

    public int getUserId() {
        return userId;
    }
}