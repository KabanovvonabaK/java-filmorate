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
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class FilmService {

    final Storage<Film> filmStorage;
    final Storage<User> userStorage;

    @Autowired
    public FilmService(Storage<Film> filmStorage, Storage<User> userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }


    public void addLike(int filmId, int userId) {
        if (!checkFilmExist(filmId)) {
            throw new FilmNotFoundException(filmId);
        }
        if (!checkUserExist(userId)) {
            throw new UserNotFoundException(userId);
        }
        if (!userStorage.findById(userId).getLikedFilms().contains(filmId)) {
            Film film = filmStorage.findById(filmId);
            filmStorage.findById(filmId).setRate(film.getRate() + 1);
            userStorage.findById(userId).getLikedFilms().add(filmId);
            log.info("User with id {} like film with id {}", userId, filmId);
        } else {
            throw new UserAlreadyLikedFilm(filmId, userId);
        }
    }

    public void removeLike(int id, int userId) {
        if (!checkFilmExist(id)) {
            throw new FilmNotFoundException(id);
        }
        if (!checkUserExist(userId)) {
            throw new UserNotFoundException(userId);
        }
        if (userStorage.findById(userId).getLikedFilms().contains(id)) {
            Film film = filmStorage.findById(id);
            filmStorage.findById(id).setRate(film.getRate() - 1);
            userStorage.findById(userId).getLikedFilms().remove(id);
            log.info("User with id {} remove like from film with id {}", userId, id);
        } else {
            throw new UserNotLikedThisFilm(id, userId);
        }
    }

    public List<Film> getTopFilms(int count) {
        List<Film> films = filmStorage.findAll();
        films.sort(Comparator.comparingInt(Film::getRate).reversed());
        if (count < films.size()) {
            films = films.subList(0, count);
        }
        return films;
    }

    public List<Film> getAllFilms() {
        return filmStorage.findAll();
    }

    public Film getFilmById(int id) {
        return filmStorage.findById(id);
    }

    public Film addFilm(Film film) {
        return filmStorage.create(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.update(film);
    }

    private boolean checkFilmExist(int id) {
        for (Film film : filmStorage.findAll()) {
            if (film.getId() == id) {
                return true;
            }
        }
        return false;
    }

    private boolean checkUserExist(int id) {
        for (User user : userStorage.findAll()) {
            if (user.getId() == id) {
                return true;
            }
        }
        return false;
    }
}