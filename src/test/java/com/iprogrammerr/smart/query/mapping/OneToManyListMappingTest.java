package com.iprogrammerr.smart.query.mapping;

import com.iprogrammerr.smart.query.QueryFactory;
import com.iprogrammerr.smart.query.SmartQueryFactory;
import com.iprogrammerr.smart.query.TestDatabase;
import com.iprogrammerr.smart.query.example.table.Author;
import com.iprogrammerr.smart.query.example.active.AuthorRecord;
import com.iprogrammerr.smart.query.example.AuthorWithBooks;
import com.iprogrammerr.smart.query.example.table.Book;
import com.iprogrammerr.smart.query.example.active.BookRecord;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OneToManyListMappingTest {

    private QueryFactory factory;

    @Before
    public void setup() {
        TestDatabase setup = new TestDatabase();
        setup.setup();
        factory = new SmartQueryFactory(setup.source());
    }

    @Test
    public void doesMapping() {
        List<AuthorWithBooks> expected = prepare();
        List<AuthorWithBooks> actual = factory.newQuery().dsl()
            .select("a.*", Book.AUTHOR_ID, Book.TITLE, "b.id as bid").from(Author.TABLE).as("a")
            .innerJoin(Book.TABLE).as("b").on("a.id", "b.author_id")
            .orderBy(Author.NAME).desc()
            .query()
            .fetch(Mappings.listOfOneToMany(Author.class, Book.class, AuthorWithBooks::new));

        MatcherAssert.assertThat(actual, Matchers.contains(expected.get(0), expected.get(1)));
    }

    private List<AuthorWithBooks> prepare() {
        List<Book> lemBooks = new ArrayList<>();
        List<Book> aristotleBooks = new ArrayList<>();

        AuthorRecord ar1 = new AuthorRecord(factory)
            .setName("Lem Stanisław")
            .setAlias("LS");
        ar1.insert();
        Author lem = ar1.fetch();

        AuthorRecord ar2 = new AuthorRecord(factory)
            .setName("Aristotle")
            .setAlias("Philosopher");
        ar2.insert();
        Author aristotle = ar2.fetch();

        BookRecord br1 = new BookRecord(factory)
            .setAuthorId(ar1.getId())
            .setTitle("Cyberiada");
        br1.insert();
        lemBooks.add(br1.fetch());

        BookRecord br2 = new BookRecord(factory)
            .setAuthorId(ar1.getId())
            .setTitle("Dzienniki Gwiazdowe");
        br2.insert();
        lemBooks.add(br2.fetch());

        BookRecord br3 = new BookRecord(factory)
            .setAuthorId(ar2.getId())
            .setTitle("Politics");
        br3.insert();
        aristotleBooks.add(br3.fetch());

        BookRecord br4 = new BookRecord(factory)
            .setAuthorId(ar2.getId())
            .setTitle("Physics");
        br4.insert();
        aristotleBooks.add(br4.fetch());

        return Arrays.asList(new AuthorWithBooks(lem, lemBooks),
            new AuthorWithBooks(aristotle, aristotleBooks));
    }
}
