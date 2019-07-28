[![Build Status](https://travis-ci.com/Iprogrammerr/Smart-Query.svg?branch=master)](https://travis-ci.com/Iprogrammerr/Smart-Query)
[![Test Coverage](https://img.shields.io/codecov/c/github/iprogrammerr/smart-query/master.svg)](https://codecov.io/gh/Iprogrammerr/Smart-Query/branch/master)
[![Maven Central](https://img.shields.io/maven-central/v/com.iprogrammerr/smart-query.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.iprogrammerr%22%20AND%20a:%22smart-query%22)
# Smart Query
Simple, yet powerful jdbc wrapper that gives both convenience and complete power over sql.
## Plain SQL
```java
Connection connection = DriverManager.getConnection("jdbc:h2:mem:test");
Author author = new SmartQuery(connection)
    .sql("SELECT * FROM author WHERE id = ?")
    .set(1)
    .fetch(r -> {
        r.next();
        return new Author(r.getLong("id"), r.getString("name"), r.getString("alias"));
     });
     
author = new SmartQuery(connection)
    .sql("SELECT * FROM author WHERE id != ?")
    .set(2)
    .fetch(Mappings.ofClass(Author.class));

long id = new SmartQuery(connection)
    .sql("INSERT INTO book(pages, author) values(?, ?)")
    .set(500, "None")
    .executeReturningId();
    
new SmartQuery(connection)
    .sql("UPDATE book SET pages = ?, author = ?")
    .sql("WHERE id = ?")
    .set(600, "Some", id)
    .execute();
    
new SmartQuery(connection)
    .sql("DELETE FROM book WHERE = ?")
    .set(id)
    .execute();
```
## DSL
```java
Author author = new SmartQuery(connection).dsl()
    .selectAll().from("author").where("id").equal().value(1)
    .query()
    .fetch(r -> {
        r.next();
        return new Author(r.getLong("id"), r.getString("name"), r.getString("alias"));
    });
    
author = new SmartQuery(connection).dsl()
    .selectAll().from("author").where("id").notEqual().value(2)
    .query()
    .fetch(Mappings.ofClass(Author.class));
    
long id = new SmartQuery(connection).dsl()
    .insertInto("book").columns("pages", "author").values(500, "None")
    .query()
    .executeReturningId();
    
new SmartQuery(connection).dsl()
    .update("book").set("pages", 600).set("author", "Some").where("id").equal().value(id)
    .query()
    .execute();
    
new SmartQuery(connection).dsl()
    .delete("book").where("id").equal().value(id)
    .query()
    .execute();
```
## Mapping
Results are mapped using functional interface.
```java
public interface ResultMapping<T> {
    T value(ResultSet result) throws Exception;
}
```
Which means that you can implement your own mapping logic or use one of the provided implementations. Probably, the most useful will be:
```java
Mappings.ofClass(Class<?> clazz)
Mappings.listOfClass(Class<?> clazz)
```
Both of them are using reflection to support primitives and nested objects mapping.
```java
public class BookWithUser {

    @Mapping({"bid", "b_id"})
    private final int bookId;
    @Mapping("uid")
    private final int userId;
    @Mapping("title")
    private final String book;
    @Mapping("name")
    private final String user;

    public BookWithUser(int bookId, int userId, String book, String user) {
        this.bookId = bookId;
        this.userId = userId;
        this.book = book;
        this.user = user;
    }
}

 BookWithUser book = factory.newQuery().dsl()
    .select("b.id as bid", "u.id as uid", Book.TITLE, User.NAME).from(Book.TABLE).as("b")
    .innerJoin(UserBook.TABLE).as("ub").on("b.id", "ub.book_id")
    .innerJoin(User.TABLE).as("u").on("ub.user_id", "u.id")
    .query()
    .fetch(Mappings.ofClass(BookWithUser.class));
```
In the above example ClassMapping will look for integer of labels: bid, b_id and bookid in the ResultSet. If mapping isn't specified in the annotation only field name will be used. If it is, all labels are used to search(ignoringCase on both sides) for value untill one of them will be present in a given result. Otherwise, null will be inserted(if it's an object, exception will be thrown otherwise). **The only requirement is a constructor with all arguments in the same order as fields declaration**.  You can also embed other objects as follows:
```java
public class AuthorWithBook {

    @Embedded
    public final Author author;
    @Embedded
    public final Book book;
    @Mapping("books")
    public final int booksCount;

    public AuthorWithBook(Author author, Book book, int booksCount) {
        this.author = author;
        this.book = book;
        this.booksCount = booksCount;
    }
}

AuthorWithBook author = factory.newQuery().dsl()
    .select("a.*", "b.id as bid", Book.AUTHOR_ID, Book.TITLE).append(",")
    .count(Book.AUTHOR_ID).as("books")
    .from(Author.TABLE).as("a")
    .innerJoin(Book.TABLE).as("b").on("a.id", "b.author_id")
    .query()
    .fetch(Mappings.ofClass(AuthorWithBook.class));
```
Collections are not supported. Reason being is that each object is mapped from one row of the ResultSet. Goal of this library is to keep everything transparent, avoiding reflection magic and using it only for simple, repetitive things, like mapping single objects. To map complex relations, you can use other, explicit mappings:
```java
AuthorWithBooks author = factory.newQuery().dsl()
    select("a.*", Book.AUTHOR_ID, Book.TITLE, "b.id as bid").from(Author.TABLE).as("a")
    .innerJoin(Book.TABLE).as("b").on("a.id", "b.author_id")
    .where(Author.NAME).equal().value("Stanisław Lem")
    .query()
    //Author mapping, Book mapping, Group mapping(single author, list of books)
    .fetch(Mappings.oneToMany(Author.class, Book.class, (author, books) -> new AuthorWithBooks(a, books)));
    
List<AuthorWithBooks> authors = factory.newQuery().dsl()
    .select("a.*", Book.AUTHOR_ID, Book.TITLE, "b.id as bid").from(Author.TABLE).as("a")
    .innerJoin(Book.TABLE).as("b").on("a.id", "b.author_id")
    .orderBy(Author.NAME).desc()
    .query()
    .fetch(Mappings.listOfOneToMany(Author.class, Book.class, AuthorWithBooks::new));
```
Groups are matched using simple, functional interface.
```java
public interface GroupPredicate<T> {
    boolean belongsTo(T previous, ResultSet next) throws Exception;
}
```
By default EqualsPredicate is used, which maps next resultSet to T and compares it with previous one using equals(). You can also easily inject your own.
```java
List<AuthorWithBooks> authors = factory.newQuery().dsl()
    .select("a.*", Book.AUTHOR_ID, Book.TITLE, "b.id as bid").from(Author.TABLE).as("a")
    .innerJoin(Book.TABLE).as("b").on("a.id", "b.author_id")
    .orderBy(Author.NAME).desc()
    .query()
    //compares next row with previously mapped object only on the base of id
    .fetch(Mappings.listOfOneToMany((p, n) -> p.id == n.getInt("id"),
            Author.class, Book.class, AuthorWithBooks::new));
```
Something of greater complexity:
```java
AuthorWithBooksWithUsersWithPets complexAuthor = factory.newQuery().dsl()
    .select("a.*", "b.id as bid", Book.AUTHOR_ID, Book.TITLE, "u.id as uid", "u.name as uname",
        "p.user_id as p_id", "p.name as p_name")
    .from(Author.TABLE).as("a")
    .innerJoin(Book.TABLE).as("b").on("a.id", "b.author_id")
    .innerJoin(UserBook.TABLE).as("ub").on("b.id", "ub.book_id")
    .innerJoin(User.TABLE).as("u").on("ub.user_id", "u.id")
    .innerJoin(Pet.TABLE).as("p").on("u.id", "p.user_id")
    .where(Author.ALIAS).equal().value("LS")
    .query()
    //Author mapping, Book mapping, UserWithPet mapping
    .fetch(Mappings.biOneToMany(Author.class, Book.class, UserWithPet.class,
        //Group mapping, which is: Author, Map<Book, List<UserWithPet>>
        //Author <-1...n-Book <-1...n-User <-1-Pet
        (one, many) -> {
            List<BookWithUsersWithPets> books = many.entrySet().stream()
                .map(e -> new BookWithUsersWithPets(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
            return new AuthorWithBooksWithUsersWithPets(one, books);
        }));

List<AuthorWithBooksWithUsers> complexAuthors = factory.newQuery().dsl()
    .select("a.*", "b.id as bid", Book.AUTHOR_ID, Book.TITLE, "u.id as u_id", "u.name as u_name")
    .from(Author.TABLE).as("a")
    .innerJoin(Book.TABLE).as("b").on("a.id", "b.author_id")
    .innerJoin(UserBook.TABLE).as("ub").on("b.id", "ub.book_id")
    .innerJoin(User.TABLE).as("u").on("ub.user_id", "u.id")
    .orderBy(Author.NAME).desc()
    .query()
    //Author <-1...n-Book <-1...n-User
    .fetch(Mappings.listOfBiOneToMany(Author.class, Book.class, User.class,
        (one, many) -> {
            List<BookWithUsers> books = many.entrySet().stream()
                .map(e -> new BookWithUsers(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
            return new AuthorWithBooksWithUsers(one, books);
        }));
```
## Transactions
```java
new SmartQuery(connection)
    .sql("INSERT INTO user(name, surname, email) VALUES(?, ?, ?)")
    .set("Alan", "Turing", "alan_turing@email.com")
    .end()
    .sql("DELETE FROM user WHERE name != ?")
    .set("Alan")
    .end()
    .sql("UPDATE user SET name = ? WHERE name = ?")
    .set("Machine", "Alan")
    .executeTransaction();

new SmartQuery(connection)
    .dsl()
    .insertInto("user").columns("name", "surname", "email")
    .values("Alan", "Turing", "alan_turing@email.com")
    .query().end()
    .dsl()
    .delete("user").where("name").notEqual().value("Alan")
    .query().end()
    .dsl()
    .update("user").set("name", "Machine").where("name").equal().value("Alan")
    .query()
    .executeTransaction();
```
## Transparency
There is much more. You can create anything that is possible with a plain SQL. Results of your experiments can be easily seen.
```java
SmartQuery query = new SmartQuery(connection);
query.dsl()
    .selectAll().from("day").as("d").innerJoin("meal as m").on("d.id", "m.day_id")
    .innerJoin("meal_product as mp").on("m.id", "mp.meal_id")
    .innerJoin("product as p").on("mp.product_id", "p.id")
    .innerJoin("food as f").on("p.food_id", "f.id");
//Generated query template
System.out.println(query.template());
//To bind values
System.out.println(query.values());
```
## Active record
There is also an ActiveRecord base class, which can be used to create table specific extensions.
```java
package com.iprogrammerr.smart.query.example.active;

import com.iprogrammerr.smart.query.QueryFactory;
import com.iprogrammerr.smart.query.active.ActiveRecord;
import com.iprogrammerr.smart.query.active.UpdateableColumn;
import com.iprogrammerr.smart.query.example.table.Author;
import com.iprogrammerr.smart.query.mapping.Mappings;

public class AuthorRecord extends ActiveRecord<Integer, Author> {

    public AuthorRecord(QueryFactory factory, Integer id) {
        super(factory, Author.TABLE, new UpdateableColumn<>(Author.ID, id), Integer.class, true,
            new UpdateableColumn<>(Author.NAME), new UpdateableColumn<>(Author.ALIAS));
    }

    public AuthorRecord(QueryFactory factory) {
        this(factory, null);
    }

    @Override
    public Author fetch() {
        return fetchQuery().fetch(Mappings.ofClass(Author.class));
    }

    public AuthorRecord setName(String name) {
        set(Author.NAME, name);
        return this;
    }

    public AuthorRecord setAlias(String alias) {
        set(Author.ALIAS, alias);
        return this;
    }
}

QueryFactory factory = new SmartQueryFactory(db.source());
AuthorRecord record = new AuthorRecord(factory)
    .setName("Friedrich Nietzsche")
    .setAlias("Philosopher");
record.insert();

int id = record.getId();
Author author = record.fetch();

record.setName("Aristotle");
record.update();

record.delete();
```
You can use [Smart Query Meta](https://github.com/Iprogrammerr/Smart-Query-Meta) to generate both ActiveRecord extensions and tables representations.
## Configuration
Factory provides easy way to configure your queries.
```java
public class SmartQueryFactory implements QueryFactory {

    private final Callable<Connection> source;
    private final DialectTranslation translation;
    private final boolean closeConnections;

    public SmartQueryFactory(Callable<Connection> source, DialectTranslation translation, 
        boolean closeConnections) {
        this.source = source;
        this.translation = translation;
        this.closeConnections = closeConnections;
    }

    public SmartQueryFactory(Callable<Connection> source, boolean closeConnections) {
        this(source, DialectTranslation.DEFAULT, closeConnections);
    }

    public SmartQueryFactory(Callable<Connection> source) {
        this(source, true);
    }

    public SmartQueryFactory(DataSource source, DialectTranslation translation, boolean closeConnections) {
        this(source::getConnection, translation, closeConnections);
    }

    public SmartQueryFactory(DataSource source, boolean closeConnections) {
        this(source, DialectTranslation.DEFAULT, closeConnections);
    }

    public SmartQueryFactory(DataSource source) {
        this(source, true);
    }

    @Override
    public Query newQuery() {
        try {
            return new SmartQuery(source.call(), translation, closeConnections);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
```
DialectTranslation is a functional interface that is always called before creating PreparedStatement from a String. If you need to do custom query translations you can put this logic here. Most of the time it isn't needed, so there is a default implementation which does nothing.
## Contribution
I will highly appreciate bug reports and feature requests. Feel free to open a [new issue](https://github.com/Iprogrammerr/Smart-Query/issues/) or fork the repo and send me a [pull request](https://github.com/Iprogrammerr/Smart-Query/pulls).