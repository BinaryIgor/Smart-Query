package com.iprogrammerr.smart.query.example;

import com.iprogrammerr.smart.query.example.table.Author;
import com.iprogrammerr.smart.query.example.table.Book;
import com.iprogrammerr.smart.query.mapping.clazz.Embedded;
import com.iprogrammerr.smart.query.mapping.clazz.Mapping;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthorWithBook that = (AuthorWithBook) o;
        return booksCount == that.booksCount &&
            Objects.equals(author, that.author) &&
            Objects.equals(book, that.book);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, book, booksCount);
    }
}
