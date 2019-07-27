package com.iprogrammerr.smart.query.mapping;

import com.iprogrammerr.smart.query.QueryFactory;
import com.iprogrammerr.smart.query.SmartQueryFactory;
import com.iprogrammerr.smart.query.TestDatabase;
import com.iprogrammerr.smart.query.example.Author;
import com.iprogrammerr.smart.query.example.AuthorRecord;
import com.iprogrammerr.smart.query.example.AuthorWithBooksWithUsers;
import com.iprogrammerr.smart.query.example.Book;
import com.iprogrammerr.smart.query.example.BookRecord;
import com.iprogrammerr.smart.query.example.BookWithUsers;
import com.iprogrammerr.smart.query.example.User;
import com.iprogrammerr.smart.query.example.UserBook;
import com.iprogrammerr.smart.query.example.UserRecord;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OneToManyOneToManyMappingTest {

    private QueryFactory factory;

    @Before
    public void setup() {
        TestDatabase setup = new TestDatabase();
        setup.setup();
        factory = new SmartQueryFactory(setup.source());
    }

    @Test
    public void doesMapping() {
        AuthorWithBooksWithUsers expected = prepare();
        AuthorWithBooksWithUsers actual = factory.newQuery().dsl()
            .select("a.*", "b.id as bid", Book.AUTHOR_ID, Book.TITLE, "u.id as uid", "u.name as uname")
            .from(Author.TABLE).as("a")
            .innerJoin(Book.TABLE).as("b").on("a.id", "b.author_id")
            .innerJoin(UserBook.TABLE).as("ub").on("b.id", "ub.book_id")
            .innerJoin(User.TABLE).as("u").on("ub.user_id", "u.id")
            .where(Author.ALIAS).equal().value(expected.author.alias)
            .query()
            .fetch(Mappings.oneToManyOneToMany(Author.class, Book.class, User.class,
                (one, many) -> {
                    List<BookWithUsers> books = many.entrySet().stream().map(e ->
                        new BookWithUsers(e.getKey(), e.getValue())
                    ).collect(Collectors.toList());
                    return new AuthorWithBooksWithUsers(one, books);
                }));
        MatcherAssert.assertThat(actual, Matchers.equalTo(expected));
    }

    private AuthorWithBooksWithUsers prepare() {
        Map<Book, List<User>> records = new LinkedHashMap<>();

        AuthorRecord ar = new AuthorRecord(factory)
            .setName("Aristotle")
            .setAlias("Philosopher");
        ar.insert();

        BookRecord br1 = new BookRecord(factory)
            .setAuthorId(ar.getId())
            .setTitle("Politics");
        br1.insert();
        Book politics = br1.fetch();
        records.put(politics, new ArrayList<>());

        BookRecord br2 = new BookRecord(factory)
            .setAuthorId(ar.getId())
            .setTitle("Physics");
        br2.insert();
        Book physics = br2.fetch();
        records.put(physics, new ArrayList<>());

        UserRecord ur1 = new UserRecord(factory)
            .setName("Igor");
        ur1.insert();
        User igor = ur1.fetch();
        records.get(politics).add(igor);
        records.get(physics).add(igor);

        UserRecord ur2 = new UserRecord(factory)
            .setName("Olek");
        ur2.insert();
        User olek = ur2.fetch();
        records.get(physics).add(olek);

        UserRecord ur3 = new UserRecord(factory)
            .setName("Anonymous");
        ur3.insert();
        User anonymous = ur3.fetch();
        records.get(physics).add(anonymous);

        factory.newQuery().dsl()
            .insertInto(UserBook.TABLE).values(ur1.getId(), politics.id)
            .query().end().dsl()
            .insertInto(UserBook.TABLE).values(ur1.getId(), physics.id)
            .query().end().dsl()
            .insertInto(UserBook.TABLE).values(ur2.getId(), physics.id)
            .query().end().dsl()
            .insertInto(UserBook.TABLE).values(ur3.getId(), physics.id)
            .query()
            .executeTransaction();

        List<BookWithUsers> books = records.entrySet().stream().map(e ->
            new BookWithUsers(e.getKey(), e.getValue())
        ).collect(Collectors.toList());
        return new AuthorWithBooksWithUsers(ar.fetch(), books);
    }
}
