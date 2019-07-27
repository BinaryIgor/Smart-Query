package com.iprogrammerr.smart.query.example;

import com.iprogrammerr.smart.query.example.table.Book;

import java.util.List;
import java.util.Objects;

public class BookWithUsersWithPets {

    public final Book book;
    public final List<UserWithPet> users;

    public BookWithUsersWithPets(Book book, List<UserWithPet> users) {
        this.book = book;
        this.users = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookWithUsersWithPets that = (BookWithUsersWithPets) o;
        return Objects.equals(book, that.book) &&
            Objects.equals(users, that.users);
    }

    @Override
    public int hashCode() {
        return Objects.hash(book, users);
    }

    @Override
    public String toString() {
        return "BookWithUsersWithPets{" +
            "book=" + book +
            ", users=" + users +
            '}';
    }
}
