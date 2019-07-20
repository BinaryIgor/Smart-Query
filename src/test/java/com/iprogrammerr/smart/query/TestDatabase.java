package com.iprogrammerr.smart.query;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.h2.tools.RunScript;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;

public class TestDatabase {

    private final String user;
    private final String password;
    private final String jdbcUrl;
    private DataSource source;

    public TestDatabase(String user, String password, String jdbcUrl) {
        this.user = user;
        this.password = password;
        this.jdbcUrl = jdbcUrl;
    }

    public TestDatabase() {
        this("test", "test", "jdbc:h2:mem:test");
    }

    public void setup() {
        try (Connection c = source().getConnection();
             BufferedReader r = new BufferedReader(new InputStreamReader(
                 TestDatabase.class.getResourceAsStream("/schema.sql")))) {
            RunScript.execute(c, r);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public DataSource source() {
        if (source == null) {
            HikariConfig config = new HikariConfig();
            config.setUsername(user);
            config.setPassword(password);
            config.setJdbcUrl(jdbcUrl);
            source = new HikariDataSource(config);
        }
        return source;
    }

    public SmartQuery query() {
        try {
            return new SmartQuery(source().getConnection());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
