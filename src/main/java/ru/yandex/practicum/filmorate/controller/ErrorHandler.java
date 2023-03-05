package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.ErrorResponse;


@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFound(final UserNotFoundException e) {
        return new ErrorResponse(String.format("User with id %s not found.", e.getUserId()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUserCantBeFriendToHimself(final UserCantBeFriendToHimself e) {
        return new ErrorResponse(String.format("User can't be friend to himself, user id is %s", e.getUserId()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleFilmNotFound(final FilmNotFoundException e) {
        return new ErrorResponse(String.format("Film with id %s don't exist", e.getFilmId()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ErrorResponse handleUserAlreadyLikedFilm(final UserAlreadyLikedFilm e) {
        return new ErrorResponse(String.format("User with id %s already liked film with id %s",
                e.getUserId(), e.getFilmId()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUserNotLikedThisFilm(final UserNotLikedThisFilm e) {
        return new ErrorResponse(String.format("User with id %s not liked film with id %s",
                e.getUserId(), e.getFilmId()));
    }
}