package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserCantBeFriendToHimself;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserService {

    final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    // Возможно нужна логика проверки добавлен ли уже друг, но поскольку друзья хранятся с использованием Set
    // дублирование исключено
    public void addFriend(int id, int friendId) {
        checkFriends(id, friendId);
        log.info(String.format("Friends added for users with ids %s and %s", id, friendId));
        userStorage.findUser(id).addFriend(friendId);
        userStorage.findUser(friendId).addFriend(id);
    }

    public void removeFriend(int id, int friendId) {
        checkFriends(id, friendId);
        log.info(String.format("Friends removed for users with ids %s and %s", id, friendId));
        userStorage.findUser(id).removeFriend(friendId);
        userStorage.findUser(friendId).removeFriend(id);
    }

    // Возможно стоит проверять set друзей на null, ну а можно просто написать об этом в документации к апи.
    public List<User> getAllFriends(int id) {
        if (checkUserExistById(id)) {
            Set<Integer> friendsIds = userStorage.getAllUsers().stream().filter(x -> x.getId() == id)
                    .findFirst().get().getFriends();
            List<User> friends = new ArrayList<>();
            for (Integer userId : friendsIds) {
                friends.add(userStorage.findUser(userId));
            }
            log.info(String.format("List of friends provided for user with id %s", id));
            return friends;
        } else {
            throw new UserNotFoundException(id);
        }
    }

    public List<User> getListOfSameFriends(int id, int otherId) {
        checkFriends(id, otherId);
        log.info(String.format("Get same friends for users with ids %s and %s", id, otherId));
        Set<Integer> friendsOfId = userStorage.getAllUsers().stream().filter(x -> x.getId() == id)
                .findFirst().get().getFriends();
        Set<Integer> friendsIfOtherId = userStorage.getAllUsers().stream().filter(x -> x.getId() == otherId)
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
            sameFriendsList.add(userStorage.findUser(userId));
        }
        return sameFriendsList;
    }

    private boolean checkUserExistById(int id) {
        for (User user : userStorage.getAllUsers()) {
            if (user.getId() == id) {
                return true;
            }
        }
        return false;
    }

    private void checkFriends(int id, int friendId) {
        if (id == friendId) {
            log.error(String.format("Attempt to add/delete/check same friends for user with id %s as friend to himself",
                    id));
            throw new UserCantBeFriendToHimself(id);
        } else {
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
}