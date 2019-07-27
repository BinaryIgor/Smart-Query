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
import com.iprogrammerr.smart.query.mapping.group.OneToManyOneToManyMapping;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OneToManyManyOneToManyMappingTest {

    private QueryFactory factory;

    @Before
    public void setup() {
        TestDatabase setup = new TestDatabase();
        setup.setup();
        factory = new SmartQueryFactory(setup.source());
    }

    @Test
    public void doesMapping() {
        List<AuthorWithBooksWithUsers> expected = prepare();
        List<AuthorWithBooksWithUsers> actual = factory.newQuery().dsl()
            .select("a.*", "b.id as bid", Book.AUTHOR_ID, Book.TITLE, "u.id as uid", "u.name as uname")
            .from(Author.TABLE).as("a")
            .innerJoin(Book.TABLE).as("b").on("a.id", "b.author_id")
            .innerJoin(UserBook.TABLE).as("ub").on("b.id", "ub.book_id")
            .innerJoin(User.TABLE).as("u").on("ub.user_id", "u.id")
            .orderBy(Author.NAME).desc()
            .query()
            .fetch(new OneToManyOneToManyMapping<>(Author.class, Book.class, User.class,
                (one, many) -> {
                    List<BookWithUsers> books = many.entrySet().stream().map(e ->
                        new BookWithUsers(e.getKey(), e.getValue())
                    ).collect(Collectors.toList());
                    return new AuthorWithBooksWithUsers(one, books);
                }));
        MatcherAssert.assertThat(expected, Matchers.contains(actual.toArray(new AuthorWithBooksWithUsers[0])));
    }

    private List<AuthorWithBooksWithUsers> prepare() {
        Map<Author, Map<Book, List<User>>> records = new LinkedHashMap<>();

        AuthorRecord ar1 = new AuthorRecord(factory)
            .setName("Plato")
            .setAlias("Idealist");
        ar1.insert();
        Author plato = ar1.fetch();
        records.put(plato, new LinkedHashMap<>());

        AuthorRecord ar2 = new AuthorRecord(factory)
            .setName("Aristotle")
            .setAlias("Philosopher");
        ar2.insert();
        Author aristotle = ar2.fetch();
        records.put(aristotle, new LinkedHashMap<>());

        BookRecord br1 = new BookRecord(factory)
            .setAuthorId(ar1.getId())
            .setTitle("Republic");
        br1.insert();
        Book republic = br1.fetch();
        records.get(plato).put(republic, new ArrayList<>());

        BookRecord br2 = new BookRecord(factory)
            .setAuthorId(ar2.getId())
            .setTitle("Politics");
        br2.insert();
        Book politics = br2.fetch();
        records.get(aristotle).put(politics, new ArrayList<>());

        BookRecord br3 = new BookRecord(factory)
            .setAuthorId(ar2.getId())
            .setTitle("Physics");
        br3.insert();
        Book physics = br3.fetch();
        records.get(aristotle).put(physics, new ArrayList<>());

        UserRecord ur1 = new UserRecord(factory)
            .setName("Igor");
        ur1.insert();
        User igor = ur1.fetch();
        records.get(plato).get(republic).add(igor);
        records.get(aristotle).get(politics).add(igor);
        records.get(aristotle).get(physics).add(igor);

        UserRecord ur2 = new UserRecord(factory)
            .setName("Olek");
        ur2.insert();
        User olek = ur2.fetch();
        records.get(plato).get(republic).add(olek);
        records.get(aristotle).get(physics).add(olek);

        UserRecord ur3 = new UserRecord(factory)
            .setName("Anonymous");
        ur3.insert();
        User anonymous = ur3.fetch();
        records.get(plato).get(republic).add(anonymous);

        factory.newQuery().dsl()
            .insertInto(UserBook.TABLE).values(ur1.getId(), republic.id)
            .query().end().dsl()
            .insertInto(UserBook.TABLE).values(ur1.getId(), politics.id)
            .query().end().dsl()
            .insertInto(UserBook.TABLE).values(ur1.getId(), physics.id)
            .query().end().dsl()
            .insertInto(UserBook.TABLE).values(ur2.getId(), republic.id)
            .query().end().dsl()
            .insertInto(UserBook.TABLE).values(ur2.getId(), physics.id)
            .query().end().dsl()
            .insertInto(UserBook.TABLE).values(ur3.getId(), republic.id)
            .query()
            .executeTransaction();

        return records.entrySet().stream().map(e -> {
            List<BookWithUsers> books = e.getValue().entrySet().stream()
                .map(ie -> new BookWithUsers(ie.getKey(), ie.getValue()))
                .collect(Collectors.toList());
            return new AuthorWithBooksWithUsers(e.getKey(), books);
        }).collect(Collectors.toList());
    }
}
