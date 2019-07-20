package com.iprogrammerr.smart.query;

import com.iprogrammerr.smart.query.example.Author;
import com.iprogrammerr.smart.query.example.AuthorRecord;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

public class ActiveRecordTest {

    private QueryFactory factory;

    @Before
    public void setup() {
        TestDatabase db = new TestDatabase();
        db.setup();
        factory = new SmartQueryFactory(db.source());
    }

    @Test
    public void getIdThrowsException() {
        AuthorRecord record = new AuthorRecord(factory);
        Exception exception = new Exception();
        try {
            record.getId();
        } catch (Exception e) {
            exception = e;
        }
        MatcherAssert.assertThat(exception.getMessage(), Matchers.equalTo("Id isn't initialized"));
    }

    @Test
    public void insertsAndFetches() {
        String name = "Adam";
        String alias = "Wieszcz";

        AuthorRecord record = insertAndGet(name, alias);
        int id = record.getId();

        MatcherAssert.assertThat(record.fetch(), Matchers.equalTo(new Author(id, name, alias)));
    }

    private AuthorRecord insertAndGet(String name, String alias) {
        AuthorRecord record = new AuthorRecord(factory)
            .setName(name).setAlias(alias);
        record.insert();
        return record;
    }

    @Test
    public void updates() {
        String name = "Lem";
        AuthorRecord record = insertAndGet(name, "SL");

        String newAlias = "LS";
        record.setAlias(newAlias);
        record.update();

        MatcherAssert.assertThat(record.fetch(), Matchers.equalTo(new Author(record.getId(), name, newAlias)));
    }

    @Test
    public void deletes() {
        AuthorRecord record = insertAndGet("a", "b");
        int id = record.getId();
        record.delete();

        boolean deleted = factory.newQuery().dsl().select(Author.ID).from(Author.TABLE)
            .where(Author.ID).equal().value(id)
            .query()
            .fetch(r -> !r.next());

        MatcherAssert.assertThat(deleted, Matchers.equalTo(deleted));
    }
}
