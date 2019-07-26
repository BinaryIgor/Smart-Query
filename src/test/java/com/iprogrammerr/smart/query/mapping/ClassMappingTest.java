package com.iprogrammerr.smart.query.mapping;

import com.iprogrammerr.smart.query.QueryFactory;
import com.iprogrammerr.smart.query.SmartQueryFactory;
import com.iprogrammerr.smart.query.TestDatabase;
import com.iprogrammerr.smart.query.example.Author;
import com.iprogrammerr.smart.query.example.AuthorRecord;
import com.iprogrammerr.smart.query.example.Book;
import com.iprogrammerr.smart.query.example.BookRecord;
import com.iprogrammerr.smart.query.example.BookWithAuthor;
import com.iprogrammerr.smart.query.example.BookWithUser;
import com.iprogrammerr.smart.query.example.User;
import com.iprogrammerr.smart.query.example.UserBook;
import com.iprogrammerr.smart.query.example.UserRecord;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

public class ClassMappingTest {

    private QueryFactory factory;

    @Before
    public void setup() {
        TestDatabase setup = new TestDatabase();
        setup.setup();
        factory = new SmartQueryFactory(setup.source());
    }

    @Test
    public void mapsDefaults() {
        String author = "Lem";
        String title = "Fiasko";
        AuthorRecord ar = new AuthorRecord(factory)
            .setName(author)
            .setAlias("L");
        ar.insert();
        BookRecord br = new BookRecord(factory)
            .setAuthorId(ar.getId())
            .setTitle(title);
        br.insert();

        BookWithAuthor expected = new BookWithAuthor(author, title);
        BookWithAuthor actual = factory.newQuery().dsl()
            .select("a.name as author", Book.TITLE).from(Author.TABLE).as("a")
            .innerJoin(Book.TABLE).as("b").on("a.id", "b.author_id")
            .query()
            .fetch(new ClassMapping<>(BookWithAuthor.class, true));

        MatcherAssert.assertThat(actual, Matchers.equalTo(expected));
    }

    @Test
    public void mapsFromAnnotations() {
        String book = "Thus Spoke Zarathustra";
        String user = "Igor";
        AuthorRecord ar = new AuthorRecord(factory)
            .setName("Nietzsche")
            .setAlias("Dynamite");
        ar.insert();
        BookRecord br = new BookRecord(factory)
            .setAuthorId(ar.getId())
            .setTitle(book);
        br.insert();
        UserRecord ur = new UserRecord(factory)
            .setName(user);
        ur.insert();
        factory.newQuery().dsl()
            .insertInto(UserBook.TABLE).values(ur.getId(), br.getId())
            .query().execute();

        BookWithUser expected = new BookWithUser(br.getId(), ur.getId(), book, user);
        BookWithUser actual = factory.newQuery().dsl()
            .select("b.id as bid", "u.id as uid", Book.TITLE, User.NAME).from(Book.TABLE).as("b")
            .innerJoin(UserBook.TABLE).as("ub").on("b.id", "ub.book_id")
            .innerJoin(User.TABLE).as("u").on("ub.user_id", "u.id")
            .query()
            .fetch(r -> {
                r.next();
                return new ClassMapping<>(BookWithUser.class).value(r);
            });

        MatcherAssert.assertThat(actual, Matchers.equalTo(expected));
    }
}
