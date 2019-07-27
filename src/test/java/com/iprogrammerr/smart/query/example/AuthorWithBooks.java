package com.iprogrammerr.smart.query.example;

import java.util.List;
import java.util.Objects;

public class AuthorWithBooks {

    public final Author author;
    public final List<Book> books;

    public AuthorWithBooks(Author author, List<Book> books) {
        this.author = author;
        this.books = books;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthorWithBooks that = (AuthorWithBooks) o;
        return Objects.equals(author, that.author) &&
            Objects.equals(books, that.books);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, books);
    }
}
