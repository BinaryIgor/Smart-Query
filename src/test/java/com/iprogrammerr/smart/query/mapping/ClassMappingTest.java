package com.iprogrammerr.smart.query.mapping;

import com.iprogrammerr.smart.query.QueryFactory;
import com.iprogrammerr.smart.query.SmartQueryFactory;
import com.iprogrammerr.smart.query.TestDatabase;
import com.iprogrammerr.smart.query.example.AuthorWithBook;
import com.iprogrammerr.smart.query.example.BookWithAuthor;
import com.iprogrammerr.smart.query.example.BookWithUser;
import com.iprogrammerr.smart.query.example.active.AuthorRecord;
import com.iprogrammerr.smart.query.example.active.BookRecord;
import com.iprogrammerr.smart.query.example.active.UserRecord;
import com.iprogrammerr.smart.query.example.table.Author;
import com.iprogrammerr.smart.query.example.table.Book;
import com.iprogrammerr.smart.query.example.table.User;
import com.iprogrammerr.smart.query.example.table.UserBook;
import com.iprogrammerr.smart.query.mapping.clazz.ClassMapping;
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
            .fetch(Mappings.ofClass(BookWithAuthor.class));

        MatcherAssert.assertThat(actual, Matchers.equalTo(expected));
    }

    @Test
    public void mapsFromMappingAnnotations() {
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
            .fetch(Mappings.ofClass(BookWithUser.class));

        MatcherAssert.assertThat(actual, Matchers.equalTo(expected));
    }

    @Test
    public void mapsFromEmbeddedAnnotations() {
        String author = "Fyodor Dostoevsky";
        String alias = "FD";
        String book = "Demons";
        AuthorRecord ar = new AuthorRecord(factory)
            .setName(author)
            .setAlias(alias);
        ar.insert();
        BookRecord br = new BookRecord(factory)
            .setAuthorId(ar.getId())
            .setTitle(book);
        br.insert();

        AuthorWithBook expected = new AuthorWithBook(ar.fetch(), br.fetch(), 1);
        AuthorWithBook actual = factory.newQuery().dsl()
            .select("a.*", "b.id as bid", Book.AUTHOR_ID, Book.TITLE).append(", ")
            .count(Book.AUTHOR_ID).as("books")
            .from(Author.TABLE).as("a")
            .innerJoin(Book.TABLE).as("b").on("a.id", "b.author_id")
            .query()
            .fetch(Mappings.ofClass(AuthorWithBook.class));

        MatcherAssert.assertThat(actual, Matchers.equalTo(expected));
    }

    @Test
    public void throwsExceptionOnNullPrimitives() {
        String author = "Author";
        String alias = "a";
        AuthorRecord ar = new AuthorRecord(factory)
            .setName(author)
            .setAlias(alias);
        ar.insert();

        String message = "";
        try {
            factory.newQuery().dsl()
                .select(Author.NAME, Author.ALIAS).from(Author.TABLE)
                .query()
                .fetch(new ClassMapping<>(Author.class, true));
        } catch (Exception e) {
            message = e.getMessage();
        }

        MatcherAssert.assertThat(message, Matchers.stringContainsInOrder("Constructor:",
            Author.class.getSimpleName(), "Values:"));
    }

    @Test
    public void insertsNullInLackingValues() {
        String author = "Genius";
        String alias = "g";
        AuthorRecord ar = new AuthorRecord(factory)
            .setName(author)
            .setAlias(alias);
        ar.insert();

        Author expected = new Author(ar.getId(), null, null);
        Author actual = factory.newQuery().dsl()
            .select(Author.ID).from(Author.TABLE)
            .query()
            .fetch(Mappings.ofClass(Author.class));

        MatcherAssert.assertThat(actual, Matchers.equalTo(expected));
    }
}
