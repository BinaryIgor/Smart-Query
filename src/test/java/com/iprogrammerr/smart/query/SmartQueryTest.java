package com.iprogrammerr.smart.query;

import com.iprogrammerr.smart.query.example.table.Author;
import com.iprogrammerr.smart.query.mapping.Mappings;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;

public class SmartQueryTest {

    private TestDatabase setup;

    @Before
    public void setup() {
        setup = new TestDatabase();
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
        String name = "Adam Bernard Mickiewicz";
        String alias = "Three Bards";

        long id = setup.query()
            .sql("INSERT INTO author(name, alias) values(?, ?)")
            .set(name, alias)
            .executeReturningId();
        Author author = setup.query()
            .sql("SELECT * FROM author WHERE id = ?")
            .set(id)
            .fetch(r -> {
                r.next();
                return Author.fromResult(r);
            });

        MatcherAssert.assertThat(author, Matchers.equalTo(new Author(id, name, alias)));
    }

    @Test
    public void insertsAndFetchesByClass() {
        String name = "Ayn Rand";
        String alias = "AR";

        long id = setup.query().dsl()
            .insertInto(Author.TABLE).columns(Author.NAME, Author.ALIAS).values(name, alias)
            .query()
            .executeReturningId();

        Author author = setup.query().dsl()
            .select(Author.ID + " as aid", Author.NAME, Author.ALIAS).as(Author.ALIAS_ANONYM)
            .from(Author.TABLE)
            .where(Author.ID).equal().value(id)
            .query()
            .fetch(Mappings.ofClass(Author.class));

        MatcherAssert.assertThat(author, Matchers.equalTo(new Author(id, name, alias)));
    }

    @Test
    public void insertsAndSearches() {
        String name = "Nikola";
        String alias = "Tesla";

        setup.query().dsl()
            .insertInto("author").columns("name", "alias").values(name, alias)
            .query().execute();
        String fetchedAlias = setup.query().dsl()
            .select("alias").from("author").where("LOWER(name)").like("nik%")
            .query()
            .fetch(r -> {
                r.next();
                return r.getString(1);
            });

        MatcherAssert.assertThat(fetchedAlias, Matchers.equalTo(alias));
    }

    @Test
    public void updates() {
        long id = setup.query()
            .sql("INSERT INTO author(name, alias) values(?, ?)")
            .set("Ignacy", "Anonim")
            .executeReturningId();

        String name = "Leonardo da Vinci";
        String alias = "Genius";
        setup.query()
            .sql("update author set name = ?, alias = ?")
            .set(name, alias)
            .execute();

        Author author = setup.query()
            .sql("SELECT * FROM author")
            .fetch(r -> {
                r.next();
                return Author.fromResult(r);
            });

        MatcherAssert.assertThat(author, Matchers.equalTo(new Author(id, name, alias)));
    }

    @Test
    public void deletes() {
        long id = setup.query()
            .sql("INSERT INTO author(name, alias) values(?, ?)")
            .set("abc", "def")
            .executeReturningId();

        setup.query()
            .sql("DELETE FROM author WHERE id = ?")
            .set(id)
            .execute();

        int count = setup.query()
            .sql("SELECT COUNT(id) FROM author")
            .fetch(r -> {
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
        new SmartQuery(connection, closes)
            .sql("delete from author")
            .execute();
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

        int count = setup.query().sql("SELECT COUNT(name) FROM author WHERE alias = ?")
            .set(alias)
            .fetch(r -> {
                r.next();
                return r.getInt(1);
            });

        MatcherAssert.assertThat(count, Matchers.equalTo(1));
    }

    @Test
    public void executesTransactionUsingDsl() {
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

    @Test
    public void rollsBackTransaction() {
        String name = "Friedrich Nietzsche";

        try {
            setup.query().dsl()
                .insertInto("author").columns("name", "alias").values(name, "a")
                .query().end()
                .dsl()
                .insertInto("author").columns("name", "alias").values(name, "b")
                .query()
                .executeTransaction();
        } catch (Exception ignored) {

        }

        boolean empty = setup.query().dsl()
            .select("id").from("author")
            .query()
            .fetch(r -> !r.next());

        MatcherAssert.assertThat(empty, Matchers.equalTo(true));
    }

    @Test
    public void returnsScrollableInTwoDirectionsResult() {
        setup.query().dsl()
            .insertInto(Author.TABLE).columns(Author.NAME, Author.ALIAS).values("Igor", "Ir")
            .query().end()
            .dsl()
            .insertInto(Author.TABLE).columns(Author.NAME, Author.ALIAS).values("Secret", "S")
            .query()
            .executeTransaction();

        boolean scrollable = setup.query().sql("SELECT id FROM author")
            .fetch(r -> r.next() && r.next() && r.previous());

        MatcherAssert.assertThat(scrollable, Matchers.equalTo(true));
    }
}
