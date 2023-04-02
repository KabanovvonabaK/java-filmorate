package ru.yandex.practicum.filmorate.storage.db;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.MPANotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
@AllArgsConstructor
public class MpaDbStorage implements Storage<Mpa> {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Mpa create(Mpa mpa) {
        String query = "INSERT INTO MPA (name) VALUES (?);";
        jdbcTemplate.update(query, mpa.getName());
        log.info("INSERT INTO MPA (name) VALUES ({}); executed", mpa.getName());
        return mpa;
    }

    @Override
    public Mpa update(Mpa mpa) {
        checkMpaExist(mpa.getId());
        String query = "UPDATE MPA SET name = ? WHERE mpa_id = ?;";
        jdbcTemplate.update(query, mpa.getName(), mpa.getId());
        log.info("UPDATE MPA SET name = {} WHERE mpa_id = {}; executed", mpa.getName(), mpa.getId());
        return null;
    }

    @Override
    public Mpa findById(int mpaId) {
        checkMpaExist(mpaId);
        String query = "SELECT * FROM MPA WHERE mpa_id = ?;";
        log.info("SELECT * FROM MPA WHERE mpa_id = {}; executed", mpaId);
        return jdbcTemplate.queryForObject(query, this::parseMpa, mpaId);
    }

    @Override
    public List<Mpa> findAll() {
        String query = "SELECT * FROM MPA;";
        log.info("SELECT * FROM MPA; executed");
        return jdbcTemplate.query(query, this::parseMpa);
    }

    private void checkMpaExist(int mpaId) {
        String query = "SELECT * FROM MPA WHERE mpa_id = ?;";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(query, mpaId);
        if (!userRows.next()) {
            throw new MPANotFoundException(mpaId);
        }
    }

    private Mpa parseMpa(ResultSet resultSet, int rowNum) throws SQLException {
        int id = resultSet.getInt("mpa_id");
        String nameMpa = resultSet.getString("name");

        return new Mpa(id, nameMpa);
    }
}