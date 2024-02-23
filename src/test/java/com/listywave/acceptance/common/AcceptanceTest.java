package com.listywave.acceptance.common;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

import com.listywave.user.repository.user.UserRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;

@TestInstance(PER_CLASS)
@DisplayNameGeneration(ReplaceUnderscores.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class AcceptanceTest {

    @LocalServerPort
    protected int port;
    private DatabaseCleaner databaseCleaner;

    @Autowired
    protected UserRepository userRepository;

    @BeforeAll
    void beforeAll(@Autowired JdbcTemplate jdbcTemplate) {
        this.databaseCleaner = new DatabaseCleaner(jdbcTemplate);
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        databaseCleaner.clean();
    }
}
