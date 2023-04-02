package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserCantBeFriendToHimself;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component("dbUserStorage")
@RequiredArgsConstructor
public class UserDbStorage implements Storage<User> {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User create(User user) {
        validateUserEmail(user);
        final String query = "INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY) VALUES (?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(query, new String[]{"user_id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return user;
    }

    @Override
    public User update(User user) {
        checkUserExist(user.getId());
        final String query = "UPDATE USERS SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?;";
        jdbcTemplate.update(query, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        log.info("UPDATE USERS SET EMAIL = {}, LOGIN = {}, NAME = {}, BIRTHDAY = {} WHERE user_id = {}; executed",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    @Override
    public User findById(int id) {
        checkUserExist(id);
        String query = "SELECT * FROM USERS WHERE user_id = ?;";
        return jdbcTemplate.queryForObject(query, this::parseUser, id);
    }

    @Override
    public List<User> findAll() {
        final String query = "SELECT * FROM USERS;";
        log.info("'SELECT * FROM USERS' executed");
        return jdbcTemplate.query(query, this::parseUser);
    }

    public void addFriend(int userId, int friendId) {
        checkUserExist(userId);
        checkUserExist(friendId);
        if (userId == friendId) {
            throw new UserCantBeFriendToHimself(userId);
        }
        String query = "INSERT INTO FRIENDSHIP (user_id, friend_id) VALUES (?, ?);";
        jdbcTemplate.update(query, userId, friendId);
        log.info("INSERT INTO FRIENDSHIP (user_id, friend_id) VALUES ({}, {}); executed", userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        checkUserExist(userId);
        checkUserExist(friendId);
        if (userId == friendId) {
            throw new UserCantBeFriendToHimself(userId);
        }
        String query = "DELETE FROM FRIENDSHIP WHERE user_id = ? AND friend_id = ?;";
        jdbcTemplate.update(query, userId, friendId);
        log.info("DELETE FROM FRIENDSHIP WHERE user_id = {} AND friend_id = {}; executed", userId, friendId);
    }

    public List<User> getAllFriends(int userId) {
        checkUserExist(userId);
        String query = "SELECT * " +
                "FROM USERS u " +
                "JOIN FRIENDSHIP f ON u.user_id = f.friend_id " +
                "WHERE f.user_id = ?";
        return jdbcTemplate.query(query, this::parseUser, userId);
    }

    public List<User> getListOfSameFriends(int userId, int otherUserId) {
        checkUserExist(userId);
        checkUserExist(otherUserId);
        String query = "SELECT u.user_id, " +
                "u.name, " +
                "u.email, " +
                "u.login, " +
                "u.birthday " +
                "FROM FRIENDSHIP AS f " +
                "LEFT JOIN USERS AS u ON f.friend_id = u.user_id " +
                "WHERE f.user_id = ? " +
                "AND f.friend_id IN (SELECT friend_id FROM FRIENDSHIP AS fs WHERE fs.user_id = ?);";
        return jdbcTemplate.query(query, this::parseUser, userId, otherUserId);
    }

    private User parseUser(ResultSet resultSet, int row) throws SQLException {
        int userId = resultSet.getInt("user_id");
        String email = resultSet.getString("email");
        String login = resultSet.getNString("login");
        String name = resultSet.getString("name");
        LocalDate birthday = resultSet.getDate("birthday").toLocalDate();

        return new User(userId, email, login, name, birthday);
    }

    private void checkUserExist(int userId) {
        String query = "SELECT * FROM USERS WHERE user_id = ?;";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(query, userId);
        if (!userRows.next()) {
            throw new UserNotFoundException(userId);
        }
    }

    private void validateUserEmail(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Email should be filled.");
        }
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Email should contain @.");
        }
    }
}