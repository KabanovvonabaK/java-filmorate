package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserCantBeFriendToHimself;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserService {

    final Storage<User> userStorage;

    @Autowired
    public UserService(Storage<User> userStorage) {
        this.userStorage = userStorage;
    }

    // Возможно нужна логика проверки добавлен ли уже друг, но поскольку друзья хранятся с использованием Set
    // дублирование исключено
    public void addFriend(int id, int friendId) {
        checkFriends(id, friendId);
        log.info("Friends added for users with ids {} and {}", id, friendId);
        userStorage.findById(id).addFriend(friendId);
        userStorage.findById(friendId).addFriend(id);
    }

    public void removeFriend(int id, int friendId) {
        checkFriends(id, friendId);
        log.info("Friends removed for users with ids {} and {}", id, friendId);
        userStorage.findById(id).removeFriend(friendId);
        userStorage.findById(friendId).removeFriend(id);
    }

    // Возможно стоит проверять set друзей на null, ну а можно просто написать об этом в документации к апи.
    public List<User> getAllFriends(int id) {
        if (checkUserExistById(id)) {
            Set<Integer> friendsIds = userStorage.findAll().stream().filter(x -> x.getId() == id)
                    .findFirst().get().getFriends();
            List<User> friends = new ArrayList<>();
            for (Integer userId : friendsIds) {
                friends.add(userStorage.findById(userId));
            }
            log.info("List of friends provided for user with id {}", id);
            return friends;
        } else {
            throw new UserNotFoundException(id);
        }
    }

    public List<User> getListOfSameFriends(int id, int otherId) {
        checkFriends(id, otherId);
        log.info("Get same friends for users with ids {} and {}", id, otherId);
        Set<Integer> friendsOfId = userStorage.findAll().stream().filter(x -> x.getId() == id)
                .findFirst().get().getFriends();
        Set<Integer> friendsIfOtherId = userStorage.findAll().stream().filter(x -> x.getId() == otherId)
                .findFirst().get().getFriends();

        Set<Integer> sameIds = new HashSet<>();

        if (friendsOfId != null && friendsIfOtherId != null) {
            for (Integer i : friendsOfId) {
                for (Integer k : friendsIfOtherId) {
                    if (i.equals(k)) {
                        sameIds.add(i);
                    }
                }
            }
            log.info(sameIds.toString());
        }
        List<User> sameFriendsList = new ArrayList<>();
        for (Integer userId : sameIds) {
            sameFriendsList.add(userStorage.findById(userId));
        }
        return sameFriendsList;
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