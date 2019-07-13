[![Build Status](https://travis-ci.com/Iprogrammerr/Smart-Query.svg?branch=master)](https://travis-ci.com/Iprogrammerr/Smart-Query)
[![Test Coverage](https://img.shields.io/codecov/c/github/iprogrammerr/smart-query/master.svg)](https://codecov.io/gh/Iprogrammerr/Smart-Query/branch/master)
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
    .sql("DELETE from book WHERE = ?")
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
There is much more. You can create anything that is possible with plain SQL. Results of your experiments can be easily seen.
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
## Configuration
Using factory you can easily configure your queries.
```java
public class SmartQueryFactory implements QueryFactory {

    private final Callable<Connection> source;
    private final DialectTranslation translation;
    private final boolean closeConnections;

    public SmartQueryFactory(Callable<Connection> source, DialectTranslation translation, boolean closeConnections) {
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