package com.iprogrammerr.smart.query.example.table;

import com.iprogrammerr.smart.query.mapping.clazz.Mapping;

import java.sql.ResultSet;
import java.util.Objects;

public class Book {

    public static final String TABLE = "BOOK";
    public static final String ID = "id";
    public static final String AUTHOR_ID = "author_id";
    public static final String TITLE = "title";

    @Mapping(labels = "bid")
    public final Integer id;
    @Mapping(labels = AUTHOR_ID)
    public final Integer authorId;
    public final String title;

    public Book(Integer id, Integer authorId, String title) {
        this.id = id;
        this.authorId = authorId;
        this.title = title;
    }

    public static Book fromResult(ResultSet result, String idLabel, String authorIdLabel, String titleLabel)
        throws Exception {
        Integer id = result.getInt(idLabel);
        Integer authorId = result.getInt(authorIdLabel);
        String title = result.getString(titleLabel);
        return new Book(id, authorId, title);
    }

    public static Book fromResult(ResultSet result) throws Exception {
        return fromResult(result, ID, AUTHOR_ID, TITLE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(id, book.id) &&
            Objects.equals(authorId, book.authorId) &&
            Objects.equals(title, book.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, authorId, title);
    }

    @Override
    public String toString() {
        return "Book{" +
            "id=" + id +
            ", authorId=" + authorId +
            ", title='" + title + '\'' +
            '}';
    }
}