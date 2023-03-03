package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyLikedFilm;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotLikedThisFilm;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class FilmService {

    final FilmStorage filmStorage;
    final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }


    public void addLike(int id, int userId) {
        if (checkFilmExist(id)) {
            if (checkUserExist(userId)) {
                if (!userStorage.findUser(userId).getLikedFilms().contains(id)) {
                    Film film = filmStorage.getFilmById(id);
                    filmStorage.getFilmById(id).setRate(film.getRate() + 1);
                    userStorage.findUser(userId).getLikedFilms().add(id);
                    log.info(String.format("User with id %s like film with id %s", userId, id));
                } else {
                    throw new UserAlreadyLikedFilm(id, userId);
                }
            } else {
                throw new UserNotFoundException(userId);
            }
        } else {
            throw new FilmNotFoundException(id);
        }
    }

    public void removeLike(int id, int userId) {
        if (checkFilmExist(id)) {
            if (checkUserExist(userId)) {
                if (userStorage.findUser(userId).getLikedFilms().contains(id)) {
                    Film film = filmStorage.getFilmById(id);
                    filmStorage.getFilmById(id).setRate(film.getRate() - 1);
                    userStorage.findUser(userId).getLikedFilms().remove(id);
                    log.info(String.format("User with id %s remove like from film with id %s", userId, id));
                } else {
                    throw new UserNotLikedThisFilm(id, userId);
                }
            } else {
                throw new UserNotFoundException(userId);
            }
        } else {
            throw new FilmNotFoundException(id);
        }
    }

    public List<Film> getTopFilms(int count) {
        List<Film> films = filmStorage.getAllFilms();
        films.sort(Comparator.comparingInt(Film::getRate).reversed());
        if (count < films.size()) {
            films = films.subList(0, count);
        }
        return films;
    }

    private boolean checkFilmExist(int id) {
        for (Film film : filmStorage.getAllFilms()) {
            if (film.getId() == id) {
                return true;
            }
        }
        return false;
    }

    private boolean checkUserExist(int id) {
        for (User user : userStorage.getAllUsers()) {
            if (user.getId() == id) {
                return true;
            }
        }
        return false;
    }
}