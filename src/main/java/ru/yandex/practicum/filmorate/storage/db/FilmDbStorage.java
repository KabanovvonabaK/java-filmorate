package ru.yandex.practicum.filmorate.storage.db;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.MPANotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component("dbFilmStorage")
@AllArgsConstructor
public class FilmDbStorage implements Storage<Film> {

    private final JdbcTemplate jdbcTemplate;
    private static final LocalDate RELEASE_DATE_NOT_BEFORE = LocalDate.of(1895, 12, 28);
    private static final int MAX_DESCRIPTION_LENGTH = 200;

    @Override
    public Film create(Film film) {
        checkMPAExist(film.getMpa().getId());
        checkReleaseDate(film.getReleaseDate());
        checkDescriptionLength(film.getDescription());
        checkDuration(film.getDuration());
        final String query = "INSERT INTO FILMS (name, description, release_date, duration) " +
                "VALUES (?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(query, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setDouble(4, film.getDuration());
            return stmt;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        final String mpaQuery = "INSERT INTO MPA_FILMS (film_id, mpa_id) VALUES (?, ?);";
        jdbcTemplate.update(mpaQuery, film.getId(), film.getMpa().getId());
        final String genresQuery = "INSERT INTO FILM_GENRES (film_id, genre_id) VALUES (?, ?);";

        if (film.getGenres() != null) {
            for (Genre g : film.getGenres()) {
                jdbcTemplate.update(genresQuery, film.getId(), g.getId());
            }
        }

        film.setMpa(findMpa(film.getId()));
        film.setGenres(findGenres(film.getId()));
        return film;
    }

    @Override
    public Film update(Film film) {
        checkFilmExist(film.getId());
        checkMPAExist(film.getMpa().getId());
        final String query = "UPDATE FILMS SET name = ?, description = ?, release_date = ?, duration = ? " +
                "WHERE film_id = ?;";

        if (film.getMpa() != null) {
            final String deleteMpa = "DELETE FROM MPA_FILMS WHERE film_id = ?;";
            final String updateMpa = "INSERT INTO MPA_FILMS (film_id, mpa_id) VALUES (?, ?);";

            jdbcTemplate.update(deleteMpa, film.getId());
            jdbcTemplate.update(updateMpa, film.getId(), film.getMpa().getId());
        }

        if (film.getGenres() != null) {
            final String deleteGenresQuery = "DELETE FROM FILM_GENRES WHERE film_id = ?";
            final String updateGenresQuery = "INSERT INTO FILM_GENRES (film_id, genre_id) VALUES (?, ?)";

            jdbcTemplate.update(deleteGenresQuery, film.getId());
            for (Genre g : film.getGenres()) {
                String checkDuplicate = "SELECT * FROM FILM_GENRES WHERE film_id = ? AND genre_id = ?";
                SqlRowSet checkRows = jdbcTemplate.queryForRowSet(checkDuplicate, film.getId(), g.getId());
                if (!checkRows.next()) {
                    jdbcTemplate.update(updateGenresQuery, film.getId(), g.getId());
                }
            }
        }
        jdbcTemplate.update(query, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getId());
        film.setMpa(findMpa(film.getId()));
        film.setGenres(findGenres(film.getId()));
        return film;
    }

    @Override
    public Film findById(int id) {
        checkFilmExist(id);
        final String query = "SELECT * FROM FILMS WHERE film_id = ?;";
        return jdbcTemplate.queryForObject(query, this::parseFilm, id);
    }

    @Override
    public List<Film> findAll() {
        final String query = "SELECT * FROM FILMS;";
        return jdbcTemplate.query(query, this::parseFilm);
    }

    public Film addLike(int filmId, int userId) {
        checkUserExist(userId);
        checkFilmExist(filmId);
        final String queryCheckLike = "SELECT * FROM LIKES WHERE user_id = ? AND film_id = ?;";
        if (jdbcTemplate.queryForRowSet(queryCheckLike, userId, filmId).next()) {
            throw new ValidationException("Film already liked by this user.");
        }
        final String query = "INSERT INTO LIKES (user_id, film_id) VALUES (?, ?);";
        jdbcTemplate.update(query, userId, filmId);
        return findById(filmId);
    }

