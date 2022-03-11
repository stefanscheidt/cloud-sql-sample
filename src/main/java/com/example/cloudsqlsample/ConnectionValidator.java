package com.example.cloudsqlsample;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.SQLException;

@Component
public class ConnectionValidator {

    private final JdbcTemplate jdbcTemplate;

    public ConnectionValidator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void validateConnection() throws SQLException {
        jdbcTemplate.getDataSource().getConnection().isValid(1000);
    }
}
