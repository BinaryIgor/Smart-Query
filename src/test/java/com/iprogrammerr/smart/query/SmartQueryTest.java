package com.iprogrammerr.smart.query;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;

public class SmartQueryTest {

    private TestDatabaseSetup setup;

    @Before
    public void setup() {
        setup = new TestDatabaseSetup();
        setup.setup();
    }

    private SmartQuery query() {
        try {
            return new SmartQuery(setup.source().getConnection());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void appendsSql() {
        String first = "SELECT * FROM a";
        String second = "WHERE b > 1";
        SmartQuery query = query();
        query.sql(first).sql(second);
        MatcherAssert.assertThat(query.template(), Matchers.equalTo(first + " " + second));
    }

    @Test
    public void bindsValues() {
        int first = 1;
        double second = 4.4;
        String third = "value";
        SmartQuery query = query();
        query.set(first, second, third);
        MatcherAssert.assertThat(query.values(), Matchers.contains(first, second, third));
    }

    @Test
    public void insertsAndFetches() {
        String name = "Adam";
        String alias = "Stasiek";

        long id = query().sql("INSERT INTO author(name, alias) values(?, ?)").set(name, alias)
            .executeReturningId();
        Author author = query().sql("SELECT * FROM author WHERE id = ?").set(id).fetch(r -> {
            r.next();
            return new Author(r);
        });

        MatcherAssert.assertThat(author, Matchers.equalTo(new Author(id, name, alias)));
    }

    @Test
    public void updates() {
        long id = query().sql("INSERT INTO author(name, alias) values(?, ?)").set("Ignacy", "Anonim")
            .executeReturningId();

        String name = "Leonardo";
        String alias = "Da Vinci";
        query().sql("update author set name = ?, alias = ?").set(name, alias).execute();

        Author author = query().sql("SELECT * FROM author").fetch(r -> {
            r.next();
            return new Author(r);
        });

        MatcherAssert.assertThat(author, Matchers.equalTo(new Author(id, name, alias)));
    }

    @Test
    public void deletes() {
        long id = query().sql("INSERT INTO author(name, alias) values(?, ?)").set("abc", "def")
            .executeReturningId();
        query().sql("DELETE FROM author WHERE id = ?").set(id).execute();

        int count = query().sql("SELECT COUNT(id) FROM author").fetch(r -> {
            r.next();
            return r.getInt(1);
        });

        MatcherAssert.assertThat(count, Matchers.equalTo(0));
    }

    @Test
    public void closesConnection() throws Exception {
        closesConnection(true);
    }

    private void closesConnection(boolean closes) throws Exception {
        Connection connection = setup.source().getConnection();
        new SmartQuery(connection, closes).sql("delete from author").execute();
        MatcherAssert.assertThat(connection.isClosed(), Matchers.equalTo(closes));
    }

    @Test
    public void leavesConnectionOpen() throws Exception {
        closesConnection(false);
    }

    @Test
    public void usesDialectTranslation() throws Exception {
        String suffix = "xxx";
        SmartQuery query = new SmartQuery(setup.source().getConnection(), sql -> sql + suffix);
        MatcherAssert.assertThat(query.template(), Matchers.endsWith(suffix));
    }
}