    public Film removeLike(int userId, int filmId) {
        checkUserExist(userId);
        checkFilmExist(filmId);
        final String query = "DELETE FROM LIKES WHERE user_id = ? AND film_id = ?;";
        jdbcTemplate.update(query, userId, filmId);
        log.info("DELETE FROM LIKES WHERE user_id = {} AND film_id = {}; executed", userId, filmId);
        return findById(filmId);
    }

    public List<Film> getTopFilms(int count) {
        String sqlQuery = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration " +
                "FROM FILMS f " +
                "LEFT JOIN LIKES l ON f.film_id = l.film_id " +
                "group by f.film_id, l.film_id IN ( " +
                "    SELECT LIKES.film_id " +
                "    FROM LIKES " +
                ") " +
                "ORDER BY COUNT(l.film_id) DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sqlQuery, this::parseFilm, count);
    }

    private void checkFilmExist(int filmId) {
        String query = "SELECT * FROM FILMS WHERE film_id = ?;";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(query, filmId);
        if (!sqlRowSet.next()) {
            throw new FilmNotFoundException(filmId);
        }
    }

    private void checkMPAExist(int mpaId) {
        String query = "SELECT * FROM MPA WHERE mpa_id = ?;";
        SqlRowSet mpa_ids = jdbcTemplate.queryForRowSet(query, mpaId);
        if (!mpa_ids.next()) {
            throw new MPANotFoundException(mpaId);
        }
    }

    private void checkUserExist(int userId) {
        String query = "SELECT * FROM USERS WHERE user_id = ?;";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(query, userId);
        if (!userRows.next()) {
            throw new UserNotFoundException(userId);
        }
    }


    private void checkReleaseDate(LocalDate date) {
        if (date.isBefore(RELEASE_DATE_NOT_BEFORE)) {
            throw new ValidationException("Release date can't be earlier than " + RELEASE_DATE_NOT_BEFORE);
        }
    }

    private void checkDescriptionLength(String description) {
        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new ValidationException("Description is too long.");
        }
    }

    private void checkDuration(double duration) {
        if (duration <= 0) {
            throw new ValidationException("Movie length is too short.");
        }
    }

    private Mpa findMpa(int filmId) {
        final String query = "SELECT m.mpa_id, m.name " +
                "FROM MPA m " +
                "LEFT JOIN MPA_FILMS mf ON m.mpa_id = mf.mpa_id " +
                "WHERE mf.film_id = ?";

        return jdbcTemplate.queryForObject(query, this::parseMpa, filmId);
    }

    private List<Genre> findGenres(int filmId) {
        final String query = "SELECT g.genre_id, g.name " +
                "FROM GENRES g " +
                "LEFT JOIN FILM_GENRES fg on g.genre_id = fg.GENRE_ID " +
                "WHERE fg.film_id = ?";

        return jdbcTemplate.query(query, this::parseGenre, filmId);
    }

    private Film parseFilm(ResultSet resultSet, int rowNum) throws SQLException {
        final int id = resultSet.getInt("film_id");
        final String name = resultSet.getString("name");
        final String description = resultSet.getString("description");
        final LocalDate releaseDate = resultSet.getDate("release_date").toLocalDate();
        long duration = resultSet.getLong("duration");

        return new Film(id, name, description, releaseDate, duration, findMpa(id), findGenres(id));
    }

    private Genre parseGenre(ResultSet resultSet, int rowNum) throws SQLException {
        final int id = resultSet.getInt("genre_id");
        final String name = resultSet.getString("name");
        return new Genre(id, name);
    }

    private Mpa parseMpa(ResultSet resultSet, int rowNum) throws SQLException {
        final int id = resultSet.getInt("mpa_id");
        final String name = resultSet.getString("name");
        return new Mpa(id, name);
    }
}