package com.listywave.acceptance.common;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;

public class DatabaseCleaner {

    private final JdbcTemplate jdbcTemplate;
    private final List<String> truncateQueries;

    public DatabaseCleaner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.truncateQueries = jdbcTemplate.queryForList("""
                SELECT Concat('TRUNCATE TABLE ', TABLE_NAME, ';') AS query
                FROM INFORMATION_SCHEMA.TABLES
                WHERE TABLE_SCHEMA = 'PUBLIC'
                    """, String.class
        );
    }

    public void clean() {
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
        truncateQueries.forEach(jdbcTemplate::execute);
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
    }
}
