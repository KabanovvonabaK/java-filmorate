package ru.yandex.practicum.filmorate.exceptions;

public class UserCantBeFriendToHimself extends RuntimeException {

    int userId;

    public UserCantBeFriendToHimself(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }
}