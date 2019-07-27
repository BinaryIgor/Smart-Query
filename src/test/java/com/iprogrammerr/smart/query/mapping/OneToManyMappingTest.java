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
import java.util.List;

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
        AuthorWithBooks expected = prepare();
        AuthorWithBooks actual = factory.newQuery().dsl()
            .select("a.*", Book.AUTHOR_ID, Book.TITLE, "b.id as bid").from(Author.TABLE).as("a")
            .innerJoin(Book.TABLE).as("b").on("a.id", "b.author_id")
            .where(Author.NAME).equal().value(expected.author.name)
            .query()
            .fetch(Mappings.oneToMany(Author.class, Book.class, AuthorWithBooks::new));

        MatcherAssert.assertThat(actual, Matchers.equalTo(expected));
    }

    private AuthorWithBooks prepare() {
        List<Book> books = new ArrayList<>();

        AuthorRecord ar = new AuthorRecord(factory)
            .setName("Lem Stanisław")
            .setAlias("LS");
        ar.insert();

        BookRecord br1 = new BookRecord(factory)
            .setAuthorId(ar.getId())
            .setTitle("Solaris");
        br1.insert();
        books.add(br1.fetch());

        BookRecord br2 = new BookRecord(factory)
            .setAuthorId(ar.getId())
            .setTitle("Dzienniki Gwiazdowe");
        br2.insert();
        books.add(br2.fetch());

        return new AuthorWithBooks(ar.fetch(), books);
    }
}
