package ru.yandex.practicum.filmorate.storage.db;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
@AllArgsConstructor
public class GenreDbStorage implements Storage<Genre> {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Genre create(Genre genre) {
        String query = "INSERT INTO GENRES (name) VALUES (?);";
        jdbcTemplate.update(query, genre.getName());
        log.info("INSERT INTO GENRES (name) VALUES ({}); executed", genre.getName());
        return genre;
    }

    @Override
    public Genre update(Genre genre) {
        checkGenreExist(genre.getId());
        String query = "UPDATE GENRES SET name = ? WHERE genre_id = ?;";
        jdbcTemplate.update(query, genre.getName(), genre.getId());
        log.info("UPDATE GENRES SET name = {} WHERE genre_id = {}; executed", genre.getName(), genre.getId());
        return null;
    }

    @Override
    public Genre findById(int genreId) {
        checkGenreExist(genreId);
        String query = "SELECT * FROM GENRES WHERE genre_id = ?;";
        log.info("SELECT * FROM GENRE WHERE genre_id = {}; executed", genreId);
        return jdbcTemplate.queryForObject(query, this::parseGenre, genreId);
    }

    @Override
    public List<Genre> findAll() {
        String query = "SELECT * FROM GENRES;";
        log.info("SELECT * FROM GENRES; executed");
        return jdbcTemplate.query(query, this::parseGenre);
    }

    private void checkGenreExist(int genreId) {
        String query = "SELECT * FROM GENRES WHERE genre_id = ?;";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(query, genreId);
        if (!userRows.next()) {
            throw new GenreNotFoundException(genreId);
        }
    }

    private Genre parseGenre(ResultSet resultSet, int rowNum) throws SQLException {
        int id = resultSet.getInt("genre_id");
        String nameGenre = resultSet.getString("name");

        return new Genre(id, nameGenre);
    }
}