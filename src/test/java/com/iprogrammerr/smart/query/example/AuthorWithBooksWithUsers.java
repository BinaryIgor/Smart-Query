package com.iprogrammerr.smart.query.example;

import com.iprogrammerr.smart.query.example.table.Author;

import java.util.List;
import java.util.Objects;

public class AuthorWithBooksWithUsers {

    public final Author author;
    public final List<BookWithUsers> books;

    public AuthorWithBooksWithUsers(Author author, List<BookWithUsers> books) {
        this.author = author;
        this.books = books;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthorWithBooksWithUsers that = (AuthorWithBooksWithUsers) o;
        return Objects.equals(author, that.author) &&
            Objects.equals(books, that.books);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, books);
    }

    @Override
    public String toString() {
        return "AuthorWithBooksWithUsers{" +
            "author=" + author +
            ", books=" + books +
            '}';
    }
}
