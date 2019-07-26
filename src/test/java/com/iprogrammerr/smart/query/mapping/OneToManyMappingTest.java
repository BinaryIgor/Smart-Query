package com.iprogrammerr.smart.query.mapping;

import com.iprogrammerr.smart.query.QueryFactory;
import com.iprogrammerr.smart.query.SmartQueryFactory;
import com.iprogrammerr.smart.query.TestDatabase;
import com.iprogrammerr.smart.query.example.Author;
import com.iprogrammerr.smart.query.example.AuthorRecord;
import com.iprogrammerr.smart.query.example.Book;
import com.iprogrammerr.smart.query.example.BookRecord;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OneToManyMappingTest {

    private QueryFactory factory;

    @Before
    public void setup() {
        TestDatabase setup = new TestDatabase();
        setup.setup();
        factory = new SmartQueryFactory(setup.source());
    }

    @Test
    public void doesMapping() {
        Map<Author, List<Book>> expected = prepare();
        Map<Author, List<Book>> actual = factory.newQuery().dsl()
            .select("a.*", Book.AUTHOR_ID, Book.TITLE, "b.id as bid").from(Author.TABLE).as("a")
            .innerJoin(Book.TABLE).as("b").on("a.id", "b.author_id")
            .orderBy(Author.NAME).desc()
            .query()
            .fetch(new OneToManyMapping<>(Author.class, Book.class));
        Iterator<Map.Entry<Author, List<Book>>> entries = expected.entrySet().iterator();
        Map.Entry<Author, List<Book>> first = entries.next();
        Map.Entry<Author, List<Book>> second = entries.next();
        MatcherAssert.assertThat(actual.entrySet(), Matchers.contains(first, second));
    }

    private Map<Author, List<Book>> prepare() {
        Map<Author, List<Book>> records = new LinkedHashMap<>();

        AuthorRecord ar1 = new AuthorRecord(factory)
            .setName("Lem Stanisław")
            .setAlias("LS");
        ar1.insert();
        Author lem = ar1.fetch();
        records.put(lem, new ArrayList<>());

        AuthorRecord ar2 = new AuthorRecord(factory)
            .setName("Aristotle")
            .setAlias("Philosopher");
        ar2.insert();
        Author aristotle = ar2.fetch();
        records.put(aristotle, new ArrayList<>());

        BookRecord br1 = new BookRecord(factory)
            .setAuthorId(ar1.getId())
            .setTitle("Cyberiada");
        br1.insert();
        records.get(lem).add(br1.fetch());

        BookRecord br2 = new BookRecord(factory)
            .setAuthorId(ar1.getId())
            .setTitle("Dzienniki Gwiazdowe");
        br2.insert();
        records.get(lem).add(br2.fetch());

        BookRecord br3 = new BookRecord(factory)
            .setAuthorId(ar2.getId())
            .setTitle("Politics");
        br3.insert();
        records.get(aristotle).add(br3.fetch());

        BookRecord br4 = new BookRecord(factory)
            .setAuthorId(ar2.getId())
            .setTitle("Physics");
        br4.insert();
        records.get(aristotle).add(br4.fetch());

        return records;
    }
}
