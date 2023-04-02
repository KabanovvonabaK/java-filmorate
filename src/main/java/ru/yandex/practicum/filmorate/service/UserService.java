package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserCantBeFriendToHimself;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;

import java.util.List;

@Slf4j
@Service
public class UserService {

    final UserDbStorage userStorage;

    @Autowired
    public UserService(@Qualifier("dbUserStorage") UserDbStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(int userId, int friendId) {
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getAllFriends(int userId) {
        return userStorage.getAllFriends(userId);
    }

    public List<User> getListOfSameFriends(int id, int otherId) {
        return userStorage.getListOfSameFriends(id, otherId);
    }

    public List<User> getAllUsers() {
        return userStorage.findAll();
    }

    public User findUser(int id) {
        return userStorage.findById(id);
    }

    public User addUser(User user) {
        return userStorage.create(user);
    }

    public User updateUser(User user) {
        return userStorage.update(user);
    }

    private boolean checkUserExistById(int id) {
        for (User user : userStorage.findAll()) {
            if (user.getId() == id) {
                return true;
            }
        }
        return false;
    }

    private void checkFriends(int id, int friendId) {
        if (id == friendId) {
            log.error("Attempt to add/delete/check same friends for user with id {} as friend to himself", id);
            throw new UserCantBeFriendToHimself(id);
        }
        if (!checkUserExistById(id)) {
            log.error(String.format("User with id %s not found.", id));
            throw new UserNotFoundException(id);
        }
        if (!checkUserExistById(friendId)) {
            log.error(String.format("User with id %s not found.", friendId));
            throw new UserNotFoundException(friendId);
        }
    }
}