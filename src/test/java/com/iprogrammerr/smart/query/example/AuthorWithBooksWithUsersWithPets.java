package com.iprogrammerr.smart.query.example;

import com.iprogrammerr.smart.query.example.table.Author;

import java.util.List;
import java.util.Objects;

public class AuthorWithBooksWithUsersWithPets {

    public final Author author;
    public final List<BookWithUsersWithPets> books;

    public AuthorWithBooksWithUsersWithPets(Author author, List<BookWithUsersWithPets> books) {
        this.author = author;
        this.books = books;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthorWithBooksWithUsersWithPets that = (AuthorWithBooksWithUsersWithPets) o;
        return Objects.equals(author, that.author) &&
            Objects.equals(books, that.books);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, books);
    }

    @Override
    public String toString() {
        return "AuthorWithBooksWithUsersWithPets{" +
            "author=" + author +
            ", books=" + books +
            '}';
    }
}
