package ru.yandex.practicum.filmorate.exceptions;

public class MPANotFoundException extends RuntimeException {

    private final int mpaId;

    public MPANotFoundException(int mpaId) {
        this.mpaId = mpaId;
    }

    public int getMpaId() {
        return mpaId;
    }
}