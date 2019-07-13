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

    @Test
    public void appendsSql() {
        String first = "SELECT * FROM a";
        String second = "WHERE b > 1";
        SmartQuery query = setup.query();
        query.sql(first).sql(second);
        MatcherAssert.assertThat(query.template(), Matchers.equalTo(first + " " + second));
    }

    @Test
    public void bindsValues() {
        int first = 1;
        double second = 4.4;
        String third = "value";
        SmartQuery query = setup.query();
        query.set(first, second, third);
        MatcherAssert.assertThat(query.values(), Matchers.contains(first, second, third));
    }

    @Test
    public void insertsAndFetches() {
        String name = "Adam";
        String alias = "Stasiek";

        long id = setup.query().sql("INSERT INTO author(name, alias) values(?, ?)").set(name, alias)
            .executeReturningId();
        Author author = setup.query().sql("SELECT * FROM author WHERE id = ?").set(id).fetch(r -> {
            r.next();
            return new Author(r);
        });

        MatcherAssert.assertThat(author, Matchers.equalTo(new Author(id, name, alias)));
    }

    @Test
    public void insertsAndSearches() {
        String name = "Nikola";
        String alias = "Tesla";

        setup.query().dsl()
            .insertInto("author").columns("name", "alias").values(name, alias)
            .query().execute();
        String fetchedAlias = setup.query()
            .dsl().select("alias").from("author").where("LOWER(name)").like("nik%")
            .query()
            .fetch(r -> {
                r.next();
                return r.getString(1);
            });

        MatcherAssert.assertThat(fetchedAlias, Matchers.equalTo(alias));
    }

    @Test
    public void updates() {
        long id = setup.query().sql("INSERT INTO author(name, alias) values(?, ?)").set("Ignacy", "Anonim")
            .executeReturningId();

        String name = "Leonardo";
        String alias = "Da Vinci";
        setup.query().sql("update author set name = ?, alias = ?").set(name, alias).execute();

        Author author = setup.query().sql("SELECT * FROM author").fetch(r -> {
            r.next();
            return new Author(r);
        });

        MatcherAssert.assertThat(author, Matchers.equalTo(new Author(id, name, alias)));
    }

    @Test
    public void deletes() {
        long id = setup.query().sql("INSERT INTO author(name, alias) values(?, ?)").set("abc", "def")
            .executeReturningId();
        setup.query().sql("DELETE FROM author WHERE id = ?").set(id).execute();

        int count = setup.query().sql("SELECT COUNT(id) FROM author").fetch(r -> {
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

    @Test
    public void executesTransaction() {
        String name = "None";
        String alias = "Genius";

        setup.query().sql("INSERT INTO author(name, alias) VALUES(?, ?)")
            .set(name, alias)
            .end()
            .sql("DELETE FROM author WHERE name = ?")
            .set(name)
            .end()
            .sql("INSERT INTO author(name, alias) VALUES(?, ?)")
            .set(name, alias)
            .executeTransaction();

        int count = setup.query().sql("SELECT COUNT(name) FROM author WHERE alias = ?").set(alias)
            .fetch(r -> {
                r.next();
                return r.getInt(1);
            });

        MatcherAssert.assertThat(count, Matchers.equalTo(1));
    }

    @Test
    public void executesTransactionWithDsl() {
        String name = "Lem";
        String alias = "Genius";

        setup.query().dsl()
            .insertInto("author").columns("name", "alias").values(name, alias)
            .query().end()
            .dsl()
            .delete("author").where("name").equal().value(name)
            .query().end()
            .dsl()
            .insertInto("author").columns("name", "alias").values(name, alias)
            .query()
            .executeTransaction();

        int count = setup.query().dsl()
            .select().count("name").from("author").where("alias").equal().value(alias)
            .query()
            .fetch(r -> {
                r.next();
                return r.getInt(1);
            });

        MatcherAssert.assertThat(count, Matchers.equalTo(1));
    }
}
